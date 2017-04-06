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

import android.support.v4.media.MediaMetadataCompat
import mobile.substance.media.core.MediaObject

abstract class Genre : AudioObject() {
    open var name: String? = null
    open var numberOfSongs: Int? = null
    open var numberOfAlbums: Int? = null

    abstract fun getSongs(): List<Song>?

    abstract fun getAlbums(): List<Album>?

    override fun MediaMetadataCompat.Builder.withMetadata(): MediaMetadataCompat.Builder {
        putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, name)
        putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, numberOfSongs?.toLong() ?: 0L)
        return this
    }

}