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

package mobile.substance.sdk.music.loading.tasks

import android.content.Context
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.provider.MediaStore
import android.util.Log

import mobile.substance.sdk.music.core.objects.Song
import mobile.substance.sdk.music.loading.Library

/**
 * Created by Adrian on 3/25/2016.
 */
class SongsTask(context: Context, vararg params: Any) : Loader<Song>(context, params) {

    override fun buildObject(cursor: Cursor): Song? {
        val title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
        val artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
        val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
        val albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
        val duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
        val trackNumber = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.TRACK))
        val albumName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
        val artistId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID))
        val year = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.YEAR))

        val s = Song.Builder().setId(id).setTitle(title).setArtistName(artist).setArtistId(artistId).setAlbumName(albumName).setAlbumId(albumId).setTrackNumber(trackNumber).setYear(year ?: "0000").setDuration(duration).build()

        Log.i("SongsTask", "Loaded ID " + id)
        return s
    }

    override val uri: Uri
        get() = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

    override val selection: String?
        get() = MediaStore.Audio.Media.IS_MUSIC + "=1"

    override val sortOrder: String?
        get() = MediaStore.Audio.Media.DEFAULT_SORT_ORDER

    override val observer: ContentObserver?
        get() = object : ContentObserver(Handler()) {
            override fun onChange(selfChange: Boolean) {
                super.onChange(selfChange)
                update(Library.songs)
            }
        }
}
