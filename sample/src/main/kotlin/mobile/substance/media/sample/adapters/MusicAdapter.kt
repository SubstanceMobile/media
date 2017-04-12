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

package mobile.substance.media.sample.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import mobile.substance.media.audio.playback.PlaybackRemote
import mobile.substance.media.core.MediaObject
import mobile.substance.media.core.audio.*
import mobile.substance.media.sample.R
import mobile.substance.media.sample.SampleAudioHolder
import mobile.substance.media.sample.viewholders.MusicViewHolder

class MusicAdapter<T : MediaObject>(private val type: Long) : RecyclerView.Adapter<MusicViewHolder>() {
    var items: List<T>? = null
    var context: Context? = null

    init {
        if (type == AUDIO_TYPE_SONGS) items = SampleAudioHolder.getSongs() as List<T>
        if (type == AUDIO_TYPE_ALBUMS) items = SampleAudioHolder.getAlbums() as List<T>
        if (type == AUDIO_TYPE_ARTISTS) items = SampleAudioHolder.getArtists() as List<T>
        if (type == AUDIO_TYPE_PLAYLISTS) items = SampleAudioHolder.getPlaylists() as List<T>
        if (type == AUDIO_TYPE_GENRES) items = SampleAudioHolder.getGenres() as List<T>
    }

    override fun onBindViewHolder(holder: MusicViewHolder?, position: Int) {
        val item = items!!.get(position)
        when (item) {
            is Song -> bindSong(item, holder!!, position)
            is Album -> bindAlbum(item, holder!!)
            is Artist -> bindArtist(item, holder!!)
            is Genre -> bindGenre(item, holder!!)
            is Playlist -> bindPlaylist(item, holder!!)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MusicViewHolder? {
        context = parent!!.context
        return MusicViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false))
    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

    private fun bindAlbum(album: Album, holder: MusicViewHolder) {
        holder.title?.text = album.title
        holder.subtitle?.text = album.artistName
        album.requestArtworkLoad(holder.image!!)
    }

    private fun bindSong(song: Song, holder: MusicViewHolder, position: Int) {
        holder.title?.text = song.title
        holder.subtitle?.text = song.artistName
        song.requestArtworkLoad(holder.image!!)

        holder.itemView.setOnClickListener { it ->
            PlaybackRemote.play(items as List<Song>, position)
        }
    }

    private fun bindArtist(artist: Artist, holder: MusicViewHolder) {
        holder.title?.text = artist.name
    }

    private fun bindGenre(genre: Genre, holder: MusicViewHolder) {
        holder.title!!.text = genre.name
    }

    private fun bindPlaylist(playlist: Playlist, holder: MusicViewHolder) {
        holder.title!!.text = playlist.title
    }

}