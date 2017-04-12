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
import mobile.substance.media.audio.local.objects.MediaStoreSong
import mobile.substance.media.local.core.MediaLoader

class SongsLoader<Song : MediaStoreSong>(context: Context) : MediaLoader<Song>(context) {

    override fun Song.applyDefault(cursor: Cursor) {
        title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
        artistName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
        id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
        albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
        duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
        trackNumber = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.TRACK))
        albumTitle = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
        artistId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID))
        year = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.YEAR))
        dateAdded = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED))
    }

    override val uri: Uri
        get() = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

    override val selection: String?
        get() = "${MediaStore.Audio.Media.IS_MUSIC} = ?"

    override val selectionArgs: Array<String>?
        get() = arrayOf(1.toString())

    override val sortOrder: String
        get() = MediaStore.Audio.Media.DEFAULT_SORT_ORDER

}
