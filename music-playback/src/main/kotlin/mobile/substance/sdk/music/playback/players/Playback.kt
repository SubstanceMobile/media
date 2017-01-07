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

package mobile.substance.sdk.music.playback.players

import android.content.ContentUris
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.STATE_NONE
import android.util.Log
import android.widget.Toast
import mobile.substance.sdk.music.core.dataLinkers.MusicData
import mobile.substance.sdk.music.core.objects.*
import mobile.substance.sdk.utils.MusicCoreUtil
import mobile.substance.sdk.music.playback.PlaybackRemote
import mobile.substance.sdk.music.playback.RepeatModes
import mobile.substance.sdk.music.playback.service.MusicQueue
import mobile.substance.sdk.music.playback.service.MusicService
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
        createPlayer()
        configPlayer()
    }

    abstract fun init()

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

    fun play() = play(MusicQueue.getCurrentSong()!!)

    fun play(song: Song) {
        doPlay(song)
        if (!(SERVICE!!.getMediaSession()?.isActive ?: false)) SERVICE!!.getMediaSession()?.isActive = true
        SERVICE!!.callback {
            onSongChanged(song)
            onDurationChanged(song.songDuration?.toInt() ?: 0, song.songDurationString)
        }
    }

    private fun play(uri: Uri) {
        var mediaId: Long? = null
        if (uri.scheme == "content") mediaId = MusicCoreUtil.findByMediaId(ContentUris.parseId(uri), MusicData.getAlbums(), MusicData.getSongs())?.id
        val song = MusicData.findSongById(mediaId ?: 0)
        return if (song != null) play(song) else doPlay(uri, null)
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
        val artworkPath: String? = MusicData.findAlbumById(song.songAlbumId ?: 0)?.albumArtworkPath
        doPlay(song.uri, if (artworkPath == null) song.explicitArtworkUri else Uri.parse("file://$artworkPath"))
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

    override fun onPlayFromSearch(query: String, extras: Bundle) {
        var songs: List<Song>? = null
        try {
            when (extras.get(MediaStore.EXTRA_MEDIA_FOCUS)) {
                MediaStore.Audio.Media.ENTRY_CONTENT_TYPE -> songs = MusicData.search(extras.getString(MediaStore.EXTRA_MEDIA_TITLE))
                MediaStore.Audio.Albums.ENTRY_CONTENT_TYPE -> songs = MusicData.findSongsForAlbum(MusicData.search<Album>(extras.getString(MediaStore.EXTRA_MEDIA_ALBUM))!!.first())
                MediaStore.Audio.Artists.ENTRY_CONTENT_TYPE -> songs = MusicData.findSongsForArtist(MusicData.search<Artist>(extras.getString(MediaStore.EXTRA_MEDIA_ARTIST))!!.first())
                MediaStore.Audio.Playlists.ENTRY_CONTENT_TYPE -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) songs = MusicData.findSongsForPlaylist(MusicData.search<Playlist>(extras.getString(MediaStore.EXTRA_MEDIA_PLAYLIST))!!.first())
                }
                MediaStore.Audio.Genres.ENTRY_CONTENT_TYPE -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) songs = MusicData.findSongsForGenre(MusicData.search<Genre>(extras.getString(MediaStore.EXTRA_MEDIA_GENRE))!!.first())
                }
                else -> songs = MusicData.search(query)
            }
        } catch (e: KotlinNullPointerException) {
            e.printStackTrace()
            Toast.makeText(SERVICE, "No music found for query", Toast.LENGTH_SHORT).show()
        } finally {
            if (songs != null && songs.isNotEmpty()) PlaybackRemote.play(songs, 0)
        }
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

    /**
     * This also handles the next button with repeat. This way, calling next() will repeat if repeatMode equals [RepeatModes.REPEAT_ENABLED]. This can be turned off by overriding
     * repeatOnNext to be false.
     */
    fun next() {
        when (repeatMode) {
            RepeatModes.REPEAT_DISABLED -> doNext()
            RepeatModes.REPEAT_ENABLED -> if (repeatOnNext) play() else {
                repeatMode = RepeatModes.REPEAT_DISABLED
                doNext()
            }
            RepeatModes.REPEAT_ONCE -> {
                play()
                repeatMode = RepeatModes.REPEAT_DISABLED
            }
        }
    }

    open fun doNext() = PlaybackRemote.playNextInternal()

    override fun onSkipToNext() = next()

    ///////////////////////////////////////////////////////////////////////////
    // Previous
    // TODO STATE STUFF
    ///////////////////////////////////////////////////////////////////////////

    open fun doPrev() = PlaybackRemote.playPreviousInternal()

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

    var repeatMode = RepeatModes.REPEAT_DISABLED
        set(value) {
            field = value
            onRepeatModeChanged(value)
            notifyRepeatModeChanged()
        }

    protected open fun onRepeatModeChanged(mode: Int) = Unit

    open val repeatOnNext = false

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

    private fun passThroughPlaybackState(callback: Boolean = false) = SERVICE!!.updatePlaybackState(callback)

    protected fun passThroughPlaybackProgress(progress: Long? = null) = SERVICE!!.updatePlaybackProgress(progress)

    protected fun notifyRepeatModeChanged() {
        SERVICE!!.callback { onRepeatingChanged(repeatMode) }
    }

    /**
     * Update the MediaSession's state to buffering
     */
    protected fun notifyBuffering() {
        Log.d(TAG, "notifyBuffering()")
        playbackState = PlaybackStateCompat.STATE_BUFFERING
        passThroughPlaybackState(true)
    }

    /**
     * Update the MediaSession's state to error and trigger UI changes
     */
    protected fun notifyError() {
        Log.d(TAG, "notifyError()")
        playbackState = PlaybackStateCompat.STATE_ERROR
        passThroughPlaybackState(true)
        SERVICE!!.stopForeground(false)
        SERVICE!!.notify(PlaybackRemote.makeNotification())
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
        passThroughPlaybackState(true)
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
        passThroughPlaybackState(true)
        SERVICE!!.notify(PlaybackRemote.makeNotification())
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