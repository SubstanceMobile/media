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

package mobile.substance.media.core.audio

import mobile.substance.media.core.MediaObject
import mobile.substance.media.utils.AudioCoreUtil

abstract class Song : MediaObject(), AudioFile {
    open var title: String? = null
    open var artistName: String? = null
    open var albumTitle: String? = null
    open var duration: Long? = null
    open var year: Long? = null
    open var trackNumber: Int? = null
    open var lyrics: String? = null
    open var artworkUri: String? = null
    val formattedDuration: String
        get() = AudioCoreUtil.stringForTime(duration ?: 0L)

    abstract fun getArtist(): Artist?

    abstract fun getAlbum(): Album?

    abstract fun getGenre(): Genre?
}