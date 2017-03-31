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

package mobile.substance.media.music.playback.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.*
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserServiceCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.cast.framework.CastState
import com.google.android.gms.cast.framework.CastStateListener
import mobile.substance.media.music.playback.PlaybackRemote
import mobile.substance.media.music.playback.destroy
import mobile.substance.media.music.playback.players.CastPlayback
import mobile.substance.media.music.playback.players.GaplessPlayback
import mobile.substance.media.music.playback.players.LocalPlayback
import mobile.substance.media.music.playback.players.Playback
import mobile.substance.media.options.AudioPlaybackOptions
import mobile.substance.media.utils.AudioCoreUtil
import java.util.*
import kotlin.concurrent.thread

open class AudioService : MediaBrowserServiceCompat(), CastStateListener {

    override fun onCastStateChanged(p0: Int) {
        when (p0) {
            CastState.CONNECTED -> replacePlaybackEngine(CastPlayback, PlaybackRemote.isActive() && (engine.isPlaying() || engine.getCurrentPosition() > 0), true)
        }
    }

    //Companion object for the unique ID and log tag.
    companion object {
        // Log Tag
        val TAG: String by lazy {
            AudioService::class.java.simpleName
        }

        // Service's Unique ID.
        val UNIQUE_ID: Int by lazy {
            Random().nextInt(1000000)
        }
    }

    private var notificationManager: NotificationManager? = null

    // MediaCore Session
    private var session: MediaSessionCompat? = null

    private var CALLBACKS = ArrayList<PlaybackRemote.RemoteCallback>()

    fun registerCallback(callback: PlaybackRemote.RemoteCallback) {
        CALLBACKS.add(callback)
    }

    fun unregisterCallback(callback: PlaybackRemote.RemoteCallback) {
        if (CALLBACKS.contains(callback)) CALLBACKS.remove(callback)
    }

    fun update(callback: PlaybackRemote.RemoteCallback) {
        val song = AudioQueue.getCurrentSong()!!
        callback.onSongChanged(song)
        callback.onDurationChanged(song.duration?.toInt() ?: 0, song.formattedDuration)
        callback.onStateChanged(engine.playbackState)
        callback.onRepeatModeChanged(engine.repeatMode)
    }

    ///////////////////////////////////////////////////////////////////////////
    // Playback Engine
    ///////////////////////////////////////////////////////////////////////////

    internal fun callback(call: PlaybackRemote.RemoteCallback.() -> Any) {
        for (CALLBACK in CALLBACKS) call.invoke(CALLBACK)
    }

    var engine: Playback = if (AudioPlaybackOptions.isGaplessPlaybackEnabled) GaplessPlayback else LocalPlayback

    internal fun replacePlaybackEngine(newEngine: Playback, hotSwap: Boolean, trustedSource: Boolean = false) {
        var oldPos = 0
        var wasPlaying = false

        if (!trustedSource) Log.d(TAG, "DANGEROUS: Replacing playback engine with custom code. Make sure you debug extensively") else Log.d(TAG, "Service is requesting to replace engines internally. Hotswapping players")
        if (hotSwap) {
            //Since we are hotswapping we need to save some data
            if (engine.isPlaying()) {
                engine.pause()
                wasPlaying = true
            }
            oldPos = engine.getCurrentPosition()
            Log.d(TAG, "Current state is saved. Cleaning up old engine")
        }
        engine.stop()
        Log.d(TAG, "Current engine is now stopped")
        engine = newEngine
        engine.init(this)
        session?.setCallback(newEngine)
        Log.d(TAG, "Engine is set. Restoring previous properties = $hotSwap")
        if (hotSwap) {
            //Time to hotswap some data back in so as far as the user is concerned the engine is the same. In fact, the song they are currently listening to will resume
            if (!trustedSource) Log.d(TAG, "Since you are hotswapping players, this might be a setting that the user can change. If this is the case please display some sort of confirmation to the user that the option has actually changed because they will most likely not be able to notice the change in engine (since you are hotswapping")
            //Tells the engine that it should wait until it's actually playing before executing further playback tasks (Only one's that control the actual state, e.g. pause/resume, seek, stop). This is done in favor of asynchronous playbacks, e.g. CastPlayback
            //We mustn't & don't want to have the Service slowing down the UIThread in order to await async operations. How do we handle this? Have a look at the "pendingCalls" variable and the notifyPlaying() function in Playback.kt
            engine.isInHotSwapTransaction = true
            //Resume the current queue
            engine.play()
            //Seek back to where we were before
            engine.seek(oldPos.toLong())
            //Once we restored all of the old properties we pause the playback again if it was already paused
            if (!wasPlaying) engine.pause()
            Log.d(TAG, "State restored")
        }

        // Reset old values that become deprecated with a new engine
        callback { onRepeatModeChanged(PlaybackStateCompat.REPEAT_MODE_NONE) }

        Log.d(TAG, "Engine transaction complete")
    }

    ///////////////////////////////////////////////////////////////////////////
    // Initialization
    ///////////////////////////////////////////////////////////////////////////

