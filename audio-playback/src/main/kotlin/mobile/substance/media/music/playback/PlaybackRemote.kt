/*
 * Copyright 2017 Substance Mobile
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

package mobile.substance.media.music.playback

import android.app.Activity
import android.app.Notification
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v7.app.NotificationCompat
import android.util.Log
import co.metalab.asyncawait.async
import com.google.android.gms.cast.framework.CastContext
import mobile.substance.media.core.audio.Song
import mobile.substance.media.utils.AudioCoreUtil
import mobile.substance.media.music.playback.players.Playback
import mobile.substance.media.music.playback.service.DefaultMediaNotification
import mobile.substance.media.music.playback.service.MediaNotification
import mobile.substance.media.music.playback.service.AudioQueue
import mobile.substance.media.music.playback.service.AudioService
import mobile.substance.media.options.AudioPlaybackOptions
import mobile.substance.media.utils.AudioPlaybackUtil
import java.util.*
import kotlin.collections.ArrayList

/**
 * This class is used to control all music playback.
 */
object PlaybackRemote : ServiceConnection {
    private var context: Context? = null
    private var serviceClass: Class<*> = AudioService::class.java
    private var service: AudioService? = null
    private var isBound = false

    ///////////////////////////////////////////////////////////////////////////
    // Manages Connections
    ///////////////////////////////////////////////////////////////////////////

    private val SERVICE_BOUND_LISTENERS: MutableList<(AudioService) -> Unit> = ArrayList()

    private fun getService(listener: AudioService.() -> Unit) {
        val running = if (context != null) AudioPlaybackUtil.isServiceRunning(context!!, serviceClass) else false
        if (running && isBound && service != null) {
            listener.invoke(service!!)
            return
        }
        if (!running) {
            Log.d("getService()", "Looks like the service is not running. It will be started now")
            context?.startService(Intent(context, serviceClass))
        }
        if (!isBound) context?.bindService(Intent(context, serviceClass), this, Activity.BIND_ADJUST_WITH_ACTIVITY)
        SERVICE_BOUND_LISTENERS.add(listener)
    }

    internal fun delegate(call: AudioService.() -> Any) = getService { this.call() }

