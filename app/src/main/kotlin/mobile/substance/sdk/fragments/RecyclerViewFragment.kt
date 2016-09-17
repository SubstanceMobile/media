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

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import butterknife.bindView
import mobile.substance.sdk.R
import mobile.substance.sdk.adapters.MusicAdapter
import mobile.substance.sdk.music.core.objects.*
import mobile.substance.sdk.music.loading.Library
import mobile.substance.sdk.music.loading.LibraryListener
import mobile.substance.sdk.music.loading.MusicType

/**
 * Created by Julian on 06/05/16.
 */
class RecyclerViewFragment : BaseFragment(), LibraryListener {

    fun setType(data: MusicType): RecyclerViewFragment {
        this.type = data
        return this
    }

    override fun onSongLoaded(item: Song, pos: Int) {
    }

    override fun onSongsCompleted(result: List<Song>) {
        if (type == MusicType.SONGS) setAdapter()
    }

    override fun onAlbumLoaded(item: Album, pos: Int) {
    }

    override fun onAlbumsCompleted(result: List<Album>) {
        if (type == MusicType.ALBUMS) setAdapter()
    }

    override fun onArtistLoaded(item: Artist, pos: Int) {
    }

    override fun onArtistsCompleted(result: List<Artist>) {
        if (type == MusicType.ARTISTS) setAdapter()
    }

    override fun onPlaylistLoaded(item: Playlist, pos: Int) {
    }

    override fun onPlaylistsCompleted(result: List<Playlist>) {
        if (type == MusicType.PLAYLISTS) setAdapter()
    }

    override fun onGenreLoaded(item: Genre, pos: Int) {
    }

    override fun onGenresCompleted(result: List<Genre>) {
        if (type == MusicType.GENRES) setAdapter()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putSerializable("type", type)
    }

    private val recyclerview: RecyclerView by bindView<RecyclerView>(R.id.fragment_recyclerview_rv)
    private var type: MusicType? = null

    override val layoutResId = R.layout.fragment_recyclerview

    override fun init(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) type = savedInstanceState.getSerializable("type") as MusicType?

        recyclerview.layoutManager = LinearLayoutManager(activity)
        setAdapter()
        Library.registerListener(this)
    }

    private fun setAdapter() {
        when (type) {
            MusicType.SONGS -> recyclerview.adapter = MusicAdapter<Song>(type!!)
            MusicType.ALBUMS -> recyclerview.adapter = MusicAdapter<Album>(type!!)
            MusicType.ARTISTS -> recyclerview.adapter = MusicAdapter<Artist>(type!!)
            MusicType.PLAYLISTS -> recyclerview.adapter = MusicAdapter<Playlist>(type!!)
            MusicType.GENRES -> recyclerview.adapter = MusicAdapter<Genre>(type!!)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Library.unregisterListener(this)
    }
}