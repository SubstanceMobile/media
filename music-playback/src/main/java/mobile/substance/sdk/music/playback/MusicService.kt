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
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.os.PowerManager
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserServiceCompat
import android.support.v4.media.MediaMetadataCompat
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
import mobile.substance.sdk.music.core.MusicCoreOptions
import mobile.substance.sdk.music.core.objects.Song
import mobile.substance.sdk.music.loading.Library
import mobile.substance.sdk.music.playback.cast.*
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
    var playback : Playback = LocalPlayback

    // Media Session
    var session: MediaSessionCompat? = null
        private set

    // Playback Progress
    private val progressUpdate = Runnable {
        for (CALLBACK in CALLBACKS) {
            CALLBACK.onProgressChanged(playback.getCurrentPosInSong())
        }
        session!!.setPlaybackState(playbackState)
    };
    private var progressThread: Thread? = null

    private var CALLBACKS = ArrayList<PlaybackRemote.RemoteCallback>()

    fun registerCallback(callback: PlaybackRemote.RemoteCallback) {
        CALLBACKS.add(callback)
    }

    fun unregisterCallback(callback: PlaybackRemote.RemoteCallback) {
        if (CALLBACKS.contains(callback)) CALLBACKS.remove(callback)
    }

    object LocalPlayback : Playback(), MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, AudioManager.OnAudioFocusChangeListener {
        val TAG = "LocalPlayback"
        private var localPlayer: MediaPlayer = MediaPlayer()
        private var audioManager: AudioManager? = null
        private var wasPlayingBeforeAction = false

        override fun init() {
            //Get the audio manager
            audioManager = SERVICE!!.getSystemService(Context.AUDIO_SERVICE) as AudioManager

            //Media player
            localPlayer.setWakeMode(SERVICE, PowerManager.PARTIAL_WAKE_LOCK)
            localPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
            localPlayer.setOnPreparedListener(this)
            localPlayer.setOnCompletionListener(this)
            localPlayer.setOnErrorListener(this)
        }

        ///////////////////////////////////////////////////////////////////////////
        // Play
        ///////////////////////////////////////////////////////////////////////////

        override fun play(uri: Uri, listenersAlreadyNotified: Boolean) {
            //Clear out the media player if a song is being played right now.
            if (isPlaying()) {
                localPlayer.stop()
                localPlayer.reset()
                Log.d(TAG, "play: player reset complete")
            }

            //Register the broadcast receiver
            HeadsetPlugReceiver register SERVICE!!
            Log.d(TAG, "play: HeadsetPlugReceiver registered")

            //Start the service and do some work!
            try {
                localPlayer.setDataSource(SERVICE!!.applicationContext, uri)
                Log.d(TAG, "play: source set")
            } catch (e: Exception) {
                Log.e(TAG, "Unable to play " + CoreUtil.getFilePath(SERVICE, uri), e)
            } finally {
                localPlayer.prepareAsync()
                Log.d(TAG, "play: Prepare called")
            }
        }

        override fun play() = play(MusicQueue.getCurrentSong()!!)

        override fun play(song: Song) = play(song.uri!!, false)

        override fun resume() = resume(true)

        fun resume(updateFocus: Boolean) {
            if (!isPlaying()) {
                if (!updateFocus) requestAudioFocus()
                SERVICE!!.startProgressThread()
                localPlayer.start()
                SERVICE!!.startForeground(PlaybackRemote.getCurrentSong())
            } else Log.e(TAG, "It seems like resume was called while you are playing. It is recommended you do some debugging.")
        }

        ///////////////////////////////////////////////////////////////////////////
        // Paused/Stopped states
        ///////////////////////////////////////////////////////////////////////////

        override fun pause() = pause(true)

        fun pause(updateFocus: Boolean) {
            if (updateFocus) giveUpAudioFocus()
            localPlayer.pause()
            HeadsetPlugReceiver unregister SERVICE!!
        }

        override fun stop() = stop(true)

        fun stop(updateFocus: Boolean) {
            throw UnsupportedOperationException()
        }

        ///////////////////////////////////////////////////////////////////////////
        // Other controls
        ///////////////////////////////////////////////////////////////////////////

        override fun next() = PlaybackRemote.playNext()

        override fun doPrev() = PlaybackRemote.playPrevious()

        override fun restart() = seek(0)

        override fun seek(time: Long) {
            val to = time.toInt()
            wasPlayingBeforeAction = isPlaying()
            localPlayer.pause()
            if (to >= localPlayer.duration) localPlayer.seekTo(to) else next()
            if (wasPlayingBeforeAction) localPlayer.start()
        }

        override fun repeat(repeating: Boolean) {
            localPlayer.isLooping = repeating
        }

        ///////////////////////////////////////////////////////////////////////////
        // Misc.
        ///////////////////////////////////////////////////////////////////////////

        override fun isPlaying() = localPlayer.isPlaying

        override fun isRepeating() = localPlayer.isLooping

        override fun isInitialized() = SERVICE != null

        override fun getCurrentPosInSong() = localPlayer.currentPosition

        ///////////////////////////////////////////////////////////////////////////
        // Media Player Callbacks
        ///////////////////////////////////////////////////////////////////////////

        override fun onPrepared(mp: MediaPlayer?) {
            if (requestAudioFocus()) {
                resume()
                SERVICE!!.requestUpdate()
            } else Log.e(TAG, "onPrepared(): DENIED AUDIOFOCUS")
        }

        override fun onCompletion(mp: MediaPlayer?) = if (!isRepeating()) next() else restart()

        //TODO
        override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean = false

        ///////////////////////////////////////////////////////////////////////////
        // AudioFocus
        ///////////////////////////////////////////////////////////////////////////

        fun requestAudioFocus(): Boolean = when (audioManager!!.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)) {
            AudioManager.AUDIOFOCUS_REQUEST_GRANTED -> true
            else -> false
        }

        fun giveUpAudioFocus() = audioManager!!.abandonAudioFocus(this)

        override fun onAudioFocusChange(focusChange: Int) {
            when (focusChange) {
                AudioManager.AUDIOFOCUS_GAIN -> {
                    if (wasPlayingBeforeAction) resume(false)
                    adjustVolume(false)
                }
                AudioManager.AUDIOFOCUS_LOSS -> if (isPlaying()) pause(false)
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> if (isPlaying()) {
                    wasPlayingBeforeAction = true
                    pause(false)
                }
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                    adjustVolume(true)
                }
            }
        }

        private fun adjustVolume(duck: Boolean) {
            try {
                val volume = if (duck) 0.5f else 1.0f
                localPlayer.setVolume(volume, volume)
            } catch (ignored: Exception) {
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Playback data
    ///////////////////////////////////////////////////////////////////////////

    private val metadata: MediaMetadataCompat
        get() {
            val song = MusicQueue.getCurrentSong()
            song!!.embedArtwork(BitmapFactory.decodeFile(Library.findAlbumById(song.getSongAlbumID())!!.albumArtworkPath))
            return song.getMetadataCompat()
        }

    private val playbackState: PlaybackStateCompat
        get() {
            return PlaybackStateCompat.Builder()
                    .setActions(MusicPlaybackOptions.playbackActions.getActions())
                    .setState(playback.playbackState,
                            playback.getCurrentPosInSong().toLong(),
                            playback.getPlaybackSpeed())
                    .build()
        }

    ///////////////////////////////////////////////////////////////////////////
    // Playback methods
    ///////////////////////////////////////////////////////////////////////////

    private fun updatePlaybackState() {
        var state: PlaybackState = PlaybackState.STATE_IDLE
        if (playback.isPlaying()) state = PlaybackState.STATE_PLAYING else if (MusicQueue.getCurrentSong() != null) state = PlaybackState.STATE_PAUSED
        for (CALLBACK in CALLBACKS) CALLBACK.onStateChanged(state, playback.isRepeating())
    }

    fun requestUpdate() {
        updatePlaybackState()
        updateSong()
    }

    private fun updateSong() {
        for (CALLBACK in CALLBACKS) {
            CALLBACK.onSongChanged(MusicQueue.getCurrentSong()!!)
        }
    }

    val isLocalPlaying: Boolean
        get() {
            try {
                return localPlayer!!.isPlaying
            } catch (e: Exception) {
                return false
            }
        }

    ///////////////////////////////////////////////////////////////////////////
    // Initialization
    ///////////////////////////////////////////////////////////////////////////

    private fun init() {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        session = MediaSessionCompat(this, MusicService::class.java.simpleName)
        session!!.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
        session!!.setCallback(playback)
        sessionToken = session!!.sessionToken
    }

    val isCastPlaying: Boolean
        get() = isCastConnected && castPlaybackHandler!!.isPlaying

    val currentPosition: Int

    val currentDuration: Int
        get() {
            try {
                return if (isCastPlaying) castPlaybackHandler!!.currentDuration else localPlayer!!.duration
            } catch (e: Exception) {
                e.printStackTrace()
                return 0
            }

        }


    fun pause() {
        try {
            if (localPlayer!!.isPlaying) {
                localPlayer!!.pause()
                shutdownProgressThread()
                startForeground(UNIQUE_ID, MusicNotification.create(this, false, session!!))
                stopForeground(false)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun skipForward() {
        if (!isRepeating) {
            PlaybackRemote.playNext()
        } else
            play()
    }

    private fun skipBackward() {
        if (currentDuration > 5000 && currentPosition > 5000) {
            play()
            return
        }
        isRepeating = false
        PlaybackRemote.playPrevious()
    }

    private fun startProgressThread() {
        initProgressThread()
        progressThread!!.start()
    }

    private fun shutdownProgressThread() {
        if (progressThread != null) {
            progressThread!!.interrupt()
            progressThread = null
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // MediaPlayer Callbacks
    ///////////////////////////////////////////////////////////////////////////

    override fun onError(mp: MediaPlayer, what: Int, extra: Int): Boolean {
        try {
            progressThread!!.interrupt()
            progressThread = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return true
    }

    ///////////////////////////////////////////////////////////////////////////
    // AudioManager functions
    ///////////////////////////////////////////////////////////////////////////

    private fun initProgressThread() {
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
                        progressUpdate.run()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        interrupt()
                    }
                }
            }
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

    private inner class SessionCallback : MediaSessionCompat.Callback() {

        override fun onMediaButtonEvent(mediaButtonEvent: Intent?): Boolean {
            return super.onMediaButtonEvent(mediaButtonEvent)
        }

        override fun onStop() {
            // kill()
        }

        override fun onSeekTo(pos: Long) {
            seekTo(Math.round(pos.toFloat()))
        }

        override fun onPause() {
            pause()
        }

        override fun onPlay() {
            resume()
        }

        override fun onSkipToNext() {
            skipForward()
        }

        override fun onSkipToPrevious() {
            skipBackward()
        }

        override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
            super.onPlayFromMediaId(mediaId, extras)
        }

        override fun onPlayFromSearch(query: String?, extras: Bundle?) {
            super.onPlayFromSearch(query, extras)
        }

        override fun onPlayFromUri(uri: Uri?, extras: Bundle?) {
            super.onPlayFromUri(uri, extras)
        }
    }


    class CastPlayback : Playback() {
        override fun init() {
            throw UnsupportedOperationException()
        }

        override fun play() {
            throw UnsupportedOperationException()
        }

        override fun play(uri: Uri, notifyPlaybackRemote: Boolean) {
            throw UnsupportedOperationException()
        }

        override fun play(song: Song) {
            throw UnsupportedOperationException()
        }

        override fun resume() {
            throw UnsupportedOperationException()
        }

        override fun pause() {
            throw UnsupportedOperationException()
        }

        override fun next() {
            throw UnsupportedOperationException()
        }

        override fun doPrev() {
            throw UnsupportedOperationException()
        }

        override fun restart() {
            throw UnsupportedOperationException()
        }

        override fun stop() {
            throw UnsupportedOperationException()
        }

        override fun seek(time: Long) {
            throw UnsupportedOperationException()
        }

        override fun repeat(repeating: Boolean) {
            throw UnsupportedOperationException()
        }

        override fun isPlaying(): Boolean {
            throw UnsupportedOperationException()
        }

        override fun isRepeating(): Boolean {
            throw UnsupportedOperationException()
        }

        override fun isInitialized(): Boolean {
            throw UnsupportedOperationException()
        }

        override fun getCurrentPosInSong(): Int {
            throw UnsupportedOperationException()
        }

    }

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

        connectionCallbacks = ConnectionCallbacks(routerCallback!!, ConnectionResultListener { castPlaybackHandler!!.load(currentPosition.toLong()) })

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

    fun startForeground(song: Song?) {
        var firstArt: Bitmap? = null;
        var songSuccess = false
        if (song != null) {
            firstArt = song.metadataCompat.getBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART)
            songSuccess = (firstArt != null)
        }
        if (!songSuccess) {
            //Couldn't get art from song. Use the default art as a placeholder
            firstArt = (MusicCoreOptions.getDefaultArt() as BitmapDrawable).bitmap
        }
        val notif = PlaybackRemote.makeNotification(firstArt!!)
        startForeground(UNIQUE_ID, notif)
        if (songSuccess) return
        //Fetch the album, then fetch the art, and then finally pass this all to create the notification. Meanwhile, this will use
        //the default art as a placeholder until the new art is fetched.
        //TODO: Use DataLinkers to get the album art (to create the notification
    }

    fun updateNotification(notification: Notification) {
        notificationManager!!.notify(UNIQUE_ID, notification)
    }

    fun kill() {
        destroySession()
        stopForeground(true)
        stopSelf()
    }

    override fun onUnbind(intent: Intent) = true

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
}