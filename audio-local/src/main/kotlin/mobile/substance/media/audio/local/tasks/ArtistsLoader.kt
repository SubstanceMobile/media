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
import mobile.substance.media.audio.local.objects.MediaStoreArtist
import mobile.substance.media.local.core.MediaLoader

class ArtistsLoader<Artist : MediaStoreArtist>(context: Context) : MediaLoader<Artist>(context) {

    override fun Artist.applyDefault(cursor: Cursor) {
        name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST))
        id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Artists._ID))
        numberOfSongs = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS))
        numberOfAlbums = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS))
    }

    override val uri: Uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI

}
