package mobile.substance.sdk.music.playback

import android.support.v4.media.session.PlaybackStateCompat

/**
 * For configuring the Playback Library
 */
object MusicPlaybackOptions {

    /**
     * This is a very simple interface you can override to include your own actions. It is recommended you set this and use it.
     */
    interface PlaybackActions {
        fun getActions() = PlaybackStateCompat.ACTION_PLAY_PAUSE or PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PAUSE
    }

    var playbackActions = object : PlaybackActions {}

    var statusbarIconResId = 0

    //Cast
    var castApplicationId = "null"
    var isCastEnabled = false

    var useHeadsetPlugInsteadOfAudioNoisy = false
}