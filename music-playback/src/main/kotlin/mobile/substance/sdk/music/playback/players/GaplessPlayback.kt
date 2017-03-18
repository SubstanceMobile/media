package mobile.substance.sdk.music.playback.players

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.PowerManager
import android.os.ResultReceiver
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import mobile.substance.sdk.music.playback.PlaybackRemote
import mobile.substance.sdk.music.playback.destroy
import mobile.substance.sdk.music.playback.prepareWithDataSource
import mobile.substance.sdk.music.playback.service.MusicQueue
import mobile.substance.sdk.utils.MusicCoreUtil

object GaplessPlayback : Playback(),
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener,
        AudioManager.OnAudioFocusChangeListener {
    val TAG = GaplessPlayback::class.java.simpleName
    private var players = arrayOfNulls<MediaPlayer>(2)
    private var isPrepared = false
    private var activePlayerIndex = 0
    private var audioManager: AudioManager? = null
    private var wasPlayingBeforeAction = false

    private fun getActivePlayer(): MediaPlayer? = players[activePlayerIndex]
    private fun getInactivePlayer(): MediaPlayer? = players[if (activePlayerIndex == 0) 1 else 0]

    override fun init() {
        audioManager = SERVICE!!.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    ///////////////////////////////////////////////////////////////////////////
    // MediaPlayer management
    ///////////////////////////////////////////////////////////////////////////

    override fun createPlayer() {
        players[0] = MediaPlayer()
        players[1] = MediaPlayer()
    }

    override fun configPlayer() {
        players.forEach {
            it?.apply {
                setWakeMode(SERVICE, PowerManager.PARTIAL_WAKE_LOCK)
                setAudioStreamType(AudioManager.STREAM_MUSIC)
                setOnPreparedListener(this@GaplessPlayback)
                setOnCompletionListener(this@GaplessPlayback)
                setOnErrorListener(this@GaplessPlayback)
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Controls
    ///////////////////////////////////////////////////////////////////////////

    ////////////////
    // Play TODO //
    ///////////////

    override fun doPlay(fileUri: Uri, artworkUri: Uri?) {
        // Switch the active player
        switchPlayers()

        if (!isPrepared) {
            getActivePlayer()?.prepareWithDataSource(SERVICE!!, fileUri)
        } else {
            isPrepared = false
            doResume()
        }

        //Stop the old media player if a song is being played right now.
        if (isPlaying())
            getInactivePlayer()?.stop()

        getInactivePlayer()?.reset() // Reset the old MediaPlayeer, necessary to be able to setDataSource() again

        if (shouldPrepareNext()) {
            println("GaplessPlayback.kt is preparing the next song...")
            if (repeatMode == PlaybackStateCompat.REPEAT_MODE_ONE) {
                getInactivePlayer()?.prepareWithDataSource(SERVICE!!, fileUri)
            } else if (!MusicQueue.isLastPosition() || repeatMode == PlaybackStateCompat.REPEAT_MODE_ALL) {
                getInactivePlayer()?.prepareWithDataSource(SERVICE!!, PlaybackRemote.getNextSong()!!.uri)
            }
        }
    }

    private fun switchPlayers() {
        activePlayerIndex = if (activePlayerIndex == 0) 1 else 0
    }

    private fun shouldPrepareNext(): Boolean = (!MusicQueue.isLastPosition() || repeatMode == PlaybackStateCompat.REPEAT_MODE_ALL) || (repeatMode == PlaybackStateCompat.REPEAT_MODE_ONE)

    ////////////
    // Resume //
    ////////////

    override fun doResume() = doResume(true)

    fun doResume(updateFocus: Boolean) {
        if (!isPlaying()) {
            if (!updateFocus or requestAudioFocus()) {
                getActivePlayer()?.start()
                notifyPlaying()
                startProgressThread()
            } else Log.e(TAG, "AudioFocus denied")
        } else Log.e(TAG, "It seems like resume was called while you are playing. It is recommended you do some debugging.")
    }

    ///////////
    // Pause //
    ///////////

    override fun doPause() = doPause(true)

    fun doPause(updateFocus: Boolean) {
        if (updateFocus) giveUpAudioFocus()
        getActivePlayer()?.pause()
        notifyPaused()
        shutdownProgressThread()
    }

    //////////
    // Stop //
    //////////

    override fun doStop() = doStop(true)

    fun doStop(updateFocus: Boolean) {
        if (updateFocus) giveUpAudioFocus()
        players.forEach {
            it?.destroy()
        }
        shutdownProgressThread()
        notifyIdle()
    }

    //////////
    // Seek //
    //////////

    override fun doSeek(time: Long) {
        val to = time.toInt()
        wasPlayingBeforeAction = isPlaying()
        getActivePlayer()?.pause()
        if (to <= getActivePlayer()!!.duration) {
            getActivePlayer()?.seekTo(to)
            if (wasPlayingBeforeAction) getActivePlayer()?.start()
        } else next()
    }

    ///////////////////////////////////////////////////////////////////////////
    // Progress Thread
    ///////////////////////////////////////////////////////////////////////////

    private var progressThread: Thread? = null

    class ProgressRunnable : Runnable {
        override fun run() {
            while (!Thread.interrupted()) {
                try {
                    Thread.sleep(500)
                } catch (e: InterruptedException) {
                    break
                }
                try {
                    passThroughPlaybackProgress()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Thread.currentThread().interrupt()
                }
            }
        }
    }

    internal fun startProgressThread() {
        if (progressThread == null) progressThread = Thread(ProgressRunnable()) else if (progressThread?.isAlive ?: false) return
        progressThread?.start()
    }

    internal fun shutdownProgressThread() {
        if (progressThread == null) return
        if (!(progressThread?.isInterrupted ?: false))
            progressThread?.interrupt()
        progressThread = null
    }

    ///////////////////////////////////////////////////////////////////////////
    // Media Player Callbacks
    // STATUS: TODO
    ///////////////////////////////////////////////////////////////////////////

    override fun onPrepared(mp: MediaPlayer?) {
        if (mp == getInactivePlayer()) {
            isPrepared = true
        } else doResume()
    }

    //Not checking it it is looping because onCompletion is never actually called if it is looping.
    override fun onCompletion(mp: MediaPlayer?) {
        if (!isPrepared) notifyIdle()
        shutdownProgressThread()
        next()
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        notifyError()
        shutdownProgressThread()
        //TODO: Handle this with some some sort of callback
        return true
    }

    ///////////////////////////////////////////////////////////////////////////
    // Audio Focus
    ///////////////////////////////////////////////////////////////////////////

    fun requestAudioFocus() = (audioManager!!.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED

    fun giveUpAudioFocus() = audioManager!!.abandonAudioFocus(this)

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                if (wasPlayingBeforeAction) doResume(false)
                setDucking(false)
            }
            AudioManager.AUDIOFOCUS_LOSS -> if (isPlaying()) doPause(false)
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> if (isPlaying()) {
                wasPlayingBeforeAction = true
                doPause(false)
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                setDucking(true)
            }
        }
    }

    private fun setDucking(duck: Boolean) = getActivePlayer()?.setVolume(if (duck) 0.5f else 1.0f, if (duck) 0.5f else 1.0f)

    ///////////////////////////////////////////////////////////////////////////
    // Misc.
    // STATUS: COMPLETE
    ///////////////////////////////////////////////////////////////////////////

    override fun isPlaying(): Boolean {
        try {
            return getActivePlayer()?.isPlaying ?: false
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            return false
        }
    }

    override fun getCurrentPosition(): Int {
        try {
            return getActivePlayer()?.currentPosition ?: 0
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            return 0
        }
    }
}