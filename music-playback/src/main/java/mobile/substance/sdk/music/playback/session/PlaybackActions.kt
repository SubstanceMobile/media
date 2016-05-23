package mobile.substance.sdk.music.playback.session

import android.support.v4.media.session.PlaybackStateCompat.*

/**
 * This is a very simple interface you can override to include your own actions. It is recommended you set this and use it.
 */
interface PlaybackActions {
    fun getActions() = ACTION_PLAY_PAUSE or ACTION_PLAY or ACTION_PAUSE
}