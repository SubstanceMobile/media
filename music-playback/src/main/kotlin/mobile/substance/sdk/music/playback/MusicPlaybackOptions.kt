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

    var defaultCallback: PlaybackRemote.RemoteCallback? = null

    //Cast
    var castApplicationId = "null"
    var isCastEnabled = false
}