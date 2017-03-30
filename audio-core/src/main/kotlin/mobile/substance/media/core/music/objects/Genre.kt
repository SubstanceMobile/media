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

package mobile.substance.media.core.music.objects

import android.net.Uri
import android.provider.MediaStore
import android.support.v4.media.MediaMetadataCompat

class Genre : mobile.substance.media.core.MediaObject() {

    ///////////////////////////////////////////////////////////////////////////
    // Overrides
    ///////////////////////////////////////////////////////////////////////////

    override val baseUri: Uri?
        get() = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI

    override fun toMetadataCompat(source: MediaMetadataCompat): MediaMetadataCompat {
        return MediaMetadataCompat.Builder(source)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, genreName)
                .build()
    }

    ///////////////////////////////////////////////////////////////////////////
    // Name
    ///////////////////////////////////////////////////////////////////////////

    var genreName: String? = null

    ///////////////////////////////////////////////////////////////////////////
    // Builder
    ///////////////////////////////////////////////////////////////////////////

    class Builder @JvmOverloads constructor(private val genre: Genre = Genre()) {

        fun setName(genreName: String): Builder {
            this.genre.genreName = genreName
            return this
        }

        fun setId(genreId: Long): Builder {
            this.genre.id = genreId
            return this
        }

        fun build(): Genre {
            return genre
        }
    }

}
