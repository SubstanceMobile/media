package mobile.substance.sdk.music.playback

import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import mobile.substance.sdk.music.core.objects.Song
import mobile.substance.sdk.music.loading.Library

abstract class Playback : MediaSessionCompat.Callback() {
    companion object {
        val TAG = "Playback"
        val ACTION_PLAY_FROM_LIST = "SUBSTANCE_SDK_SET_REPEAT_" + MusicService.UNIQUE_ID
        val ACTION_SET_REPEAT = "SUBSTANCE_SDK_SET_REPEAT_" + MusicService.UNIQUE_ID
    }

    var SERVICE: MusicService? = null;
        get() = field!!

    fun init(service : MusicService) {
        SERVICE = service
    }

    abstract fun init()

    ///////////////////////////////////////////////////////////////////////////
    // Play
    ///////////////////////////////////////////////////////////////////////////

    abstract fun play()

    abstract fun play(uri: Uri, listenersAlreadyNotified: Boolean)

    abstract fun play(song: Song)

    abstract fun resume()

    override fun onPlay() {
        resume()
    }

    override fun onPlayFromUri(uri: Uri?, extras: Bundle?) {
        play(uri, PlaybackRemote.tempNotifyListener)
    }

    override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
        play(Library.findSongById(java.lang.Long.valueOf(mediaId!!)!!))
    }

    override fun onPlayFromSearch(query: String?, extras: Bundle?) {
        //TODO
    }

    ///////////////////////////////////////////////////////////////////////////
    // Pause
    ///////////////////////////////////////////////////////////////////////////

    abstract fun pause()

    override fun onPause() {
        pause()
    }

    ///////////////////////////////////////////////////////////////////////////
    // Next
    ///////////////////////////////////////////////////////////////////////////

    abstract fun next()

    override fun onSkipToNext() {
        next()
    }

    ///////////////////////////////////////////////////////////////////////////
    // Previous
    ///////////////////////////////////////////////////////////////////////////

    abstract fun doPrev()

    abstract fun restart()

    /**
     * Override this to set a new value. When the player is requested to skip back a song, it gets the current playing position.
     * If the position is greater then the value returned here, then it restarts the song, otherwise it actually skips back. This can be disabled entirely be returning -1
     */
    fun restartOnPrevWhen(): Long = 5000

    private fun shouldSkipBack() = getCurrentPosInSong() <= restartOnPrevWhen()

    override fun onSkipToPrevious() {
        if (shouldSkipBack()) {
            Log.d(Companion.TAG, "onSkipToPrevious() called. Calling doPrev()")
            doPrev()
        } else {
            Log.d(Companion.TAG, "onSkipToPrevious() called. Calling prevRestart()")
            restart()
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Stop
    ///////////////////////////////////////////////////////////////////////////

    abstract fun stop()

    override fun onStop() {
        stop()
    }

    ///////////////////////////////////////////////////////////////////////////
    // Seek
    ///////////////////////////////////////////////////////////////////////////

    abstract fun seek(time: Long)

    override fun onSeekTo(pos: Long) {
        seek(pos)
    }

    ///////////////////////////////////////////////////////////////////////////
    // PlaybackState
    ///////////////////////////////////////////////////////////////////////////

    var playbackState = PlaybackStateCompat.STATE_NONE
        get

    ///////////////////////////////////////////////////////////////////////////
    // Others
    ///////////////////////////////////////////////////////////////////////////

    abstract fun repeat(repeating: Boolean)

    abstract fun isPlaying(): Boolean

    abstract fun isRepeating(): Boolean

    abstract fun isInitialized(): Boolean

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