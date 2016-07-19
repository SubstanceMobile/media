/*
 * Copyright 2016 Substance Mobile
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mobile.substance.sdk.music.playback.service

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserServiceCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.cast.framework.CastState
import com.google.android.gms.cast.framework.CastStateListener
import mobile.substance.sdk.music.playback.MusicPlaybackOptions
import mobile.substance.sdk.music.playback.service.MusicQueue
import mobile.substance.sdk.music.playback.PlaybackRemote
import mobile.substance.sdk.music.playback.players.CastPlayback
import mobile.substance.sdk.music.playback.players.LocalPlayback
import mobile.substance.sdk.music.playback.players.Playback
import java.util.*

class MusicService : MediaBrowserServiceCompat(), CastStateListener {

    override fun onCastStateChanged(p0: Int) {
        if (p0 == CastState.CONNECTED) replacePlaybackEngine(CastPlayback, engine.isPlaying() || engine.getCurrentPosInSong() > 0, true)
    }

    //Companion object for the unique ID and log tag.
    companion object {
        // Log Tag
        val TAG: String by lazy {
            MusicService::class.java.simpleName
        }

        // Service's Unique ID.
        val UNIQUE_ID: Int by lazy {
            Random().nextInt(1000000)
        }
    }

    private var notificationManager: NotificationManager? = null

    // Media Session
    private var session: MediaSessionCompat? = null

    private var CALLBACKS = ArrayList<PlaybackRemote.RemoteCallback>()

    fun registerCallback(callback: PlaybackRemote.RemoteCallback) {
        CALLBACKS.add(callback)
    }

    fun unregisterCallback(callback: PlaybackRemote.RemoteCallback) {
        if (CALLBACKS.contains(callback)) CALLBACKS.remove(callback)
    }

    ///////////////////////////////////////////////////////////////////////////
    // Playback Engine
    ///////////////////////////////////////////////////////////////////////////

    var engine: Playback = LocalPlayback

    internal fun replacePlaybackEngine(newEngine: Playback, hotswap: Boolean, trustedSource: Boolean = false) {
        var oldPos = 0
        var wasPlaying = false

        if (!trustedSource) Log.d(TAG, "DANGEROUS: Replacing playback engine with custom code. Make sure you debug extensively") else Log.d(TAG, "Service is requesting to replace engines internally. Hotswapping players")
        if (hotswap) {
            //Since we are hotswapping we need to save some data
            if (engine.isPlaying()) {
                engine.pause()
                wasPlaying = true
            }
            oldPos = engine.getCurrentPosInSong()
            Log.d(TAG, "Current state is saved. Cleaning up old engine")
        }
        engine.stop()
        Log.d(TAG, "Current engine is now stopped")
        engine = newEngine
        engine.init(this)
        session?.setCallback(newEngine)
        Log.d(TAG, "Engine is set. Restoring previous properties = $hotswap")
        if (hotswap) {
            //Time to hotswap some data back in so as far as the user is concerned the engine is the same. In fact, the song they are currently listening to will resume
            if (!trustedSource) Log.d(TAG, "Since you are hotswapping players, this might be a setting that the user can change. If this is the case please display some sort of confirmation to the user that the option has actually changed because they will most likely not be able to notice the change in engine (since you are hotswapping")
            //Tells the engine that it should wait until it's actually playing before executing further playback tasks (Only one's that control the actual state, e.g. pause/resume, seek, stop). This is done in favor of asynchronous playbacks, e.g. CastPlayback
            //We mustn't & don't want to have the Service slowing down the UIThread in order to await async operations. How do we handle this? Have a look at the "pendingCalls" variable and the nowPlaying() function in Playback.kt
            engine.inHotswapTransaction = true
            //Resume the current queue
            engine.play()
            //Seek back to where we were before
            engine.seek(oldPos.toLong())
            //Once we restored all of the old properties we pause the playback again if it was already paused
            if (!wasPlaying) engine.pause()
            Log.d(TAG, "State restored")
        }
        Log.d(TAG, "Engine transaction complete")
    }

    ///////////////////////////////////////////////////////////////////////////
    // Initialization
    ///////////////////////////////////////////////////////////////////////////

    private fun init() {
        HeadsetPlugReceiver register this

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        session = MediaSessionCompat(this, MusicService::class.java.simpleName)
        session!!.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)

        session!!.setCallback(engine)
        sessionToken = session!!.sessionToken

        if (MusicPlaybackOptions.isCastEnabled) {
            val instance = CastContext.getSharedInstance(this)
            instance.addCastStateListener(this)
            if (instance.sessionManager.currentCastSession != null && instance.sessionManager.currentCastSession.isConnected)
                replacePlaybackEngine(CastPlayback, false, true)
        }

        engine.init(this)
    }

    ///////////////////////////////////////////////////////////////////////////
    // Progress Thread
    ///////////////////////////////////////////////////////////////////////////

    private var progressThread: ProgressThread? = null

    inner class ProgressThread : Thread() {
        override fun run() {
            while (!interrupted()) {
                try {
                    sleep(500)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                    interrupt()
                }
                try {
                    for (CALLBACK in CALLBACKS) {
                        CALLBACK.onProgressChanged(engine.getCurrentPosInSong())
                        CALLBACK.onStateChanged(
                                if (engine.isPlaying())
                                    PlaybackState.STATE_PLAYING
                                else if (MusicQueue.getCurrentSong() != null)
                                    PlaybackState.STATE_PAUSED
                                else PlaybackState.STATE_IDLE,
                                engine.isRepeating())
                    }
                    session?.setMetadata(MusicQueue.getCurrentSong()!!.metadata)
                    session?.setPlaybackState(
                            PlaybackStateCompat.Builder().setActions(MusicPlaybackOptions.playbackActions.getActions())
                                    .setState(engine.playbackState, engine.getCurrentPosInSong().toLong(), engine.getPlaybackSpeed())
                                    .build())
                } catch (e: Exception) {
                    e.printStackTrace()
                    interrupt()
                }
            }
        }
    }

    fun startProgressThread() {
        if (progressThread == null)
            progressThread = ProgressThread()
        progressThread?.start()
    }

    fun shutdownProgressThreadIfNecessary() {
        if (progressThread == null) return
        if (!(progressThread?.isInterrupted ?: false))
            progressThread?.interrupt()
        progressThread = null
    }

    ///////////////////////////////////////////////////////////////////////////
    // MediaSession
    ///////////////////////////////////////////////////////////////////////////

    private fun destroySession() {
        session?.isActive = false
        session?.release()
        session = null
    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot? = null //We don't use this

    override fun onLoadChildren(parentId: String, result: Result<List<MediaBrowserCompat.MediaItem>>) {

    } // We don't use this

    fun control() = session?.controller

    fun getMediaSession() = session

    ///////////////////////////////////////////////////////////////////////////
    // Lifecycle
    ///////////////////////////////////////////////////////////////////////////

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate()")
        init()
    }

    override fun onDestroy() {
        super.onDestroy()
        HeadsetPlugReceiver unregister this
        destroySession()
        if (MusicPlaybackOptions.isCastEnabled) CastContext.getSharedInstance(this).removeCastStateListener(this)
    }

    fun startForeground() = startForeground(UNIQUE_ID, PlaybackRemote.makeNotification(object : PlaybackRemote.NotificationUpdateInterface {
        override fun updateNotification(notification: Notification): Unit = updateNotification(notif = notification)
    }))

    fun updateNotification(notif: Notification) = notificationManager!!.notify(UNIQUE_ID, notif)

    fun kill() {
        destroySession()
        stopForeground(true)
        stopSelf()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand()")
        for (callback in CALLBACKS)
            callback.onReceivedIntent(intent)
        return Service.START_NOT_STICKY
    }

    ///////////////////////////////////////////////////////////////////////////
    // Binding
    ///////////////////////////////////////////////////////////////////////////

    private val binder = ServiceBinder()

    inner class ServiceBinder : Binder() {
        val service: MusicService
            get() = this@MusicService
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.d(TAG, "onBind()")
        return binder
    }

    override fun onUnbind(intent: Intent) = true

}