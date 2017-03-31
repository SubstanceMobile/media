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

package mobile.substance.sdk.app.sample.adapters

import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import mobile.substance.sdk.R
import mobile.substance.sdk.music.core.dataLinkers.MusicData
import mobile.substance.sdk.music.core.dataLinkers.MusicLibraryData
import mobile.substance.sdk.music.core.objects.*
import mobile.substance.sdk.music.loading.Library
import mobile.substance.sdk.music.loading.LibraryListener
import mobile.substance.sdk.music.loading.MusicType
import mobile.substance.sdk.music.playback.PlaybackRemote
import mobile.substance.sdk.app.sample.viewholders.MusicViewHolder

class MusicAdapter<T : MediaObject>(private val type: MusicType) : RecyclerView.Adapter<MusicViewHolder>() {
    var items: List<T>? = null
    var context: Context? = null

    init {
        if (type == MusicType.SONGS) items = MusicData.getSongs() as List<T>
        if (type == MusicType.ALBUMS) items = MusicData.getAlbums() as List<T>
        if (type == MusicType.ARTISTS) items = MusicData.getArtists() as List<T>
        if (type == MusicType.PLAYLISTS) items = MusicData.getPlaylists() as List<T>
        if (type == MusicType.GENRES) items = MusicData.getGenres() as List<T>
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
        holder.title?.text = album.albumName
        holder.subtitle?.text = album.albumArtistName
        album.requestArt(holder.image!!)
    }

    private fun bindSong(song: Song, holder: MusicViewHolder, position: Int) {
        holder.title?.text = song.songTitle
        holder.subtitle?.text = song.songArtistName
        val album = MusicData.findAlbumById(song.songAlbumId ?: 0)
        album?.requestArt(holder.image!!)


        holder.itemView.setOnClickListener { it ->
            PlaybackRemote.play(items as List<Song>, position)
        }
    }

    private fun bindArtist(artist: Artist, holder: MusicViewHolder) {
        holder.title?.text = artist.artistName
        Glide.with(context)
                .load(R.drawable.ic_person_black_24dp)
                .crossFade()
                .centerCrop()
                .into(holder.image)
    }

    private fun bindGenre(genre: Genre, holder: MusicViewHolder) {
        holder.title!!.text = genre.genreName
        val songs = MusicData.findSongsForGenre(genre)
        if (songs.isNotEmpty()) MusicData.findAlbumById(songs.first().songAlbumId!!)!!.requestArt(holder.image!!)
    }

    private fun bindPlaylist(playlist: Playlist, holder: MusicViewHolder) {
        holder.title!!.text = playlist.playlistName
        val songs = MusicData.findSongsForPlaylist(playlist)
        if (songs.isNotEmpty()) Library.findAlbumById(songs.first().songAlbumId!!)!!.requestArt(holder.image!!)
    }

}