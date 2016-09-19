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
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.*
import android.util.Log
import mobile.substance.sdk.music.core.dataLinkers.MusicData
import mobile.substance.sdk.music.core.objects.Album
import mobile.substance.sdk.music.core.objects.Artist
import mobile.substance.sdk.music.core.objects.Song
import mobile.substance.sdk.music.core.utils.MusicCoreUtil
import mobile.substance.sdk.music.playback.MusicPlaybackOptions
import mobile.substance.sdk.music.playback.PlaybackRemote
import mobile.substance.sdk.music.playback.service.MusicQueue
import mobile.substance.sdk.music.playback.service.MusicService
import mobile.substance.sdk.music.playback.service.PlaybackState
import java.util.*

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

        init()
        createPlayerIfNecessary(false)
        configPlayer()
    }

    abstract fun init()

    ///////////////////////////////////////////////////////////////////////////
    // Player Helper methods
    ///////////////////////////////////////////////////////////////////////////

    private var pendingCalls: MutableList<() -> Unit> = ArrayList()
    var inHotswapTransaction = false
    private var firstPlayer = true
    private var playerReleased = false

    /**
     * Override this to specify that the player instance was NOT created when your playback class was created. Otherwise don't
     */
    open val playerCreatedOnClassCreation: Boolean = true

    /**
     * It is recommended that you don't call this method, as the library calls it itself where necessary. Only call this
     * if you know what you are doing
     */
    fun createPlayerIfNecessary(config: Boolean) {
        if (isPlayerNecessary()) {
            createPlayer()
            if (config) configPlayer()
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

    open fun isPlayerNecessary() = firstPlayer and !playerCreatedOnClassCreation or playerReleased

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

    fun play(song: Song, mediaId: Long? = null) {
        play(song.uri, false, mediaId ?: song.id)
        SERVICE!!.callback {
            onSongChanged(song)
            onDurationChanged(song.songDuration?.toInt() ?: 0, song.songDurationString)
        }
    }

    private fun play(uri: Uri, listenersAlreadyNotified: Boolean, mediaId: Long? = null) {
        createPlayerIfNecessary(true)
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
        if (inHotswapTransaction) {
            pendingCalls.add { resume() }
            return
        }

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

    private fun playFromMediaId(mediaId: Long?) {

    }

    override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
        val song = MusicData.findSongById(mediaId!!.toLong())
        if (song != null) play(song) else Log.d(TAG, "onPlayFromMediaId: no song with such ID exists")
    }

    override fun onPlayFromSearch(query: String?, extras: Bundle?) {
        query ?: return
        var songs: MutableList<Song>? = null
        when (extras?.get(MediaStore.EXTRA_MEDIA_FOCUS)) {
            MediaStore.Audio.Media.ENTRY_CONTENT_TYPE -> {
                songs = MusicData.search<Song>(query) ?: return

            }
            MediaStore.Audio.Albums.ENTRY_CONTENT_TYPE -> {
                val results = MusicData.search<Album>(query) ?: return
                songs = ArrayList<Song>()
                results.forEach { songs!!.addAll(MusicData.findSongsForAlbum(it)) }
            }
            MediaStore.Audio.Artists.ENTRY_CONTENT_TYPE -> {
                val results = MusicData.search<Artist>(query) ?: return
                songs = ArrayList<Song>()
                results.forEach { songs!!.addAll(MusicData.findSongsForArtist(it)) }
            }
        }
        if (songs != null && songs.size > 0) PlaybackRemote.play(songs, 0)
    }

    ///////////////////////////////////////////////////////////////////////////
    // Pause
    ///////////////////////////////////////////////////////////////////////////

    fun pause() {
        if (inHotswapTransaction) {
            pendingCalls.add { pause() }
            return
        }

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
        if (isRepeating && callRepeatOnNext) doRepeat() else doNext()
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
        if (inHotswapTransaction) {
            pendingCalls.add { stop() }
            return
        }

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
        if (inHotswapTransaction) {
            pendingCalls.add { seek(time) }
            return
        }

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
        set(value) {
            field = value
            when (field) {
                PlaybackStateCompat.STATE_PAUSED -> SERVICE!!.callback { onStateChanged(PlaybackState.STATE_PAUSED) }
                PlaybackStateCompat.STATE_PLAYING -> SERVICE!!.callback { onStateChanged(PlaybackState.STATE_PLAYING) }
                PlaybackStateCompat.STATE_NONE -> SERVICE!!.callback { onStateChanged(PlaybackState.STATE_IDLE) }
            }
        }

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

    open var isRepeating = false
        set(value) {
            field = value
            notifyRepeatingChanged()
        }

    internal fun notifyRepeatingChanged() {
        SERVICE!!.callback { onRepeatingChanged(isRepeating) }
    }

    open fun doRepeat() = play()

    open val callRepeatOnNext = true

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

    protected fun nowPlaying() {
        if (inHotswapTransaction) {
            inHotswapTransaction = false
            for (call in pendingCalls)
                call.invoke()
            pendingCalls.clear()
        }
        SERVICE!!.startProgressThread()
        // Set the state because the thread takes a few ms to make its first run
        SERVICE!!.getMediaSession()?.setPlaybackState(
                PlaybackStateCompat.Builder().setActions(MusicPlaybackOptions.playbackActions.getActions())
                        .setState(playbackState, getCurrentPosInSong().toLong(), getPlaybackSpeed())
                        .build())
        SERVICE!!.updateMetadata()
        SERVICE!!.startForeground()
    }

    protected fun nowPaused() {
        SERVICE!!.shutdownProgressThreadIfNecessary()
        SERVICE!!.stopForeground(false)
        SERVICE!!.getMediaSession()?.setPlaybackState(
                PlaybackStateCompat.Builder().setActions(MusicPlaybackOptions.playbackActions.getActions())
                        .setState(playbackState, getCurrentPosInSong().toLong(), getPlaybackSpeed())
                        .build())
        SERVICE!!.updateNotification(PlaybackRemote.makeNotification())
    }

}