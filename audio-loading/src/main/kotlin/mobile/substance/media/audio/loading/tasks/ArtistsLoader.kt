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

package mobile.substance.media.audio.loading.tasks

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import mobile.substance.media.audio.loading.objects.MediaStoreArtist
import mobile.substance.media.loading.core.MediaLoader

class ArtistsLoader(context: Context) : MediaLoader<MediaStoreArtist>(context) {

    override fun buildObject(cursor: Cursor): MediaStoreArtist? {
        val name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST))
        val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Artists._ID))
        val numberOfSongs = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS))
        val numberOfAlbums = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS))

        return MediaStoreArtist().apply {
            this.name = name
            this.id = id
            this.numberOfSongs = numberOfSongs
            this.numberOfAlbums = numberOfAlbums
        }
    }

    override val uri: Uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI

    override val loaderId: Int = 12

}
