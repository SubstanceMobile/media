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
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import mobile.substance.sdk.music.core.objects.Artist

class ArtistsLoader(context: Context) : MediaLoader<Artist>(context) {

    override fun buildObject(cursor: Cursor): Artist? {
        val name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST))
        val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Artists._ID))

        val a = Artist.Builder()
                .setName(name)
                .setId(id)
                .build()
        return a
    }

    override val uri: Uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI

    override val loaderId: Int = 12

}