package mobile.substance.sdk.music.playback

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.util.Log
import mobile.substance.sdk.music.core.CoreUtil
import mobile.substance.sdk.music.playback.players.Playback


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
        stop()
        reset()
        release()
        tripPlayerNecessity()
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
            localPlayer.playbackParams = PlaybackParams().setSpeed(getPlaybackSpeed()).allowDefaults()
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Controls
    ///////////////////////////////////////////////////////////////////////////

    ////////////////
    // Play TODO //
    ///////////////

    override fun doPlay(uri: Uri, listenersAlreadyNotified: Boolean) {
        //Clear out the media player if a song is being played right now.
        if (isPlaying()) {
            localPlayer.stop()
            localPlayer.reset()
        }

        //Register the broadcast receiver
        HeadsetPlugReceiver register SERVICE!!

        //Notify the listeners if it hasn't already happened externally
        if (!listenersAlreadyNotified) {
            //TODO Work with listeners
        }

        //Start the service and do some work!
        try {
            val url = MusicPlaybackUtil.getUrlFromUri(uri)
            if (url.equals(""))
                localPlayer.setDataSource(SERVICE!!.applicationContext, uri)
            else {
                localPlayer.setDataSource(url)
                triggerStartBuffer()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Unable to play " + CoreUtil.getFilePath(SERVICE, uri), e)
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
            if (!updateFocus and requestAudioFocus()) {
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
        SERVICE!!.kill()
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

    override fun repeat(repeating: Boolean) {
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

    override fun isInitialized() = SERVICE != null

    override fun getCurrentPosInSong() = localPlayer.currentPosition
}