    override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
        service = (binder as AudioService.ServiceBinder).service
        isBound = true
        try {
            for (listener in SERVICE_BOUND_LISTENERS) listener.invoke(service!!)
        } catch (e: KotlinNullPointerException) {
            e.printStackTrace()
        }
        SERVICE_BOUND_LISTENERS.clear()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        service = null
        isBound = false
    }

    ///////////////////////////////////////////////////////////////////////////
    // Main
    ///////////////////////////////////////////////////////////////////////////

    fun <S : AudioService> init(serviceClass: Class<S>, context: Context) {
        this.serviceClass = serviceClass
        this.context = context
        if (AudioPlaybackOptions.isCastEnabled) CastContext.getSharedInstance(context)
    }

    fun cleanup() {
        if (isBound) try {
            context!!.unbindService(this)
        } catch (e: Exception) {}
    }

    ///////////////////////////////////////////////////////////////////////////
    // Callback
    ///////////////////////////////////////////////////////////////////////////

    fun registerCallback(callback: RemoteCallback) = getService { registerCallback(callback) }

    fun unregisterCallback(callback: RemoteCallback) = getService { unregisterCallback(callback) }

    fun requestUpdates(callback: RemoteCallback) = getService { update(callback) }

    interface RemoteCallback {

        fun onReceivedIntent(intent: Intent) = Unit

        fun onProgressChanged(progress: Int)

        fun onDurationChanged(duration: Int, durationString: String)

        fun onSongChanged(song: Song)

        fun onStateChanged(@PlaybackStateCompat.State state: Int)

        fun onRepeatModeChanged(@PlaybackStateCompat.RepeatMode mode: Int)

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
        engine.play()
    }

    /**
     * Set the queue to the provided list and start playing from the provided position.
     */
    fun play(songs: List<Song>, position: Int) {
        var play = true
        //Make sure we are not already playing the first song in the list. If we are, then just keep playing and quietly update the queue in the background (unless specified otherwise)
        if (getCurrentSong() != null) play = songs[position] != getCurrentSong()
        AudioQueue.set(songs.toMutableList(), position)
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
            getService { engine.play(song) }
        }
    }

    fun playFromUri(uri: Uri, extras: Bundle) = getService { control()!!.transportControls.playFromUri(uri, extras) }

    fun playFromMediaId(mediaId: String, extras: Bundle) = getService { control()!!.transportControls.playFromMediaId(mediaId, extras) }

    fun playFromSearch(query: String, extras: Bundle) = getService { control()!!.transportControls.playFromSearch(query, extras) }

    /**
     * Play the next song in the queue
     */
    fun playNext() = getService { control()?.transportControls?.skipToNext() }

    internal fun playNextInternal() = getService {
        AudioQueue.moveForward(1)
        play()
    }

    /**
     * Play the previous song in the queue
     */
    fun playPrevious() = getService { control()?.transportControls?.skipToPrevious() }

    internal fun playPreviousInternal() {
        AudioQueue.moveBackward(1)
        play()
    }

    /**
     * Resume playback if it was previously paused
     */
    fun resume() = getService {
        // Calling play on the MediaController will actually call resume on the service
        control()!!.transportControls!!.play()
    }

    /**
     * Pause playback
     */
    fun pause() = getService { control()!!.transportControls.pause() }

    /**
     * Stop playback
     */
    fun stop() = getService { control()!!.transportControls.stop() }

    /**
     * Seek playback to the specified position
     */
    fun seekTo(progress: Int) = getService { control()!!.transportControls.seekTo(progress.toLong()) }

    /**
     * Shuffle the current queue
     */
    fun useShuffledQueue(useShuffledQueue: Boolean) {
        if (!AudioQueue.isSecondaryActive && useShuffledQueue) AudioQueue.initSecondaryQueue()
        AudioQueue.isSecondaryActive
    }

    /**
     * Set whether the playback engine shall loop or not
     */
    fun setRepeatMode(@PlaybackStateCompat.RepeatMode mode: Int) = getService { control()!!.transportControls.setRepeatMode(mode) }

    /**
     * Switch the repeat mode in the recurring order [PlaybackStateCompat.REPEAT_MODE_NONE] -> [PlaybackStateCompat.REPEAT_MODE_ALL] -> [PlaybackStateCompat.REPEAT_MODE_ONE]
     */
    fun switchRepeatMode() = getService {
        when (engine.repeatMode) {
            PlaybackStateCompat.REPEAT_MODE_NONE -> control()!!.transportControls.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ALL)
            PlaybackStateCompat.REPEAT_MODE_ALL -> control()!!.transportControls.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ONE)
            PlaybackStateCompat.REPEAT_MODE_ONE -> control()!!.transportControls.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_NONE)
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Queue
    ///////////////////////////////////////////////////////////////////////////

    fun getCurrentSong(): Song? = AudioQueue.getCurrentSong()

    fun getNextSong(): Song? = AudioQueue.get(AudioQueue.POSITION + 1)

    fun getPreviousSong(): Song? = AudioQueue.get(AudioQueue.POSITION - 1)

    fun getQueue(startAtCurrentPosition: Boolean = false): List<Song> = AudioQueue.getQueue(startAtCurrentPosition)

    fun getCurrentPosition() = AudioQueue.POSITION

    fun setQueue(queue: MutableList<Song>, position: Int) = AudioQueue.set(queue, position)

    /**
     * Swaps the two songs at the given positions. You may want to use this in your UI, e.g. when using a drag-drop style queue reordering
     */
    fun swapQueueItems(fromPosition: Int, toPosition: Int, startAtCurrentPosition: Boolean = false, triggerCallbacks: Boolean = false) {
        Collections.swap(AudioQueue.getQueue(), if (startAtCurrentPosition) fromPosition + AudioQueue.POSITION + 1 else fromPosition, if (startAtCurrentPosition) toPosition + AudioQueue.POSITION + 1 else toPosition)
        if (triggerCallbacks) AudioQueue.notifyChanged()
    }

    fun removeFromQueue(pos: Int, startsAtCurrentPosition: Boolean = false, triggerCallbacks: Boolean = false) {
        AudioQueue.getQueue().removeAt(if (startsAtCurrentPosition) AudioQueue.POSITION + 1 + pos else pos)
        if (triggerCallbacks) AudioQueue.notifyChanged()
    }

    fun addToQueueAsNext(song: Song, triggerCallbacks: Boolean = false) {
        AudioQueue.getQueue().add(AudioQueue.POSITION + 1, song)
        if (triggerCallbacks) AudioQueue.notifyChanged()
    }

    fun addToQueue(song: Song, triggerCallbacks: Boolean = false) = addToQueue(listOf(song), triggerCallbacks)

    fun addToQueue(songs: List<Song>, triggerCallbacks: Boolean = false) {
        AudioQueue.getQueue().addAll(songs)
        if (triggerCallbacks) AudioQueue.notifyChanged()
    }

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
        async {
            updateInterface.updateNotification(makeNotification(await { AudioCoreUtil.getArtwork(AudioQueue.getCurrentSong()!!, service!!) }))
        }
        return makeNotification(null)
    }

    internal fun makeNotification(albumArt: Bitmap?): Notification {
        if (notificationBuilder == null) {
            notificationBuilder = notificationCreator.createNotification(context!!, getMediaSession(),
                    AudioPlaybackUtil.getPendingIntent(context!!, AudioPlaybackUtil.Action.PLAY, serviceClass),
                    AudioPlaybackUtil.getPendingIntent(context!!, AudioPlaybackUtil.Action.PAUSE, serviceClass),
                    AudioPlaybackUtil.getPendingIntent(context!!, AudioPlaybackUtil.Action.SKIP_FORWARD, serviceClass),
                    AudioPlaybackUtil.getPendingIntent(context!!, AudioPlaybackUtil.Action.SKIP_BACKWARD, serviceClass),
                    AudioPlaybackUtil.getPendingIntent(context!!, AudioPlaybackUtil.Action.NOTIFICATION, serviceClass),
                    AudioPlaybackUtil.getPendingIntent(context!!, AudioPlaybackUtil.Action.STOP, serviceClass))
        }
        notificationCreator.populate(getCurrentSong()!!,
                notificationBuilder!!,
                getMediaSession(),
                AudioPlaybackUtil.getPendingIntent(context!!, AudioPlaybackUtil.Action.PLAY, serviceClass),
                AudioPlaybackUtil.getPendingIntent(context!!, AudioPlaybackUtil.Action.PAUSE, serviceClass))
        if (albumArt != null) notificationCreator.loadArt(albumArt, notificationBuilder!!)
        return notificationCreator.buildNotification(notificationBuilder!!)
    }

    ///////////////////////////////////////////////////////////////////////////
    // Other
    ///////////////////////////////////////////////////////////////////////////

    /**
     * <b>WARNING: Do not call this unless really necessary or you know what you are doing</b>
     *
     * Feel free to override Playback and set it here. This will tell the service to control this instead of the default LocalPlayback, GaplessPlayback and CastPlayback classes. As a warning, if you start cast playback this will be replaced.
     * Be warned that if you stop cast playback there will first be a call to AudioPlaybackOptions to create a custom player instance. Make sure you call that too.
     *
     * If you want all state to be transferred between players, make sure you leave the hotswap (the second parameter) as true. This will automatically transfer playback state
     * position, and current song between players to ensure consistent playback. If you have an option in your settings activity for setting a custom player, it is recommended
     * that you use hotswap and notify the user that the change has actually been made, as it might not be obvious to the user that the transaction has actually happened.
     */
    @JvmOverloads fun setCustomPlaybackEngineForService(engine: Playback, hotSwap: Boolean = true) = getService { replacePlaybackEngine(engine, hotSwap, false) }

    fun getMediaSession() = service?.getMediaSession()

    fun isReady() = isBound

    fun isPlaying() = service?.engine?.isPlaying()

    fun isActive() = getCurrentSong() != null

    fun getRepeatMode() = service?.engine?.repeatMode

    /**
     * Calling this method allows interference with internal stuff. Only call it if you know what you're doing and log heavily!
     */
    fun getActivePlaybackEngine() = service?.engine

}