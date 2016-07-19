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

import android.app.Activity
import android.app.Notification
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.IBinder
import android.support.v7.app.NotificationCompat
import android.util.Log
import com.google.android.gms.cast.framework.CastContext
import mobile.substance.sdk.music.core.MusicCoreOptions
import mobile.substance.sdk.music.core.dataLinkers.MusicData
import mobile.substance.sdk.music.core.objects.Song
import mobile.substance.sdk.music.playback.players.Playback
import mobile.substance.sdk.music.playback.service.*
import java.net.URL
import java.util.*

/**
 * This class is used to control all music playback.
 */
object PlaybackRemote : ServiceConnection {
    private var context: Context? = null
    private var service: MusicService? = null
    private var isBound = false

    ///////////////////////////////////////////////////////////////////////////
    // Manages Connections
    ///////////////////////////////////////////////////////////////////////////

    private val SERVICE_BOUND_LISTENERS: MutableList<ServiceLoadListener> = arrayListOf()

    private interface ServiceLoadListener {
        fun respond(service: MusicService?)
    }

    private fun getService(listener: ServiceLoadListener) {
        val running = if (context != null) MusicPlaybackUtil.isServiceRunning(context!!) else false
        if (running && isBound) {
            listener.respond(service)
            return
        }
        if (!running) {
            Log.d("getService()", "Looks like the service is not running. Starting it now...")
            context?.startService(Intent(context, MusicService::class.java))
        }
        if (!isBound) context?.bindService(Intent(context, MusicService::class.java), this, Activity.BIND_ADJUST_WITH_ACTIVITY)
        SERVICE_BOUND_LISTENERS.add(listener)
    }

    override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
        service = (binder as MusicService.ServiceBinder).service
        //TODO: Callback
        isBound = true
        for (listener in SERVICE_BOUND_LISTENERS) {
            listener.respond(service)
            SERVICE_BOUND_LISTENERS.remove(listener)
        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        service = null
        isBound = false
    }

    ///////////////////////////////////////////////////////////////////////////
    // Main
    ///////////////////////////////////////////////////////////////////////////

    fun init(context: Context) {
        this.context = context
        if (MusicPlaybackOptions.isCastEnabled) CastContext.getSharedInstance(context)
    }

    fun cleanup() {
        if (isBound) context!!.unbindService(this)
    }

    ///////////////////////////////////////////////////////////////////////////
    // Callback
    ///////////////////////////////////////////////////////////////////////////

    fun registerCallback(callback: RemoteCallback) = getService(object : ServiceLoadListener {
        override fun respond(service: MusicService?) {
            service!!.registerCallback(callback)
        }
    })

    fun unregisterCallback(callback: RemoteCallback) = getService(object : ServiceLoadListener {
        override fun respond(service: MusicService?) {
            service!!.unregisterCallback(callback)
        }
    })

    interface RemoteCallback {

        fun onReceivedIntent(intent: Intent) {
            when {
                intent.action.endsWith(".RESUME") -> resume()
                intent.action.endsWith(".PAUSE") -> pause()
                intent.action.endsWith(".skip.FORWARD") -> playNext()
                intent.action.endsWith(".skip.BACKWARD") -> playPrevious()
                intent.action.endsWith(".NOTIFICATION") -> context!!.startActivity(context!!.packageManager.getLaunchIntentForPackage(context!!.applicationContext.packageName))
                intent.action.endsWith(".SEEK") -> seekTo(intent.getIntExtra("progress", 0))
            }
        }

        fun onProgressChanged(progress: Int)

        fun onDurationChanged(duration: Int)

        fun onSongChanged(song: Song)

        fun onStateChanged(state: PlaybackState, isRepeating: Boolean)

        fun onQueueChanged(queue: List<Song>)

    }

    ///////////////////////////////////////////////////////////////////////////
    // Controls
    ///////////////////////////////////////////////////////////////////////////

    internal fun play() = getService(object : ServiceLoadListener {
        override fun respond(service: MusicService?) {
            //Calling play using direct access to playback object because calling play on the MediaController will resume and cause unintended behaviour
            service!!.engine.play()
        }
    })

    /**
     * Set the queue to the provided list and start playing from the provided position.
     */
    fun play(songs: MutableList<Song>, position: Int) {
        var play = true
        //Make sure we are not already playing the first song in the list. If we are, then just keep playing and quietly update the queue in the background (unless specified otherwise)
        if (getCurrentSong() != null) play = songs[position].id != getCurrentSong()?.id
        MusicQueue.set(songs, position)
        if (play) play()
    }

    /**
     * Play the song provided. This will replace the queue unless you specify otherwise in the second parameter (This is not recommended as it might cause undocumented behaviour)
     */
    @JvmOverloads fun play(song: Song, replaceQueue: Boolean = true) {
        if (replaceQueue) {
            play(arrayListOf(song), 0)
        } else {
            //Connect to the service and start playing music. This is not recommended and may cause unpredictable behaviour
            getService(object : ServiceLoadListener {
                override fun respond(service: MusicService?) = service!!.engine.play(song)
            })
        }
    }

    /**
     * Play the next song in the queue
     */
    fun playNext() {
        MusicQueue.moveForward(1)
        play()
    }

    /**
     * Play the previous song in the queue
     */
    fun playPrevious() {
        MusicQueue.moveBackward(1)
        play()
    }

    /**
     * Resume playback if it was previously paused
     */
    fun resume() = getService(object : ServiceLoadListener {
        override fun respond(service: MusicService?) {
            //Calling play on the MediaController will actually call resume on the service
            service!!.control()!!.transportControls!!.play()
        }
    })

