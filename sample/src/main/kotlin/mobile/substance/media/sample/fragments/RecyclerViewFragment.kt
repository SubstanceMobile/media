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

package mobile.substance.media.sample.fragments

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import butterknife.bindView
import mobile.substance.media.audio.local.LocalAudioHolder
import mobile.substance.media.audio.local.MediaStoreAudioHolderListener
import mobile.substance.media.audio.local.objects.*
import mobile.substance.media.core.audio.*
import mobile.substance.media.sample.R
import mobile.substance.media.sample.SampleAudioHolder
import mobile.substance.media.sample.adapters.MusicAdapter

class RecyclerViewFragment : BaseFragment(), MediaStoreAudioHolderListener {

    override fun onSongsCompleted(result: List<MediaStoreSong>) {
        if (type == AUDIO_TYPE_SONGS) setAdapter()
    }

    override fun onAlbumsCompleted(result: List<MediaStoreAlbum>) {
        if (type == AUDIO_TYPE_ALBUMS) setAdapter()
    }

    override fun onArtistsCompleted(result: List<MediaStoreArtist>) {
        if (type == AUDIO_TYPE_ARTISTS) setAdapter()
    }

    override fun onPlaylistsCompleted(result: List<MediaStorePlaylist>) {
        if (type == AUDIO_TYPE_PLAYLISTS) setAdapter()
    }

    override fun onGenresCompleted(result: List<MediaStoreGenre>) {
        if (type == AUDIO_TYPE_GENRES) setAdapter()
    }

    fun setType(@AudioType type: Long): RecyclerViewFragment {
        this.type = type
        return this
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putSerializable("type", type)
    }

    private val recyclerview: RecyclerView by bindView<RecyclerView>(R.id.fragment_recyclerview_rv)
    @AudioType
    private var type: Long? = null

    override val layoutResId = R.layout.fragment_recyclerview

    override fun init(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) type = savedInstanceState.getLong("type")

        recyclerview.layoutManager = LinearLayoutManager(activity)
        setAdapter()
        SampleAudioHolder.registerListener(this)
    }

    private fun setAdapter() {
        when (type) {
            AUDIO_TYPE_SONGS -> recyclerview.adapter = MusicAdapter<Song>(type!!)
            AUDIO_TYPE_ALBUMS -> recyclerview.adapter = MusicAdapter<Album>(type!!)
            AUDIO_TYPE_ARTISTS -> recyclerview.adapter = MusicAdapter<Artist>(type!!)
            AUDIO_TYPE_PLAYLISTS -> recyclerview.adapter = MusicAdapter<Playlist>(type!!)
            AUDIO_TYPE_GENRES -> recyclerview.adapter = MusicAdapter<Genre>(type!!)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        SampleAudioHolder.unregisterListener(this)
    }
}