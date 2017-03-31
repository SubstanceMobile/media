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

package mobile.substance.media.options

import android.support.v4.media.session.PlaybackStateCompat
import mobile.substance.media.music.playback.PlaybackRemote

/**
 * For configuring the Playback MediaStoreAudioHolder
 */
object AudioPlaybackOptions {

    /**
     * This is a very simple interface you can override to include your own actions. It is recommended you set this and use it.
     */
    interface PlaybackActions {
        fun getActions() = PlaybackStateCompat.ACTION_PLAY_PAUSE or
                PlaybackStateCompat.ACTION_PLAY or
                PlaybackStateCompat.ACTION_PAUSE or
                PlaybackStateCompat.ACTION_STOP or
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID or
                PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH or
                PlaybackStateCompat.ACTION_PLAY_FROM_URI or
                PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE_ENABLED or
                PlaybackStateCompat.ACTION_SET_REPEAT_MODE or
                PlaybackStateCompat.ACTION_SEEK_TO
    }

    var isGaplessPlaybackEnabled = false
    var playbackActions = object : PlaybackActions {}

    var statusbarIconResId = 0

    var defaultCallback: PlaybackRemote.RemoteCallback? = null

    // Lockscreen artwork
    var isLockscreenArtworkEnabled = true
    var isLockscreenArtworkBlurEnabled = false

    // Cast
    var castApplicationId = "null"
    var isCastEnabled = false
}