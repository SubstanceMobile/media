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

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.PowerManager
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import mobile.substance.media.audio.playback.PlaybackRemote
import mobile.substance.media.audio.playback.destroy
import mobile.substance.media.audio.playback.prepareWithDataSource
import mobile.substance.media.audio.playback.service.AudioQueue
import mobile.substance.media.core.audio.Song

object GaplessPlayback : Playback(),
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener,
        AudioManager.OnAudioFocusChangeListener {
    val TAG = GaplessPlayback::class.java.simpleName
    private var players = arrayOfNulls<MediaPlayer>(2)
    private var preparedSong: Uri? = null
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

    //////////
    // Play //
    //////////

    override fun doPlay(fileUri: Uri, artworkUri: Uri?) {
        println("doPlay()")

        if (getActivePlayer()?.isPlaying ?: false) getActivePlayer()?.stop()
        if (getInactivePlayer()?.isPlaying ?: false) getInactivePlayer()?.stop()

        getActivePlayer()?.reset()
        getInactivePlayer()?.reset()

        getActivePlayer()?.prepareWithDataSource(SERVICE!!, fileUri)

        if (shouldPrepareNext()) {
            println("doPlay() preparing the next player")
            prepareNextPlayer(fileUri)
        }
    }

    private fun switchPlayers() {
        activePlayerIndex = if (activePlayerIndex == 0) 1 else 0
    }

    private fun shouldPrepareNext(): Boolean = (!AudioQueue.isLastPosition() || repeatMode == PlaybackStateCompat.REPEAT_MODE_ALL) || (repeatMode == PlaybackStateCompat.REPEAT_MODE_ONE)

    private fun prepareNextPlayer(currentUri: Uri) {
        println("GaplessPlayback.kt is preparing the next song...")
        if (repeatMode == PlaybackStateCompat.REPEAT_MODE_ONE) {
            preparedSong = currentUri
            getInactivePlayer()?.prepareWithDataSource(SERVICE!!, currentUri)
            println("GaplessPlayback.kt has prepared the current song again")
        } else if (!AudioQueue.isLastPosition() || repeatMode == PlaybackStateCompat.REPEAT_MODE_ALL) {
            preparedSong = PlaybackRemote.getNextSong()!!.uri
            getInactivePlayer()?.prepareWithDataSource(SERVICE!!, PlaybackRemote.getNextSong()!!.uri)
            println("GaplessPlayback.kt has prepared the next song in the queue")
        }
    }

    // We'll probably have prepared the next song already, a change of the repeat mode invalidates this
    override fun onSetRepeatMode(repeatMode: Int) {
        super.onSetRepeatMode(repeatMode)
        invalidateNextPlayer()
    }

    // We'll probably have prepared the next song already, a change of the queue invalidates this
    override fun onQueueChanged() = invalidateNextPlayer()

    private fun invalidateNextPlayer() {
        if (preparedSong != PlaybackRemote.getNextSong()?.uri) return
        getInactivePlayer()?.reset()
        preparedSong = PlaybackRemote.getCurrentSong()?.uri
        if (shouldPrepareNext()) prepareNextPlayer(preparedSong!!)
    }

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
                    dispatchPlaybackProgress()
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
    // MediaCore Player Callbacks
    ///////////////////////////////////////////////////////////////////////////

    override fun onPrepared(mp: MediaPlayer?) {
        if (mp == getActivePlayer()) {
            println("onPrepared() the active player, starting it")
            doResume()
        } else {
            println("onPrepared() the inactive player. Setting it as next")
            getActivePlayer()?.setNextMediaPlayer(mp)
        }
    }

    override fun onCompletion(mp: MediaPlayer?) {
        if (!shouldPrepareNext()) {
            next()
        } else {
            notifyPlaying()
            if (repeatMode != PlaybackStateCompat.REPEAT_MODE_ONE) {
                println("onCompletion() moving forward")
                AudioQueue.moveForward(1)
                dispatchOnSongChanged(PlaybackRemote.getCurrentSong()!!)
            }
            switchPlayers()

            getInactivePlayer()?.reset()

            if (shouldPrepareNext()) {
                println("onCompletion() preparing the next song")
                prepareNextPlayer(preparedSong!!)
            }
        }
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        notifyError()
        shutdownProgressThread()
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
        } catch (ignored: IllegalStateException) {
            return false
        }
    }

    override fun getCurrentPosition(): Int {
        try {
            return getActivePlayer()?.currentPosition ?: 0
        } catch (ignored: IllegalStateException) {
            return 0
        }
    }
}