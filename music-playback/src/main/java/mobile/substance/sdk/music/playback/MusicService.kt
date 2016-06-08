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

package mobile.substance.sdk.music.playback

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.net.Uri
import android.os.*
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserServiceCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.view.MenuItemCompat
import android.support.v7.app.MediaRouteActionProvider
import android.support.v7.media.MediaRouteSelector
import android.support.v7.media.MediaRouter
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.google.android.gms.cast.Cast
import com.google.android.gms.cast.CastDevice
import com.google.android.gms.cast.CastMediaControlIntent
import com.google.android.gms.common.api.GoogleApiClient
import mobile.substance.sdk.music.core.CoreUtil
import mobile.substance.sdk.music.playback.cast.*
import mobile.substance.sdk.music.playback.players.Playback
import java.util.*

class MusicService : MediaBrowserServiceCompat(), CastCallbacks {
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
    var playback: Playback = LocalPlayback

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
    // Initialization
    ///////////////////////////////////////////////////////////////////////////

    private fun init() {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        session = MediaSessionCompat(this, MusicService::class.java.simpleName)
        session!!.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
        playback.init(this)
        session!!.setCallback(playback)
        sessionToken = session!!.sessionToken
    }

    ///////////////////////////////////////////////////////////////////////////
    // Progress Thread
    ///////////////////////////////////////////////////////////////////////////

    private var progressThread: Thread? = null

    fun startProgressThread() {
        progressThread = object : Thread() {
            override fun run() {
                while (!Thread.interrupted()) {
                    try {
                        Thread.sleep(500)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                        interrupt()
                    }
                    try {
                        for (CALLBACK in CALLBACKS) {
                            CALLBACK.onProgressChanged(playback.getCurrentPosInSong())
                            CALLBACK.onStateChanged(
                                    if (playback.isPlaying())
                                        PlaybackState.STATE_PLAYING
                                    else if (MusicQueue.getCurrentSong() != null)
                                        PlaybackState.STATE_PAUSED
                                    else PlaybackState.STATE_IDLE,
                                    playback.isRepeating())
                        }
                        session!!.setMetadata(MusicQueue.getCurrentSong()!!.metadataCompat)
                        session!!.setPlaybackState(
                                PlaybackStateCompat.Builder().setActions(MusicPlaybackOptions.playbackActions.getActions())
                                        .setState(playback.playbackState, playback.getCurrentPosInSong().toLong(), playback.getPlaybackSpeed())
                                        .build())
                    } catch (e: Exception) {
                        e.printStackTrace()
                        interrupt()
                    }
                }
            }
        }
        progressThread!!.start()
    }

    fun shutdownProgressThread() {
        if (progressThread != null) {
            progressThread!!.interrupt()
            progressThread = null
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // MediaSession
    ///////////////////////////////////////////////////////////////////////////

    private fun destroySession() {
        session!!.isActive = false
        session!!.release()
        session = null
    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): MediaBrowserServiceCompat.BrowserRoot? = null //We don't use this

    override fun onLoadChildren(parentId: String, result: MediaBrowserServiceCompat.Result<List<MediaBrowserCompat.MediaItem>>) {

    } // We don't use this

    fun control() = session?.controller

    fun getMediaSession() = session

    ///////////////////////////////////////////////////////////////////////////
    // Google Cast functions
    ///////////////////////////////////////////////////////////////////////////

    private var apiClient: GoogleApiClient? = null
    private var castPlaybackHandler: CastPlaybackHandler? = null
    private val isCastInitialized = false
    private var applicationId: String? = null
    @Volatile private var mediaRouter: MediaRouter? = null
    @Volatile private var routeSelector: MediaRouteSelector? = null
    private var connectionCallbacks: ConnectionCallbacks? = null
    private var routerCallback: MediaRouterCallback? = null

    fun initGoogleCast(routeItem: MenuItem, applicationId: String) {
        this.applicationId = applicationId

        mediaRouter = MediaRouter.getInstance(applicationContext)
        routeSelector = MediaRouteSelector.Builder().addControlCategory(CastMediaControlIntent.categoryForCast(applicationId)).build()

        val routeActionProvider = MenuItemCompat.getActionProvider(routeItem) as MediaRouteActionProvider
        routeActionProvider.routeSelector = routeSelector!!
        routerCallback = MediaRouterCallback(this)
        mediaRouter!!.addCallback(routeSelector!!, routerCallback!!,
                MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY)
    }

    override fun onCastDeviceSelected(mDevice: CastDevice) {
        launchCast(mDevice)
    }

    override fun onCastDeviceUnselected() {
        progressThread!!.interrupt()
        progressThread = null
    }

    private val isCastConnected: Boolean
        get() = castPlaybackHandler != null && castPlaybackHandler!!.isConnected

    private fun launchCast(device: CastDevice) {
        val listener = Cast.Listener()
        val apiOptionsBuilder = Cast.CastOptions.Builder(device, listener)

        connectionCallbacks = ConnectionCallbacks(routerCallback!!, ConnectionResultListener { castPlaybackHandler!!.load(playback.getCurrentPosInSong().toLong()) })

        apiClient = GoogleApiClient.Builder(this).addApi(Cast.API, apiOptionsBuilder.build()).addConnectionCallbacks(connectionCallbacks!!).addOnConnectionFailedListener { Toast.makeText(this@MusicService, "Connection failed", Toast.LENGTH_LONG).show() }.build()
        connectionCallbacks!!.setApiClient(apiClient!!)
        castPlaybackHandler = CastPlaybackHandler(this, apiClient!!)
        apiClient!!.connect()
    }

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
    }

    fun startForeground() = startForeground(UNIQUE_ID, PlaybackRemote.makeNotification(object : PlaybackRemote.NotificationUpdateInterface {
        override fun updateNotification(notification: Notification): Unit = updateNotification(notification)
    }))

    fun updateNotification(notification: Notification) = notificationManager!!.notify(UNIQUE_ID, notification)

    fun kill() {
        destroySession()
        stopForeground(true)
        stopSelf()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand()")
        for (callback in CALLBACKS) {
            callback.onReceivedIntent(intent)
        }
        return Service.START_STICKY
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