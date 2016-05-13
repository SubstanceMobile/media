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

    override fun onSongLoaded(item: Song?, pos: Int) {
    }

    override fun onSongsCompleted(result: MutableList<Song>?) {
        if (type == LibraryData.SONGS) setAdapter()
    }

    override fun onAlbumLoaded(item: Album?, pos: Int) {
    }

    override fun onAlbumsCompleted(result: MutableList<Album>?) {
        if (type == LibraryData.ALBUMS) setAdapter()
    }

    override fun onArtistLoaded(item: Artist?, pos: Int) {
    }

    override fun onArtistsCompleted(result: MutableList<Artist>?) {
        if (type == LibraryData.ARTISTS) setAdapter()
    }

    override fun onPlaylistLoaded(item: Playlist?, pos: Int) {
    }

    override fun onPlaylistsCompleted(result: MutableList<Playlist>?) {
        if (type == LibraryData.PLAYLISTS) setAdapter()
    }

    override fun onGenreLoaded(item: Genre?, pos: Int) {
    }

    override fun onGenresCompleted(result: MutableList<Genre>?) {
        if (type == LibraryData.GENRES) setAdapter()
    }

    private var recyclerview: RecyclerView? = null
    private var type: LibraryData? = null

    override fun getLayoutResId(): Int {
        return R.layout.fragment_recyclerview
    }

    override fun init() {
        recyclerview!!.layoutManager = LinearLayoutManager(activity)
        setAdapter()
        Library.registerListener(this)
    }

    private fun setAdapter() {
        when (type) {
            LibraryData.SONGS -> recyclerview!!.adapter = MusicAdapter<Song>(Library.getSongs())
            LibraryData.ALBUMS -> recyclerview!!.adapter = MusicAdapter<Album>(Library.getAlbums())
            LibraryData.ARTISTS -> recyclerview!!.adapter = MusicAdapter<Artist>(Library.getArtists())
            LibraryData.PLAYLISTS -> recyclerview!!.adapter = MusicAdapter<Playlist>(Library.getPlaylists())
            LibraryData.GENRES -> recyclerview!!.adapter = MusicAdapter<Genre>(Library.getGenres())
        }
    }

    override fun initViews(root: View) {
        recyclerview = root.findViewById(R.id.fragment_recyclerview) as RecyclerView
    }

    override fun onDetach() {
        super.onDetach()
        Library.unregisterListener(this)
    }


}