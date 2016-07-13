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

package mobile.substance.sdk.music.playback.players

import android.content.ContentUris
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat.*
import android.util.Log
import mobile.substance.sdk.music.core.dataLinkers.MusicData
import mobile.substance.sdk.music.core.objects.Song
import mobile.substance.sdk.music.core.utils.MusicCoreUtil
import mobile.substance.sdk.music.playback.MusicQueue
import mobile.substance.sdk.music.playback.MusicService
import mobile.substance.sdk.music.playback.PlaybackRemote

abstract class Playback : MediaSessionCompat.Callback() {
    companion object {
        val TAG = "Playback"
        val ACTION_PLAY_FROM_LIST = "SUBSTANCE_SDK_SET_REPEAT_" + MusicService.UNIQUE_ID
        val ACTION_SET_REPEAT = "SUBSTANCE_SDK_SET_REPEAT_" + MusicService.UNIQUE_ID
    }

    var SERVICE: MusicService? = null

    fun init(service: MusicService) {
        SERVICE = service
        Log.i(TAG, "Service has been set. We are now initialized")

        //Calling configPlayer because instance will always be created out of the box. If necessary we will call create player too (see above)
        configPlayer()
        init()
    }

    abstract fun init()

    ///////////////////////////////////////////////////////////////////////////
    // Player Helper methods
    ///////////////////////////////////////////////////////////////////////////

    private var firstPlayer = true
    private var playerReleased = false

    /**
     * Override this to specify that the player instance was NOT created when your playback class was created. Otherwise do
     */
    open fun playerCreatedOnClassCreation() = true

    /**
     * It is recommended that you don't call this method, as the library calls it itself where necessary. Only call this
     * if you know what you are doing
     */
    fun createMediaPlayerIfNecessary() {
        if (isPlayerNecessary()) {
            createPlayer()
            configPlayer()
            firstPlayer = false
            playerReleased = false
        }
    }

    /**
     * Override it if you need it, BUT do not call this method. This method is called where it is usually most necessary (on all of the actions that can induce playback)
     * If you do want to call it make sure you know what you are doing.
     */
    open fun createPlayer() {}

    /**
     * Override it if you need it, BUT do not call this method. This method is called where it is usually most necessary (on all of the actions that can induce playback)
     * If you do want to call it make sure you know what you are doing.
     */
    open fun configPlayer() {}

    open fun isPlayerNecessary() = !firstPlayer and playerReleased

    fun tripPlayerNecessity() {
        playerReleased = true
    }

    ///////////////////////////////////////////////////////////////////////////
    // Play
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////
    // Play from Uri //
    ///////////////////

    fun play() = play(MusicQueue.getCurrentSong()!!)

    fun play(song: Song, mediaId: Long? = null) = play(song.uri, false, mediaId)

    fun play(uri: Uri, listenersAlreadyNotified: Boolean, mediaId: Long? = null) {
        createMediaPlayerIfNecessary()
        if (!manuallyHandleState) playbackState = STATE_PLAYING
        doPlay(uri, listenersAlreadyNotified, mediaId)
    }

    abstract fun doPlay(uri: Uri, listenersAlreadyNotified: Boolean, mediaId: Long? = null)

    override fun onPlayFromUri(uri: Uri?, extras: Bundle?) {
        //TODO change the listener already notified to a variable
        if (uri != null) {
            var mediaId: Long? = null
            if (uri.scheme == "content") mediaId = MusicCoreUtil.findByMediaId(ContentUris.parseId(uri), MusicData.getAlbums(), MusicData.getSongs())?.id
            play(uri, false, mediaId)
        }
    }

    ////////////
    // Resume //
    ////////////

    fun resume() {
        if (!manuallyHandleState) playbackState = STATE_PLAYING
        doResume()
    }

    abstract fun doResume()

    override fun onPlay() {
        resume()
    }

    ///////////
    // Other //
    ///////////

