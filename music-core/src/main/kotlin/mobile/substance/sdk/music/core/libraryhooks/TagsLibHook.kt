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

package mobile.substance.sdk.music.core.libraryhooks

import mobile.substance.sdk.music.core.objects.Album
import mobile.substance.sdk.music.core.objects.Song

/**
 * This class is used only as a data source for the tags library
 */
object TagsLibHook {
    var songList: (() -> List<Song>?)? = null
    var albumList: (() -> List<Album>?)? = null

    fun findSongById(id: Long): Song? {
        if (songList?.invoke() == null) return null
        if (songList!!.invoke()!!.isEmpty()) return null
        for (song in songList!!.invoke()!!) if (song.id == id) return song
        return null
    }

    fun findAlbumById(id: Long): Album? {
        if (albumList?.invoke() == null) return null
        if (albumList!!.invoke()!!.isEmpty()) return null
        for (album in albumList!!.invoke()!!) if (album.id == id) return album
        return null
    }
}