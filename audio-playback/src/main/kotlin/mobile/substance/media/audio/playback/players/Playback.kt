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

package mobile.substance.media.audio.playback.players

import android.net.Uri
import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.STATE_NONE
import android.util.Log
import mobile.substance.media.audio.playback.PlaybackRemote
import mobile.substance.media.audio.playback.service.AudioQueue
import mobile.substance.media.audio.playback.service.AudioService
import mobile.substance.media.core.audio.Song
import java.util.*

abstract class Playback : MediaSessionCompat.Callback() {

    companion object {
        val TAG: String = Playback::class.java.simpleName
    }

    var SERVICE: AudioService? = null

    fun init(service: AudioService) {
        SERVICE = service
        Log.i(TAG, "Service has been set. We are now initialized")

        init()
        createPlayer()
        configPlayer()
    }

    abstract fun init()

    open fun onQueueChanged() = Unit

    ///////////////////////////////////////////////////////////////////////////
    // Player Helper methods
    ///////////////////////////////////////////////////////////////////////////

    private var pendingCalls: MutableList<() -> Unit> = ArrayList()
    var isInHotSwapTransaction = false

    /**
     * Do not call this method. This method is called where it is usually most necessary (on all of the actions that can induce playback)
     * If you do want to call it make sure you know what you are doing.
     */
    open fun createPlayer() {
    }

    /**
     * Override it if you need it, BUT do not call this method. This method is called where it is usually most necessary (on all of the actions that can induce playback)
     * If you do want to call it make sure you know what you are doing.
     */
    open fun configPlayer() {
    }

    ///////////////////////////////////////////////////////////////////////////
    // Play
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////
    // Play from Uri //
    ///////////////////

    fun play() = play(AudioQueue.getCurrentSong()!!)

    fun play(song: Song) {
        doPlay(song)
        if (!(SERVICE!!.getMediaSession()?.isActive ?: false)) SERVICE!!.getMediaSession()?.isActive = true
        dispatchOnSongChanged(song)
    }

    protected fun dispatchOnSongChanged(song: Song) {
        SERVICE!!.callback {
            onSongChanged(song)
            onDurationChanged(song.duration ?: 0, song.formattedDuration)
        }
    }

    open fun doPlay(song: Song) {
        doPlay(song.uri, song.artworkUri)
    }

    abstract fun doPlay(fileUri: Uri, artworkUri: Uri?)

    ////////////
    // Resume //
    ////////////

    fun resume() {
        if (isInHotSwapTransaction) {
            pendingCalls.add { resume() }
            return
        }

        doResume()
    }

    abstract fun doResume()

    override fun onPlay() {
        resume()
    }

    ///////////
    // Other //
    ///////////

    final override fun onPlayFromSearch(query: String, extras: Bundle) {
        SERVICE?.playRequestCallback?.onRequestPlayFromSearch(query, extras)
    }

    final override fun onPlayFromUri(uri: Uri?, extras: Bundle?) {
        SERVICE?.playRequestCallback?.onRequestPlayFromUri(uri, extras)
    }