    override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
        val song = MusicData.findSongById(mediaId!!.toLong())
        if (song != null) play(song) else Log.d(TAG, "onPlayFromMediaId: no song with such ID exists")
    }

    override fun onPlayFromSearch(query: String?, extras: Bundle?) {
        when (extras?.get(MediaStore.EXTRA_MEDIA_FOCUS)) {

        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Pause
    ///////////////////////////////////////////////////////////////////////////

    fun pause() {
        if (!manuallyHandleState) playbackState = STATE_PAUSED
        doPause()
    }

    abstract fun doPause()

    override fun onPause() = pause()

    ///////////////////////////////////////////////////////////////////////////
    // Next
    ///////////////////////////////////////////////////////////////////////////

    /**
     * This also handles the next button with repeat. This way, calling next() will repeat if setRepeating() is set to true. This can be turned off by overriding
     * callRepeatOnNext to be false.
     */
    fun next() {
        if (!manuallyHandleState) playbackState = STATE_SKIPPING_TO_NEXT
        if (isRepeating() && callRepeatOnNext()) doRepeat() else doNext()
    }

    open fun doNext() = PlaybackRemote.playNext()

    override fun onSkipToNext() = next()

    ///////////////////////////////////////////////////////////////////////////
    // Previous
    // TODO STATE STUFF
    ///////////////////////////////////////////////////////////////////////////

    open fun doPrev() = PlaybackRemote.playPrevious()

    open fun restart() = seek(0L)

    /**
     * Override this to set a new value. When the player is requested to skip back a song, it gets the current playing position.
     * If the position is greater then the value returned here, then it restarts the song, otherwise it actually skips back. This can be disabled entirely be returning -1
     */
    open fun restartOnPrevWhen(): Long = 5000

    private fun shouldSkipBack() = getCurrentPosInSong() <= restartOnPrevWhen()

    override fun onSkipToPrevious() {
        if (shouldSkipBack()) {
            Log.d(TAG, "onSkipToPrevious() called. Calling doPrev()")
            doPrev()
        } else {
            Log.d(TAG, "onSkipToPrevious() called. Calling prevRestart()")
            restart()
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Stop
    ///////////////////////////////////////////////////////////////////////////

    fun stop() {
        if (!manuallyHandleState) playbackState = STATE_STOPPED
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
        if (time.toInt() > getCurrentPosInSong()) {
            if (!manuallyHandleState) playbackState = STATE_FAST_FORWARDING
            doSeek(time)
        } else if (time.toInt() < getCurrentPosInSong()) {
            if (!manuallyHandleState) playbackState = STATE_REWINDING
            doSeek(time)
        } else if (!manuallyHandleState) playbackState = STATE_PLAYING
    }

    abstract fun doSeek(time: Long)

    override fun onSeekTo(pos: Long) {
        seek(pos)
    }

    ///////////////////////////////////////////////////////////////////////////
    // PlaybackState
    ///////////////////////////////////////////////////////////////////////////

    internal var playbackState = STATE_NONE

    /**
     * Call this method to tell the system that you are currently buffering. Call this whenever you start buffering to make sure Android
     * can properly handle your current state.
     */
    fun triggerStartBuffer() {
        playbackState = STATE_BUFFERING
    }

    /**
     * Call this method to tell the system that you are done buffering and you are now playing.
     */
    fun triggerEndBuffer() {
        playbackState = STATE_PLAYING
    }

    /**
     * Call this method to set the playback state. This can also be used as triggerEndBuffer() that just sets a state other then playing.
     */
    fun setState(@State state: Int) {
        if (manuallyHandleState) playbackState = state
    }

    /**
     * Override this to disable playback state being set automatically.
     */
    open val manuallyHandleState: Boolean = false

    ///////////////////////////////////////////////////////////////////////////
    // Repeat
    ///////////////////////////////////////////////////////////////////////////

    private var repeating = false

    open fun setRepeating(repeating: Boolean) {
        this.repeating = repeating
    }

    open fun isRepeating() = repeating

    open fun doRepeat() = play()

    open fun callRepeatOnNext() = true

    ///////////////////////////////////////////////////////////////////////////
    // Others
    ///////////////////////////////////////////////////////////////////////////

    abstract fun isPlaying(): Boolean


    open fun isInitialized() = SERVICE != null

    abstract fun getCurrentPosInSong(): Int

    fun getPlaybackSpeed() = 1.0f

    override fun onCustomAction(action: String?, extras: Bundle?) {
        /*if (ACTION_PLAY_FROM_LIST.equals(action)) {
            play(PlaybackRemote.tempSongList, PlaybackRemote.tempListStartPos)
        } else if (ACTION_SET_REPEAT.equals(action)) {
            repeat(PlaybackRemote.tempRepeating)
        }*/
        //TODO
    }
}