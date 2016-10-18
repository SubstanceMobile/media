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
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v7.app.NotificationCompat
import android.util.Log
import com.google.android.gms.cast.framework.CastContext
import mobile.substance.sdk.music.core.dataLinkers.MusicData
import mobile.substance.sdk.music.core.objects.Song
import mobile.substance.sdk.music.core.utils.MusicCoreUtil
import mobile.substance.sdk.music.playback.players.Playback
import mobile.substance.sdk.music.playback.service.DefaultMediaNotification
import mobile.substance.sdk.music.playback.service.MediaNotification
import mobile.substance.sdk.music.playback.service.MusicQueue
import mobile.substance.sdk.music.playback.service.MusicService
import java.util.*

/**
 * This class is used to control all music playback.
 */
object PlaybackRemote : ServiceConnection {
    private var context: Context? = null
    private var serviceClass: Class<*> = MusicService::class.java
    private var service: MusicService? = null
    private var isBound = false

    ///////////////////////////////////////////////////////////////////////////
    // Manages Connections
    ///////////////////////////////////////////////////////////////////////////

    private val SERVICE_BOUND_LISTENERS: MutableList<(MusicService?) -> Unit> = arrayListOf()

    private fun getService(listener: (MusicService?) -> Unit) {
        val running = if (context != null) MusicPlaybackUtil.isServiceRunning(context!!, serviceClass) else false
        if (running && isBound) {
            listener.invoke(service)
            return
        }
        if (!running) {
            Log.d("getService()", "Looks like the service is not running. Starting it now...")
            context?.startService(Intent(context, serviceClass))
        }
        if (!isBound) context?.bindService(Intent(context, serviceClass), this, Activity.BIND_ADJUST_WITH_ACTIVITY)
        SERVICE_BOUND_LISTENERS.add(listener)
    }

