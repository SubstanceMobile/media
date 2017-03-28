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

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.util.Log
import mobile.substance.sdk.music.core.objects.Song
import mobile.substance.sdk.utils.MusicCoreUtil
import mobile.substance.sdk.music.playback.destroy


object LocalPlayback : Playback(),
        MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, AudioManager.OnAudioFocusChangeListener {
    val TAG = "LocalPlayback"
    private var localPlayer: MediaPlayer? = null
    private var audioManager: AudioManager? = null
    private var wasPlayingBeforeAction = false

    override fun init() {
        audioManager = SERVICE!!.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    ///////////////////////////////////////////////////////////////////////////
    // MediaPlayer management
    ///////////////////////////////////////////////////////////////////////////

    override fun createPlayer() {
        localPlayer = MediaPlayer()
    }

    override fun configPlayer() {
        localPlayer?.setWakeMode(SERVICE, PowerManager.PARTIAL_WAKE_LOCK)
        localPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
        localPlayer?.setOnPreparedListener(this)
        localPlayer?.setOnCompletionListener(this)
        localPlayer?.setOnErrorListener(this)

        //API 23+ playback speed API
        if (Build.VERSION.SDK_INT >= 23) {
            // localPlayer.playbackParams = PlaybackParams().setSpeed(getPlaybackSpeed()).allowDefaults() TODO: throws java.lang.IllegalStateException
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Controls
    ///////////////////////////////////////////////////////////////////////////

    ////////////////
    // Play TODO //
    ///////////////

    override fun doPlay(fileUri: Uri, artworkUri: Uri?) {
        //Stop the media player if a song is being played right now.
        if (isPlaying())
            localPlayer?.stop()

        localPlayer?.reset() // Necessary step to be able to setDataSource() again

        //Start the service and do some work!
        try {
            val url = fileUri.toString()
            Log.d("Checking url validity", url)
            if (!MusicCoreUtil.isHttpUrl(url)) localPlayer?.setDataSource(SERVICE!!.applicationContext, fileUri) else localPlayer?.setDataSource(url)
        } catch (e: Exception) {
            Log.e(TAG, "Unable to play " + MusicCoreUtil.getFilePath(SERVICE!!, fileUri), e)
        } finally {
            notifyBuffering()
            localPlayer?.prepareAsync()
        }
    }

    ////////////
    // Resume //
    ////////////

    override fun doResume() = doResume(true)

    fun doResume(updateFocus: Boolean) {
        if (!isPlaying()) {
            if (!updateFocus or requestAudioFocus()) {
                localPlayer?.start()
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
        localPlayer?.pause()
        notifyPaused()
        shutdownProgressThread()
    }

    //////////
    // Stop //
    //////////

    override fun doStop() = doStop(true)

    fun doStop(updateFocus: Boolean) {
        if (updateFocus) giveUpAudioFocus()
        localPlayer?.destroy()
        shutdownProgressThread()
        notifyIdle()
    }

    //////////
    // Seek //
    //////////

    override fun doSeek(time: Long) {
        val to = time.toInt()
        wasPlayingBeforeAction = isPlaying()
        localPlayer?.pause()
        if (to <= localPlayer!!.duration) {
            localPlayer?.seekTo(to)
            if (wasPlayingBeforeAction) localPlayer?.start()
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
    // Media Player Callbacks
    // STATUS: TODO
    ///////////////////////////////////////////////////////////////////////////

    override fun onPrepared(mp: MediaPlayer?) = doResume()

    //Not checking it it is looping because onCompletion is never actually called if it is looping.
    override fun onCompletion(mp: MediaPlayer?) {
        notifyIdle()
        shutdownProgressThread()
        next()
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

    private fun setDucking(duck: Boolean) = localPlayer?.setVolume(if (duck) 0.5f else 1.0f, if (duck) 0.5f else 1.0f)

    ///////////////////////////////////////////////////////////////////////////
    // Misc.
    // STATUS: COMPLETE
    ///////////////////////////////////////////////////////////////////////////

    override fun isPlaying(): Boolean {
        try {
            return localPlayer?.isPlaying ?: false
        } catch (ignored: IllegalStateException) {
            return false
        }
    }

    override fun getCurrentPosition(): Int {
        try {
            return localPlayer?.currentPosition ?: 0
        } catch (ignored: IllegalStateException) {
            return 0
        }
    }
}