    final override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
        SERVICE?.playRequestCallback?.onRequestPlayFromMediaId(mediaId, extras)
    }

    ///////////////////////////////////////////////////////////////////////////
    // Pause
    ///////////////////////////////////////////////////////////////////////////

    fun pause() {
        if (isInHotSwapTransaction) {
            pendingCalls.add { pause() }
            return
        }

        doPause()
    }

    abstract fun doPause()

    override fun onPause() = pause()

    ///////////////////////////////////////////////////////////////////////////
    // Next
    ///////////////////////////////////////////////////////////////////////////

    fun next() = if (repeatMode == PlaybackStateCompat.REPEAT_MODE_ONE) play() else doNext()

    open fun doNext() = PlaybackRemote.playNextInternal()

    override fun onSkipToNext() = next()

    ///////////////////////////////////////////////////////////////////////////
    // Previous
    // TODO STATE STUFF
    ///////////////////////////////////////////////////////////////////////////

    open fun doPrevious() = PlaybackRemote.playPreviousInternal()

    open fun restart() = seek(0L)

    /**
     * Override this to set a new value. When the player is requested to skip back a song, it gets the current playing position.
     * If the position is greater then the value returned here, then it restarts the song, otherwise it actually skips back. This can be disabled entirely be returning -1
     */
    open fun restartOnPreviousWhen(): Long = 5000

    private fun shouldSkipBack() = getCurrentPosition() <= restartOnPreviousWhen()

    override fun onSkipToPrevious() {
        if (shouldSkipBack()) {
            Log.d(TAG, "onSkipToPrevious() called. Calling doPrevious()")
            doPrevious()
        } else {
            Log.d(TAG, "onSkipToPrevious() called. Calling prevRestart()")
            restart()
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Stop
    ///////////////////////////////////////////////////////////////////////////

    fun stop() {
        if (isInHotSwapTransaction) {
            pendingCalls.add { stop() }
            return
        }

        doStop()
    }

    abstract fun doStop()

    override fun onStop() {
        stop()
    }

    ///////////////////////////////////////////////////////////////////////////
    // Seek
    ///////////////////////////////////////////////////////////////////////////

    fun seek(time: Long) {
        if (isInHotSwapTransaction) {
            pendingCalls.add { seek(time) }
            return
        }

        if (time.toInt() > getCurrentPosition() || time.toInt() < getCurrentPosition()) doSeek(time)
    }

    abstract fun doSeek(time: Long)

    override fun onSeekTo(pos: Long) = seek(pos)

    ///////////////////////////////////////////////////////////////////////////
    // PlaybackState
    ///////////////////////////////////////////////////////////////////////////

    internal var playbackState = STATE_NONE
        private set

    ///////////////////////////////////////////////////////////////////////////
    // Repeat
    ///////////////////////////////////////////////////////////////////////////

    @PlaybackStateCompat.RepeatMode
    var repeatMode: Int = PlaybackStateCompat.REPEAT_MODE_NONE
        set(value) {
            field = value
            dispatchRepeatModeChanged(value)
        }

    override fun onSetRepeatMode(repeatMode: Int) {
        this.repeatMode = repeatMode
    }

    ///////////////////////////////////////////////////////////////////////////
    // Shuffle mode
    ///////////////////////////////////////////////////////////////////////////

    override fun onSetShuffleModeEnabled(enabled: Boolean) = PlaybackRemote.useShuffledQueue(enabled)

    ///////////////////////////////////////////////////////////////////////////
    // Others
    ///////////////////////////////////////////////////////////////////////////

    abstract fun isPlaying(): Boolean

    open fun isInitialized() = SERVICE != null

    abstract fun getCurrentPosition(): Int

    fun getPlaybackSpeed() = 1.0F

    // Override if needed
    override fun onCustomAction(action: String?, extras: Bundle?) = Unit

    ///////////////////////////////////////////////////////////////////////////
    // State handling
    ///////////////////////////////////////////////////////////////////////////

    private fun dispatchPlaybackState(callback: Boolean = false) = SERVICE!!.updatePlaybackState(callback)

    protected fun dispatchPlaybackProgress(progress: Long? = null) = SERVICE!!.updatePlaybackProgress(progress)

    protected fun dispatchRepeatModeChanged(@PlaybackStateCompat.RepeatMode repeatMode: Int) {
        SERVICE!!.callback { onRepeatModeChanged(repeatMode) }
    }

    /**
     * Update the MediaSession's state to buffering
     */
    protected fun notifyBuffering() {
        Log.d(TAG, "notifyBuffering()")
        playbackState = PlaybackStateCompat.STATE_BUFFERING
        dispatchPlaybackState(true)
    }

    /**
     * Update the MediaSession's state to error and trigger UI changes
     */
    protected fun notifyError() {
        Log.d(TAG, "notifyError()")
        playbackState = PlaybackStateCompat.STATE_ERROR
        dispatchPlaybackState(true)
        SERVICE!!.stopForeground(false)
        if (PlaybackRemote.isActive()) SERVICE!!.notify(PlaybackRemote.makeNotification())
    }

    /**
     * Update the MediaSession's state to playing and trigger UI changes
     */
    protected fun notifyPlaying() {
        Log.d(TAG, "notifyPlaying()")
        if (playbackState == PlaybackStateCompat.STATE_BUFFERING) SERVICE!!.callback { onBufferFinished() }
        playbackState = PlaybackStateCompat.STATE_PLAYING
        if (isInHotSwapTransaction) {
            isInHotSwapTransaction = false
            for (call in pendingCalls)
                call.invoke()
            pendingCalls.clear()
        }
        dispatchPlaybackState(true)
        SERVICE!!.updateMetadata()
        SERVICE!!.startForeground()
    }

    /**
     * Update the MediaSession's state to paused and trigger UI changes
     */
    protected fun notifyPaused() {
        Log.d(TAG, "notifyPaused()")
        playbackState = PlaybackStateCompat.STATE_PAUSED
        SERVICE!!.stopForeground(false)
        dispatchPlaybackState(true)
        SERVICE!!.notify(PlaybackRemote.makeNotification())
    }

    /**
     * Update the MediaSession's state to idle/none and trigger UI changes
     */
    protected fun notifyIdle() {
        Log.d(TAG, "notifyIdle()")
        playbackState = PlaybackStateCompat.STATE_NONE
        dispatchPlaybackState()
        SERVICE!!.stopForeground(true)
    }

}