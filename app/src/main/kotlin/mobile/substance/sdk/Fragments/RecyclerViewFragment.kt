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

package mobile.substance.sdk.fragments

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import butterknife.bindView
import mobile.substance.sdk.R
import mobile.substance.sdk.adapters.MusicAdapter
import mobile.substance.sdk.music.core.objects.*
import mobile.substance.sdk.music.loading.Library
import mobile.substance.sdk.music.loading.LibraryData
import mobile.substance.sdk.music.loading.LibraryListener

/**
 * Created by Julian on 06/05/16.
 */
class RecyclerViewFragment : BaseFragment(), LibraryListener {

    fun setType(data: LibraryData): RecyclerViewFragment {
        this.type = data
        return this
    }

    override fun onSongLoaded(item: Song, pos: Int) {
    }

    override fun onSongsCompleted(result: List<Song>) {
        if (type == LibraryData.SONGS) setAdapter()
    }

    override fun onAlbumLoaded(item: Album, pos: Int) {
    }

    override fun onAlbumsCompleted(result: List<Album>) {
        if (type == LibraryData.ALBUMS) setAdapter()
    }

    override fun onArtistLoaded(item: Artist, pos: Int) {
    }

    override fun onArtistsCompleted(result: List<Artist>) {
        if (type == LibraryData.ARTISTS) setAdapter()
    }

    override fun onPlaylistLoaded(item: Playlist, pos: Int) {
    }

    override fun onPlaylistsCompleted(result: List<Playlist>) {
        if (type == LibraryData.PLAYLISTS) setAdapter()
    }

    override fun onGenreLoaded(item: Genre, pos: Int) {
    }

    override fun onGenresCompleted(result: List<Genre>) {
        if (type == LibraryData.GENRES) setAdapter()
    }

    private val recyclerview: RecyclerView by bindView<RecyclerView>(R.id.fragment_recyclerview)
    private var type: LibraryData? = null

    override val layoutResId: Int
        get() = R.id.fragment_recyclerview

    override fun init() {
        recyclerview.layoutManager = LinearLayoutManager(activity)
        setAdapter()
        Library.registerListener(this)
    }

    private fun setAdapter() {
        when (type) {
            LibraryData.SONGS -> recyclerview!!.adapter = MusicAdapter<Song>(Library.songs)
            LibraryData.ALBUMS -> recyclerview!!.adapter = MusicAdapter<Album>(Library.albums)
            LibraryData.ARTISTS -> recyclerview!!.adapter = MusicAdapter<Artist>(Library.artists)
            LibraryData.PLAYLISTS -> recyclerview!!.adapter = MusicAdapter<Playlist>(Library.playlists)
            LibraryData.GENRES -> recyclerview!!.adapter = MusicAdapter<Genre>(Library.genres)
        }
    }

    override fun onDetach() {
        super.onDetach()
        Library.unregisterListener(this)
    }
}