    /**
     * Pause playback
     */
    fun pause() = getService(object : ServiceLoadListener {
        override fun respond(service: MusicService?) {
            service!!.control()!!.transportControls.pause()
        }
    })

    /**
     * Stop playback
     */
    fun stop() = getService(object : ServiceLoadListener {
        override fun respond(service: MusicService?) {
            service!!.control()!!.transportControls.stop()
        }
    })

    /**
     * Seek playback to the specified position
     */
    fun seekTo(progress: Int) = getService(object : ServiceLoadListener {
        override fun respond(service: MusicService?) {
            service!!.control()!!.transportControls.seekTo(progress.toLong())
        }
    })

    /**
     * Shuffle the hooked songs
     */
    fun shuffle() {
        // TODO: Finalize
        val songs = ArrayList<Song>()
        songs.addAll(MusicData.getSongs())
        Collections.shuffle(songs)
        play(songs, 0)
    }

    ///////////////////////////////////////////////////////////////////////////
    // Queue
    ///////////////////////////////////////////////////////////////////////////

    fun getCurrentSong(): Song? = MusicQueue.getCurrentSong()

    fun getQueue(): List<Song>? = MusicQueue.getQueue(false)

    fun getQueue(startAtPosition: Boolean): List<Song>? = MusicQueue.getQueue(startAtPosition)

    fun setQueue(queue: MutableList<Song>, position: Int) = MusicQueue.set(queue, position)

    ///////////////////////////////////////////////////////////////////////////
    // Notification
    ///////////////////////////////////////////////////////////////////////////

    private var notificationCreator: MediaNotification = DefaultMediaNotification()
    private var notificationBuilder: NotificationCompat.Builder? = null

    fun setNotification(notification: MediaNotification) {
        this.notificationCreator = notification
    }

    fun makeNotificaion(): Notification {
        return makeNotification(null)
    }

    interface NotificationUpdateInterface {
        fun updateNotification(notification: Notification)
    }

    internal fun getArtwork(): Bitmap? {
        try {
            val albumArtPath = MusicData.findAlbumById(getCurrentSong()?.songAlbumId!!)?.albumArtworkPath
            if (albumArtPath != null && albumArtPath.length > 0) return BitmapFactory.decodeFile(albumArtPath)
            if (getCurrentSong()?.hasExplicitArtwork!! && getCurrentSong()?.explicitArtworkPath!!.length > 0) {
                val url = MusicPlaybackUtil.getUrlFromUri(Uri.parse(getCurrentSong()?.explicitArtworkPath))
                if (url != null) BitmapFactory.decodeStream(URL(url).openStream()) else BitmapFactory.decodeFile(url)
            }
        } catch (e: Exception) { e.printStackTrace() }

        return BitmapFactory.decodeResource(context?.resources, MusicCoreOptions.defaultArt)
    }

    fun makeNotification(updateInterface: NotificationUpdateInterface): Notification {

        val notification = PlaybackRemote.makeNotification(getArtwork())
        updateInterface.updateNotification(notification)

        return notification
    }

    internal fun makeNotification(albumArt: Bitmap?): Notification {
        if (notificationBuilder == null) notificationBuilder = notificationCreator.createNotification(context!!, getMediaSession(), // Why checking notificationBuilder != null?
                MusicPlaybackUtil.getPendingIntent(context!!, MusicPlaybackUtil.Action.PLAY),
                MusicPlaybackUtil.getPendingIntent(context!!, MusicPlaybackUtil.Action.PAUSE),
                MusicPlaybackUtil.getPendingIntent(context!!, MusicPlaybackUtil.Action.SKIP_FORWARD),
                MusicPlaybackUtil.getPendingIntent(context!!, MusicPlaybackUtil.Action.SKIP_BACKWARD),
                MusicPlaybackUtil.getPendingIntent(context!!, MusicPlaybackUtil.Action.NOTIFICATION),
                MusicPlaybackUtil.getPendingIntent(context!!, MusicPlaybackUtil.Action.STOP))
        notificationCreator.populate(getCurrentSong()!!, notificationBuilder!!)
        if (albumArt != null) notificationCreator.loadArt(albumArt, notificationBuilder!!)
        return notificationCreator.buildNotif(notificationBuilder!!)
    }

    ///////////////////////////////////////////////////////////////////////////
    // Other
    ///////////////////////////////////////////////////////////////////////////

    /**
     * <b>WARNING: Do not call this unless really necessary or you know what you are doing</b>
     *
     * <b>This is what the service controls so make sure you know what you are doing.</b>
     *
     * Feel free to override Playback and set it here. This will tell the service to control this instead of the default LocalPlayback and CastPlayback classes. As a warning, if you start cast playback this will be replaced.
     * Be warned that if you stop cast playback there will first be a call to MusicPlaybackOptions to create a custom player instance. Make sure you call that too.
     *
     * If you want all state to be transferred between players, make sure you leave the hotswap (the second parameter) as true. This will automatically transfer playback state
     * position, and current song between players to ensure consistent playback. If you have an option in your settings activity for setting a custom player, it is recommended
     * that you use hotswap and notify the user that the change has actually been made, as it might not be obvious to the user that the transaction has actually happened.
     */
    @JvmOverloads fun setCustomPlaybackEngineForService(engine: Playback, hotswap: Boolean = true) = getService(object : ServiceLoadListener {
        override fun respond(service: MusicService?) = service!!.replacePlaybackEngine(engine, hotswap, false)
    })

    fun getMediaSession() = service?.getMediaSession()

    fun isReady() = isBound

    fun isPlaying() = service?.engine?.isPlaying()

}