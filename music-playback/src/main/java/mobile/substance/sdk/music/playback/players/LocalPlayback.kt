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
import mobile.substance.sdk.music.core.utils.MusicCoreUtil
import mobile.substance.sdk.music.playback.HeadsetPlugReceiver
import mobile.substance.sdk.music.playback.MusicPlaybackUtil


object LocalPlayback : Playback(),
        MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, AudioManager.OnAudioFocusChangeListener {
    val TAG = "LocalPlayback"
    private var localPlayer: MediaPlayer = MediaPlayer()
    private var audioManager: AudioManager? = null
    private var wasPlayingBeforeAction = false

    override fun init() {
        audioManager = SERVICE!!.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    ///////////////////////////////////////////////////////////////////////////
    // MediaPlayer management
    ///////////////////////////////////////////////////////////////////////////

    fun MediaPlayer.destroy() {
        try {
            stop()
            reset()
            release()
            tripPlayerNecessity()
        } catch (ignored: Exception) {}
    }

    override fun createPlayer() {
        localPlayer = MediaPlayer()
    }

    override fun configPlayer() {
        localPlayer.setWakeMode(SERVICE, PowerManager.PARTIAL_WAKE_LOCK)
        localPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
        localPlayer.setOnPreparedListener(this)
        localPlayer.setOnCompletionListener(this)
        localPlayer.setOnErrorListener(this)

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

    override fun doPlay(uri: Uri, listenersAlreadyNotified: Boolean, mediaId: Long?) {
        //Stop the media player if a song is being played right now.
        if (isPlaying())
            localPlayer.stop()

        localPlayer.reset() // Necessary step to be able to setDataSource() again

        //Register the broadcast receiver
        HeadsetPlugReceiver register SERVICE!!

        //Notify the listeners if it hasn't already happened externally
        if (!listenersAlreadyNotified) {
            //TODO Work with listeners
        }

        //Start the service and do some work!
        try {
            val url = MusicPlaybackUtil.getUrlFromUri(uri)
            Log.d("Checking url validity", url.toString())
            if (url == null)
                localPlayer.setDataSource(SERVICE!!.applicationContext, uri)
            else {
                localPlayer.setDataSource(url)
                triggerStartBuffer()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Unable to play " + MusicCoreUtil.getFilePath(SERVICE!!, uri), e)
        } finally {
            localPlayer.prepareAsync()
        }
    }

    ////////////
    // Resume //
    ////////////

    override fun doResume() = doResume(true)

    fun doResume(updateFocus: Boolean) {
        if (!isPlaying()) {
            if (!updateFocus or requestAudioFocus()) {
                SERVICE!!.startProgressThread()
                localPlayer.start()
                SERVICE!!.startForeground()
            } else Log.e(TAG, "AudioFocus denied")
        } else Log.e(TAG, "It seems like resume was called while you are playing. It is recommended you do some debugging.")
    }

    ///////////
    // Pause //
    ///////////

    override fun doPause() = doPause(true)

    fun doPause(updateFocus: Boolean) {
        if (updateFocus) giveUpAudioFocus()
        localPlayer.pause()
        HeadsetPlugReceiver unregister SERVICE!!
        SERVICE!!.stopForeground(false)
        SERVICE!!.shutdownProgressThread()
    }

    //////////
    // Stop //
    //////////

    override fun doStop() = doStop(true)

    fun doStop(updateFocus: Boolean) {
        if (updateFocus) giveUpAudioFocus()
        localPlayer.destroy()
    }

    //////////
    // Seek //
    //////////

    override fun doSeek(time: Long) {
        val to = time.toInt()
        wasPlayingBeforeAction = isPlaying()
        localPlayer.pause()
        if (to >= localPlayer.duration) localPlayer.seekTo(to) else next()
        if (wasPlayingBeforeAction) localPlayer.start()
    }

    ////////////
    // Repeat //
    ////////////

    override fun setRepeating(repeating: Boolean) {
        localPlayer.isLooping = repeating
    }

    ///////////////////////////////////////////////////////////////////////////
    // Media Player Callbacks
    // STATUS: TODO
    ///////////////////////////////////////////////////////////////////////////

    override fun onPrepared(mp: MediaPlayer?) {
        triggerEndBuffer()
        resume()
    }

    //Not checking it it is looping because onCompletion is never actually called if it is looping.
    override fun onCompletion(mp: MediaPlayer?) = next()

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        SERVICE!!.shutdownProgressThread()
        //TODO: Handle this with some some sort of callback
        return true
    }

    ///////////////////////////////////////////////////////////////////////////
    // Audio Focus
    ///////////////////////////////////////////////////////////////////////////

    fun requestAudioFocus() = (audioManager!!.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)).equals(AudioManager.AUDIOFOCUS_REQUEST_GRANTED)

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

    private fun setDucking(duck: Boolean) = localPlayer.setVolume(if (duck) 0.5f else 1.0f, if (duck) 0.5f else 1.0f)

    ///////////////////////////////////////////////////////////////////////////
    // Misc.
    // STATUS: COMPLETE
    ///////////////////////////////////////////////////////////////////////////

    override fun isPlaying() = localPlayer.isPlaying

    override fun isRepeating() = localPlayer.isLooping

    override fun getCurrentPosInSong(): Int {
        try {
            return localPlayer.currentPosition
        } catch (e: Exception) {
            Log.e(TAG, "failed retrieving the current position, returning 0", e)
            return 0
        }
    }
}