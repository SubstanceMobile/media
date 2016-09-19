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
class Playlist : MediaObject() {

    ///////////////////////////////////////////////////////////////////////////
    // Overrides
    ///////////////////////////////////////////////////////////////////////////

    override val baseUri: Uri?
        get() = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI

    override fun toMetadataCompat(source: MediaMetadataCompat): MediaMetadataCompat {
        return MediaMetadataCompat.Builder(source)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, playlistName)
                .build()
    }

    ///////////////////////////////////////////////////////////////////////////
    // Title
    ///////////////////////////////////////////////////////////////////////////

    var playlistName: String? = null

    ///////////////////////////////////////////////////////////////////////////
    // Builder
    ///////////////////////////////////////////////////////////////////////////

    class Builder @JvmOverloads constructor(private val playlist: Playlist = Playlist()) {

        fun setName(name: String): Builder {
            this.playlist.playlistName = name
            return this
        }

        fun setId(id: Long): Builder {
            this.playlist.id = id
            return this
        }

        fun build(): Playlist {
            return playlist
        }

    }

}
