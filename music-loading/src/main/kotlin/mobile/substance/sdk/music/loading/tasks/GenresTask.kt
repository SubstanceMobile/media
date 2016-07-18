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

import mobile.substance.sdk.music.core.objects.Genre
import mobile.substance.sdk.music.loading.Library

/**
 * Created by Julian Os on 05.05.2016.
 */
class GenresTask(context: Context, vararg params: Any) : Loader<Genre>(context, params) {

    override fun buildObject(cursor: Cursor): Genre? {
        val name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Genres.NAME))
        val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Genres._ID))

        val g = Genre.Builder()
                .setGenreName(name)
                .setGenreId(id)
                .build()

        Log.d("GenresTask", "Loaded id $id")
        return g
    }

    override val uri: Uri = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI

    override val observer: ContentObserver? = object : ContentObserver(Handler()) {
            override fun onChange(selfChange: Boolean) {
                super.onChange(selfChange)
                update(Library.getGenres())
            }
        }
}
