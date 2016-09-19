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

package mobile.substance.sdk.music.core.objects

import android.net.Uri
import android.provider.MediaStore
import android.support.v4.media.MediaMetadataCompat

/**
 * Created by Adrian on 7/5/2015.
 */
class Artist : MediaObject() {

    ///////////////////////////////////////////////////////////////////////////
    // Overrides
    ///////////////////////////////////////////////////////////////////////////

    override val baseUri: Uri?
        get() = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI

    override fun toMetadataCompat(source: MediaMetadataCompat): MediaMetadataCompat {
        return MediaMetadataCompat.Builder(source)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, artistName)
                .putString(MediaMetadataCompat.METADATA_KEY_ART_URI, artistImagePath)
                .build()
    }

    ///////////////////////////////////////////////////////////////////////////
    //This manages the strings
    ///////////////////////////////////////////////////////////////////////////

    var artistName: String? = null

    var artistBio: String? = null

    ///////////////////////////////////////////////////////////////////////////
    //This manages the image
    ///////////////////////////////////////////////////////////////////////////

    var artistImagePath: String? = null

    ///////////////////////////////////////////////////////////////////////////
    //This manages all of the lists
    ///////////////////////////////////////////////////////////////////////////

    class Builder @JvmOverloads constructor(private val artist: Artist = Artist()) {

        fun setName(name: String): Builder {
            this.artist.artistName = name
            return this
        }

        fun setBio(bio: String): Builder {
            this.artist.artistBio = bio
            return this
        }

        fun setId(id: Long): Builder {
            this.artist.id = id
            return this
        }

        fun build(): Artist {
            return artist
        }

    }

}