    override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
        service = (binder as MusicService.ServiceBinder).service
        isBound = true
        for (listener in SERVICE_BOUND_LISTENERS) listener.invoke(service)
        SERVICE_BOUND_LISTENERS.clear()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        service = null
        isBound = false
    }

    ///////////////////////////////////////////////////////////////////////////
    // Main
    ///////////////////////////////////////////////////////////////////////////

    fun <S : MusicService> init(serviceClass: Class<S>, context: Context) {
        this.serviceClass = serviceClass
        this.context = context
        if (MusicPlaybackOptions.isCastEnabled) CastContext.getSharedInstance(context)
    }

    fun cleanup() {
        if (isBound) try {
            context!!.unbindService(this)
        } catch (e: Exception) {}
    }

    ///////////////////////////////////////////////////////////////////////////
    // Callback
    ///////////////////////////////////////////////////////////////////////////

    fun registerCallback(callback: RemoteCallback) = getService { it!!.registerCallback(callback) }

    fun unregisterCallback(callback: RemoteCallback) = getService { it!!.unregisterCallback(callback) }

    fun requestUpdates(callback: RemoteCallback) = getService { it!!.update(callback) }

    interface RemoteCallback {

        fun onReceivedIntent(intent: Intent) = Unit

        fun onProgressChanged(progress: Int)

        fun onDurationChanged(duration: Int, durationString: String)

        fun onSongChanged(song: Song)

        fun onStateChanged(state: Int)

        fun onRepeatingChanged(mode: Int)

        fun onQueueChanged(queue: List<Song>)

        fun onError()

        fun onBufferStarted() = Unit

        fun onBufferFinished() = Unit

    }

    ///////////////////////////////////////////////////////////////////////////
    // Controls
    ///////////////////////////////////////////////////////////////////////////

    internal fun play() = getService {
        //Calling play using direct access to playback object because calling play on the MediaController will resume and cause unintended behaviour
        it!!.engine.play()
    }

    /**
     * Set the queue to the provided list and start playing from the provided position.
     */
    fun play(songs: List<Song>, position: Int) {
        var play = true
        //Make sure we are not already playing the first song in the list. If we are, then just keep playing and quietly update the queue in the background (unless specified otherwise)
        if (getCurrentSong() != null) play = songs[position].id != getCurrentSong()?.id
        MusicQueue.set(songs.toMutableList(), position)
        getService { it?.callback { onQueueChanged(MusicQueue.getQueue(false)) } }
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
            getService { it!!.engine.play(song) }
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
    fun resume() = getService {
        // Calling play on the MediaController will actually call resume on the service
        it!!.control()!!.transportControls!!.play()
    }

    /**
     * Pause playback
     */
    fun pause() = getService { it!!.control()!!.transportControls.pause() }

    /**
     * Stop playback
     */
    fun stop() = getService { it!!.control()!!.transportControls.stop() }

    /**
     * Seek playback to the specified position
     */
    fun seekTo(progress: Int) = getService { it!!.control()!!.transportControls.seekTo(progress.toLong()) }

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

    /**
     * Shuffle the current queue
     */
    fun shuffleQueue() {
        val songs = ArrayList<Song>()
        songs.addAll(MusicQueue.getQueue(true))
        Collections.shuffle(songs)
        play(songs, 0)
    }

    /**
     * Set whether the playback engine shall loop or not
     */
    fun setRepeatMode(mode: Int) = getService { it!!.engine.repeatMode = mode }

    /**
     * Switch the repeat mode in the recurring order [RepeatModes.REPEAT_DISABLED] -> [RepeatModes.REPEAT_ONCE] -> [RepeatModes.REPEAT_ENABLED]
     */
    fun switchRepeatMode() = getService {
        when (it?.engine?.repeatMode) {
            RepeatModes.REPEAT_DISABLED -> it?.engine?.repeatMode = RepeatModes.REPEAT_ONCE
            RepeatModes.REPEAT_ONCE -> it?.engine?.repeatMode = RepeatModes.REPEAT_ENABLED
            RepeatModes.REPEAT_ENABLED -> it?.engine?.repeatMode = RepeatModes.REPEAT_DISABLED
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Queue
    ///////////////////////////////////////////////////////////////////////////

    fun getCurrentSong(): Song? = MusicQueue.getCurrentSong()

    fun getQueue(startAtCurrentPosition: Boolean = false): List<Song>? = MusicQueue.getQueue(startAtCurrentPosition)

    fun setQueue(queue: MutableList<Song>, position: Int) {
        MusicQueue.set(queue, position)
        getService { it?.callback { onQueueChanged(MusicQueue.getQueue(false)) } }
    }

    fun switchSongQueuePosition(fromPosition: Int, toPosition: Int, startAtCurrentPosition: Boolean = false) {
        val queue = MusicQueue.getMutableQueue()!!
        queue.add(toPosition, queue[if (startAtCurrentPosition) MusicQueue.POSITION + 1 + fromPosition else fromPosition])
        queue.removeAt(if (fromPosition > toPosition) if (startAtCurrentPosition) MusicQueue.POSITION + 1 + fromPosition else fromPosition + 1 else if (startAtCurrentPosition) MusicQueue.POSITION + 1 + fromPosition else fromPosition)
    }

    fun removeSongFromQueue(pos: Int, startsAtCurrentPosition: Boolean = false) = MusicQueue.getMutableQueue()!!.removeAt(if (startsAtCurrentPosition) MusicQueue.POSITION + 1 + pos else pos)

    ///////////////////////////////////////////////////////////////////////////
    // Notification
    ///////////////////////////////////////////////////////////////////////////

    internal var notificationCreator: MediaNotification = DefaultMediaNotification()
    private var notificationBuilder: NotificationCompat.Builder? = null

    fun setNotification(notification: MediaNotification) {
        this.notificationCreator = notification
    }

    fun makeNotification(): Notification {
        return makeNotification(null)
    }

    interface NotificationUpdateInterface {
        fun updateNotification(notification: Notification)
    }

    fun makeNotification(updateInterface: NotificationUpdateInterface): Notification {

        val artwork = MusicCoreUtil.getArtwork(MusicQueue.getCurrentSong()!!, service!!)
        val notification = makeNotification(artwork)
        updateInterface.updateNotification(notification)

        if (artwork == null) {
            Thread {
                run {
                    try {
                        val inetArtwork = MusicCoreUtil.getArtwork(MusicQueue.getCurrentSong()!!, service!!)
                        Log.d(PlaybackRemote::class.java.simpleName, "New try, seperate Thread! Is it still null? ${inetArtwork == null}, ${inetArtwork?.byteCount}")
                        Handler(Looper.getMainLooper()).post { updateInterface.updateNotification(makeNotification(inetArtwork)) }
                    } catch (ignored: Exception) {
                    }
                }
            }.start()
        }

        return notification
    }

    internal fun makeNotification(albumArt: Bitmap?): Notification {
        if (notificationBuilder == null) {
            notificationBuilder = notificationCreator.createNotification(context!!, getMediaSession(),
                    MusicPlaybackUtil.getPendingIntent(context!!, MusicPlaybackUtil.Action.PLAY, serviceClass),
                    MusicPlaybackUtil.getPendingIntent(context!!, MusicPlaybackUtil.Action.PAUSE, serviceClass),
                    MusicPlaybackUtil.getPendingIntent(context!!, MusicPlaybackUtil.Action.SKIP_FORWARD, serviceClass),
                    MusicPlaybackUtil.getPendingIntent(context!!, MusicPlaybackUtil.Action.SKIP_BACKWARD, serviceClass),
                    MusicPlaybackUtil.getPendingIntent(context!!, MusicPlaybackUtil.Action.NOTIFICATION, serviceClass),
                    MusicPlaybackUtil.getPendingIntent(context!!, MusicPlaybackUtil.Action.STOP, serviceClass))
        }
        notificationCreator.populate(getCurrentSong()!!, notificationBuilder!!, getMediaSession(), MusicPlaybackUtil.getPendingIntent(context!!, MusicPlaybackUtil.Action.PLAY, serviceClass), MusicPlaybackUtil.getPendingIntent(context!!, MusicPlaybackUtil.Action.PAUSE, serviceClass))
        if (albumArt != null) notificationCreator.loadArt(albumArt, notificationBuilder!!)
        return notificationCreator.buildNotification(notificationBuilder!!)
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
    @JvmOverloads fun setCustomPlaybackEngineForService(engine: Playback, hotSwap: Boolean = true) = getService { it!!.replacePlaybackEngine(engine, hotSwap, false) }

    fun getMediaSession() = service!!.getMediaSession()

    fun isReady() = isBound

    fun isPlaying() = service?.engine?.isPlaying()

    fun getRepeatMode() = service?.engine?.repeatMode

}