    private fun init() {
        HeadsetPlugReceiver register this
        AudioBecomingNoisyReceiver register this

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        session = MediaSessionCompat(this, this.javaClass.simpleName)
        session!!.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
        session!!.setSessionActivity(PendingIntent.getActivity(this, UNIQUE_ID, packageManager.getLaunchIntentForPackage(applicationContext.packageName), PendingIntent.FLAG_CANCEL_CURRENT))
        session!!.setCallback(engine)
        sessionToken = session!!.sessionToken

        if (AudioPlaybackOptions.defaultCallback != null) registerCallback(AudioPlaybackOptions.defaultCallback!!)

        if (AudioPlaybackOptions.isCastEnabled) {
            val instance = CastContext.getSharedInstance(this)
            instance.addCastStateListener(this)
            if (instance.sessionManager.currentCastSession != null && instance.sessionManager.currentCastSession.isConnected)
                replacePlaybackEngine(CastPlayback, false, true)
        }

        engine.init(this)
    }

    internal fun updatePlaybackState(callback: Boolean = false, progress: Long = engine.getCurrentPosition().toLong()) {
        val playbackState = engine.playbackState
        val repeatMode = engine.repeatMode
        session?.setPlaybackState(
                PlaybackStateCompat.Builder()
                        .setActions(AudioPlaybackOptions.playbackActions.getActions())
                        .setState(playbackState or repeatMode, progress, engine.getPlaybackSpeed())
                        .build())
        if (callback) {
            callback {
                onStateChanged(playbackState)
                if (playbackState == PlaybackStateCompat.STATE_ERROR) onError()
                if (playbackState == PlaybackStateCompat.STATE_BUFFERING) onBufferStarted()
            }
        }
    }

    internal fun updatePlaybackProgress(progress: Long? = null) {
        updatePlaybackState(progress = progress ?: engine.getCurrentPosition().toLong())
        Handler(Looper.getMainLooper()).post { callback { onProgressChanged(progress?.toInt() ?: engine.getCurrentPosition()) } }
    }

    internal fun updateMetadata() = thread {
        val source = AudioQueue.getCurrentSong()?.getMetadata()
        val metadataCompat = MediaMetadataCompat.Builder(source)
        if (AudioPlaybackOptions.isLockscreenArtworkEnabled) {
            metadataCompat.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART,
                    AudioCoreUtil.getArtwork(AudioQueue.getCurrentSong()!!, this, AudioPlaybackOptions.isLockscreenArtworkBlurEnabled))
        }
        session?.setMetadata(metadataCompat.build())
    }

    ///////////////////////////////////////////////////////////////////////////
    // MediaSession
    ///////////////////////////////////////////////////////////////////////////

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot? = null // We don't use this

    override fun onLoadChildren(parentId: String, result: Result<List<MediaBrowserCompat.MediaItem>>) {

    } // We don't use this

    fun control() = session?.controller

    fun getMediaSession() = session

    ///////////////////////////////////////////////////////////////////////////
    // Lifecycle
    ///////////////////////////////////////////////////////////////////////////

    override fun onCreate() {
        super.onCreate()
        init()
    }

    override fun onDestroy() {
        super.onDestroy()
        HeadsetPlugReceiver unregister this
        AudioBecomingNoisyReceiver unregister this
        session?.destroy()
        try {
            if (AudioPlaybackOptions.defaultCallback != null) unregisterCallback(AudioPlaybackOptions.defaultCallback!!)
        } catch (ignored: Exception) {
        }
        if (AudioPlaybackOptions.isCastEnabled) CastContext.getSharedInstance(this).removeCastStateListener(this)
    }

    fun startForeground() = startForeground(UNIQUE_ID, PlaybackRemote.makeNotification(object : PlaybackRemote.NotificationUpdateInterface {
        override fun updateNotification(notification: Notification): Unit = notify(notification = notification)
    }))

    fun notify(notification: Notification) = notificationManager!!.notify(UNIQUE_ID, notification)

    fun kill() {
        session?.destroy()
        stopForeground(true)
        stopSelf()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (intent.action != null) {
            val action = intent.action
            when {
                action.endsWith("NOTIFICATION") -> startActivity(packageManager.getLaunchIntentForPackage(applicationContext.packageName))
                action.endsWith("PLAY") -> PlaybackRemote.resume()
                action.endsWith("PAUSE") -> PlaybackRemote.pause()
                action.endsWith("SKIP_FORWARD") -> PlaybackRemote.playNext()
                action.endsWith("SKIP_BACKWARD") -> PlaybackRemote.playPrevious()
                action.endsWith("NOTIFICATION") -> PlaybackRemote.notificationCreator.onNotificationClicked()
                action.endsWith("SEEK") -> PlaybackRemote.seekTo(intent.getIntExtra("progress", 0))
                action.endsWith("STOP") -> {
                    engine.stop()
                    PlaybackRemote.notificationCreator.onNotificationDismissed()
                }
                else -> callback { onReceivedIntent(intent) }
            }
        }

        return Service.START_NOT_STICKY
    }

    ///////////////////////////////////////////////////////////////////////////
    // Binding
    ///////////////////////////////////////////////////////////////////////////

    private val binder = ServiceBinder()

    inner class ServiceBinder : Binder() {
        val service: AudioService
            get() = this@AudioService
    }

    override fun onBind(intent: Intent): IBinder? {
        return binder
    }

    override fun onUnbind(intent: Intent) = true

}