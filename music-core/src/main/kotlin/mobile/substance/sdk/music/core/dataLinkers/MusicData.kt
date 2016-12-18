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

package mobile.substance.sdk.music.core.dataLinkers

import android.content.Context
import mobile.substance.sdk.music.core.MusicApiError
import mobile.substance.sdk.music.core.objects.*
import java.util.*

object MusicData : MusicLibraryData {

    ///////////////////////////////////////////////////////////////////////////
    // Hook and actual data storage
    ///////////////////////////////////////////////////////////////////////////

    var dataLinker: MusicLibraryData? = null

    fun hook(data : MusicLibraryData) {
        if (data is MusicData) throw MusicApiError("Never hook MusicData into itself")
        dataLinker = data
    }

    fun getData() =
            if (dataLinker != null) dataLinker
            else throw MusicApiError("Please hook a MusicLibraryData object into MusicData")

    ///////////////////////////////////////////////////////////////////////////
    // Wrapping methods for convenience
    ///////////////////////////////////////////////////////////////////////////

    override fun getSongs(): MutableList<Song> = getData()!!.getSongs()

    override fun getAlbums(): MutableList<Album> = getData()!!.getAlbums()

    override fun getArtists(): MutableList<Artist> = getData()!!.getArtists()

    override fun getPlaylists(): MutableList<Playlist> = getData()!!.getPlaylists()

    override fun getGenres(): MutableList<Genre> = getData()!!.getGenres()

    override fun getContext(): Context = dataLinker!!.getContext()

    inline fun <reified T : MediaObject> search(query: String): ArrayList<T>? {
        val results = ArrayList<T>()
        if (T::class.java == Song::class.java)
            getSongs().forEach {
                if (it.songTitle?.contains(query, true) ?: false)
                    results.add(it as T)
            }
        if (T::class.java == Album::class.java)
            getAlbums().forEach {
                if (it.albumName?.contains(query, true) ?: false)
                    results.add(it as T)
            }
        if (T::class.java == Artist::class.java)
            getArtists().forEach {
                if (it.artistName?.contains(query, true) ?: false)
                    results.add(it as T)
            }
        if (T::class.java == Playlist::class.java)
            getPlaylists().forEach {
                if (it.playlistName?.contains(query, true) ?: false)
                    results.add(it as T)
            }
        if (T::class.java == Genre::class.java)
            getGenres().forEach {
                if (it.genreName?.contains(query, true) ?: false)
                    results.add(it as T)
            }
        return results
    }

}