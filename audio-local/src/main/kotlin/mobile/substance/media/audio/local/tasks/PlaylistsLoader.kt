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
import mobile.substance.media.audio.local.objects.MediaStorePlaylist
import mobile.substance.media.local.core.MediaLoader

class PlaylistsLoader<Playlist : MediaStorePlaylist>(context: Context) : MediaLoader<Playlist>(context) {

    override fun Playlist.applyDefault(cursor: Cursor) {
        title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.NAME))
        id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Playlists._ID))
        dateAdded = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Playlists.DATE_ADDED))
        numberOfSongs = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Playlists._COUNT))
    }

    override val uri: Uri
        get() = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI

    override val sortOrder: String
        get() = MediaStore.Audio.Playlists.DEFAULT_SORT_ORDER

}
