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

package mobile.substance.media.core.audio

interface Audio {

    fun getSongs(): List<Song> = emptyList()

    fun getAlbums(): List<Album> = emptyList()

    fun getArtists(): List<Artist> = emptyList()

    fun getPlaylists(): List<Playlist> = emptyList()

    fun getGenres(): List<Genre> = emptyList()

    fun filterAlbums(query: String): List<Album> = getAlbums().filter { it.title?.contains(query, true) ?: false || it.artistName?.contains(query, true) ?: false }

    fun filterSongs(query: String): List<Song> = getSongs().filter { it.title?.contains(query, true) ?: false || it.artistName?.contains(query, true) ?: false }

    fun filterPlaylists(query: String): List<Playlist> = getPlaylists().filter { it.title?.contains(query, true) ?: false }

    fun filterArtists(query: String): List<Artist> = getArtists().filter { it.name?.contains(query, true) ?: false }

    fun filterGenres(query: String): List<Genre> = getGenres().filter { it.name?.contains(query, true) ?: false }

    fun search(query: String): Array<List<*>> = arrayOf(filterSongs(query),
            filterAlbums(query),
            filterArtists(query),
            filterPlaylists(query),
            filterGenres(query))

}