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

    fun play(song: Song) {
        doPlay(song)
        SERVICE!!.callback {
            if (!(SERVICE!!.getMediaSession()?.isActive ?: false)) SERVICE!!.getMediaSession()?.isActive = true
            onSongChanged(song)
            onDurationChanged(song.songDuration?.toInt() ?: 0, song.songDurationString)
        }
    }

    private fun play(uri: Uri) {
        createPlayerIfNecessary(true)
        var mediaId: Long? = null
        if (uri.scheme == "content") mediaId = MusicCoreUtil.findByMediaId(ContentUris.parseId(uri), MusicData.getAlbums(), MusicData.getSongs())?.id
        val artworkPath = MusicData.findAlbumById(MusicData.findSongById(mediaId ?: 0)?.songAlbumId ?: 0)?.albumArtworkPath
        doPlay(uri, if (artworkPath != null) Uri.parse("file://$artworkPath") else null)
    }

    override fun onPlayFromUri(uri: Uri?, extras: Bundle?) {
        //TODO change the listener already notified to a variable
        if (uri != null) play(uri)
    }

    override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
        val song = MusicData.findSongById(mediaId!!.toLong())
        if (song != null) play(song) else Log.d(TAG, "onPlayFromMediaId: no song with such ID exists")
    }

    open fun doPlay(song: Song) {
        val artworkPath: String? = MusicData.findAlbumById(song.songAlbumId ?: 0)?.albumArtworkPath ?: song.explicitArtworkPath
        val artworkUri: Uri? = Uri.parse("file://$artworkPath")
        doPlay(song.uri, artworkUri)
    }

    abstract fun doPlay(fileUri: Uri, artworkUri: Uri?)

    ////////////
    // Resume //
    ////////////

    fun resume() {
        if (inHotswapTransaction) {
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

        if (time.toInt() > getCurrentPosInSong() || time.toInt() < getCurrentPosInSong()) doSeek(time)
    }

    abstract fun doSeek(time: Long)

    override fun onSeekTo(pos: Long) {
        seek(pos)
    }

    ///////////////////////////////////////////////////////////////////////////
    // PlaybackState
    ///////////////////////////////////////////////////////////////////////////

    internal var playbackState = STATE_NONE
        private set

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

    ///////////////////////////////////////////////////////////////////////////
    // State handling
    ///////////////////////////////////////////////////////////////////////////

    private fun passThroughPlaybackState() = SERVICE!!.updatePlaybackState()

    protected fun passThroughPlaybackProgress(progress: Long? = null) = SERVICE!!.updatePlaybackProgress(progress)

    /**
     * Update the MediaSession's state to buffering
     */
    protected fun notifyBuffering() {
        Log.d(TAG, "notifyBuffering()")
        playbackState = PlaybackStateCompat.STATE_BUFFERING
        passThroughPlaybackState()
    }

    /**
     * Update the MediaSession's state to error and trigger UI changes
     */
    protected fun notifyError() {
        Log.d(TAG, "notifyError()")
        playbackState = PlaybackStateCompat.STATE_ERROR
        passThroughPlaybackState()
        SERVICE!!.stopForeground(false)
        SERVICE!!.updateNotification(PlaybackRemote.makeNotification())
    }

    /**
     * Update the MediaSession's state to playing and trigger UI changes
     */
    protected fun notifyPlaying() {
        Log.d(TAG, "notifyPlaying()")
        playbackState = PlaybackStateCompat.STATE_PLAYING
        if (inHotswapTransaction) {
            inHotswapTransaction = false
            for (call in pendingCalls)
                call.invoke()
            pendingCalls.clear()
        }
        passThroughPlaybackState()
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
        passThroughPlaybackState()
        SERVICE!!.updateNotification(PlaybackRemote.makeNotification())
    }

    /**
     * Update the MediaSession's state to idle/none and trigger UI changes
     */
    protected fun notifyIdle() {
        Log.d(TAG, "notifyIdle()")
        playbackState = PlaybackStateCompat.STATE_NONE
        passThroughPlaybackState()
        SERVICE!!.stopForeground(true)
    }

}