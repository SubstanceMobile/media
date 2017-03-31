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
import mobile.substance.media.audio.loading.objects.MediaStoreSong
import mobile.substance.media.loading.core.MediaLoader

class SongsLoader(context: Context) : MediaLoader<MediaStoreSong>(context) {

    override fun buildObject(cursor: Cursor): MediaStoreSong? {
        val title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
        val artistName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
        val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
        val albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
        val duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
        val trackNumber = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.TRACK))
        val albumTitle = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
        val artistId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID))
        val year = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.YEAR))
        val dateAdded = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED))

        return MediaStoreSong().apply {
            this.title = title
            this.artistName = artistName
            this.id = id
            this.albumId = albumId
            this.duration = duration
            this.trackNumber = trackNumber
            this.albumTitle  = albumTitle
            this.artistId = artistId
            this.year = year
            this.dateAdded = dateAdded
        }
    }

    override val uri: Uri
        get() = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

    override val selection: String?
        get() = "${MediaStore.Audio.Media.IS_MUSIC} = ?"

    override val selectionArgs: Array<String>?
        get() = arrayOf(1.toString())

    override val sortOrder: String
        get() = MediaStore.Audio.Media.DEFAULT_SORT_ORDER

    override val loaderId: Int = 10

}
