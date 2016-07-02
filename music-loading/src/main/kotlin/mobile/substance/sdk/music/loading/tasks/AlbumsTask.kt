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

import mobile.substance.sdk.music.core.objects.Album
import mobile.substance.sdk.music.loading.Library

class AlbumsTask(context: Context, vararg params: Any) : Loader<Album>(context, params) {

    override fun buildObject(cursor: Cursor): Album? {
        val album = Album.Builder()
                .setName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM)))
                .setAlbumId(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Albums._ID)))
                .setArtistName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST)))
                .setNumberOfSongs(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS)))
                .setYear(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Albums.LAST_YEAR)))
                .setArtworkPath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART)))
                .build()

        Log.d("AlbumsTask", "Loaded ID ${album.id} with artwork path ${album.albumArtworkPath}")
        return album
    }

    override val uri: Uri
        get() = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI


    override val sortOrder: String?
        get() = MediaStore.Audio.Albums.DEFAULT_SORT_ORDER

    override val observer: ContentObserver?
        get() = object : ContentObserver(Handler()) {
            override fun onChange(selfChange: Boolean) {
                super.onChange(selfChange)
                update(Library.albums)
            }
        }

}
