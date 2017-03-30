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

package mobile.substance.media.music.loading

import mobile.substance.media.core.music.objects.*

/**
 * Created by Julian Os on 05.05.2016.
 */
interface LibraryListener {

    fun onSongLoaded(item: Song, pos: Int)

    fun onSongsCompleted(result: List<Song>)

    fun onAlbumLoaded(item: Album, pos: Int)

    fun onAlbumsCompleted(result: List<Album>)

    fun onArtistLoaded(item: Artist, pos: Int)

    fun onArtistsCompleted(result: List<Artist>)

    fun onPlaylistLoaded(item: Playlist, pos: Int)

    fun onPlaylistsCompleted(result: List<Playlist>)

    fun onGenreLoaded(item: Genre, pos: Int)

    fun onGenresCompleted(result: List<Genre>)

}
