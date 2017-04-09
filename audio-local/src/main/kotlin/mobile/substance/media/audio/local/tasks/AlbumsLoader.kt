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

package mobile.substance.media.audio.local.tasks

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import mobile.substance.media.audio.local.objects.MediaStoreAlbum
import mobile.substance.media.local.core.MediaLoader

class AlbumsLoader(context: Context) : MediaLoader<MediaStoreAlbum>(context) {

    override fun buildObject(cursor: Cursor): MediaStoreAlbum? {
        val title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM))
        val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Albums._ID))
        val artistName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST))
        val numberOfSongs = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS))
        val year = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.LAST_YEAR))
        val artworkUri = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART))

        return MediaStoreAlbum().apply {
            this.title = title
            this.id = id
            this.artistName = artistName
            this.numberOfSongs = numberOfSongs
            this.year = year
            this.artworkUri = Uri.parse("file://" + artworkUri)
        }
    }

    override val uri: Uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI

    override val sortOrder: String? = MediaStore.Audio.Albums.DEFAULT_SORT_ORDER

    override val loaderId: Int = 11

}
