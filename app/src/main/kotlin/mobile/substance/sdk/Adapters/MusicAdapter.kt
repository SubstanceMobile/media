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

package mobile.substance.sdk.adapters

import android.content.Context
import android.graphics.BitmapFactory
import android.support.v7.widget.RecyclerView
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
import mobile.substance.sdk.viewholders.MusicViewHolder

class MusicAdapter<T : MediaObject>(private val type: MusicType) : RecyclerView.Adapter<MusicViewHolder>(), LibraryListener {

    var items: List<T>? = null
    var context: Context? = null

    init {
        Library.registerListener(this)

        if (type == MusicType.SONGS) items = MusicData.getSongs() as List<T>
        if (type == MusicType.ALBUMS) items = MusicData.getAlbums() as List<T>
        if (type == MusicType.ARTISTS) items = MusicData.getArtists() as List<T>
        if (type == MusicType.PLAYLISTS) items = MusicData.getPlaylists() as List<T>
        if (type == MusicType.GENRES) items = MusicData.getGenres() as List<T>
    }

    override fun onSongLoaded(item: Song, pos: Int) {}

    override fun onSongsCompleted(result: List<Song>) {
        if (type == MusicType.SONGS) {
            items = result as List<T>
            notifyDataSetChanged()
        }
    }

    override fun onAlbumLoaded(item: Album, pos: Int) {}

    override fun onAlbumsCompleted(result: List<Album>) {
        if (type == MusicType.ALBUMS) {
            items = result as List<T>
            notifyDataSetChanged()
        }
    }

    override fun onArtistLoaded(item: Artist, pos: Int) {}

    override fun onArtistsCompleted(result: List<Artist>) {
        if (type == MusicType.ARTISTS) {
            items = result as List<T>
            notifyDataSetChanged()
        }
    }

    override fun onPlaylistLoaded(item: Playlist, pos: Int) {}

    override fun onPlaylistsCompleted(result: List<Playlist>) {
        if (type == MusicType.PLAYLISTS) {
            items = result as List<T>
            notifyDataSetChanged()
        }
    }

    override fun onGenreLoaded(item: Genre, pos: Int) {}

    override fun onGenresCompleted(result: List<Genre>) {
        if (type == MusicType.GENRES) {
            items = result as List<T>
            notifyDataSetChanged()
        }
    }

    override fun onBindViewHolder(holder: MusicViewHolder?, position: Int) {
        val item = items!!.get(position)
        when (item) {
            is Song -> bindSong(item, holder!!)
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

    private fun bindSong(song: Song, holder: MusicViewHolder) {
        holder.title?.text = song.songTitle
        holder.subtitle?.text = song.songArtistName
        holder.image!!.setImageBitmap(BitmapFactory.decodeFile(MusicData.findAlbumById(song.songAlbumId ?: 0)!!.albumArtworkPath))
        // MusicData.findAlbumById(song.songAlbumId!!)?.requestArt(holder.image!!)

        holder.itemView.setOnClickListener { it ->
            PlaybackRemote.play(song)
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
        MusicData.findSongsForGenreAsync(genre, object : MusicLibraryData.QueryResult<List<Song>> {
            override fun onQueryResult(result: List<Song>) {
                if (result.size > 0) MusicData.findAlbumById(result.first().songAlbumId!!)!!.requestArt(holder.image!!)
            }
        })

    }

    private fun bindPlaylist(playlist: Playlist, holder: MusicViewHolder) {
        holder.title!!.text = playlist.playlistName
        MusicData.findSongsForPlaylistAsync(playlist, object : MusicLibraryData.QueryResult<List<Song>> {
            override fun onQueryResult(result: List<Song>) {
                if (result.size > 0) Library.findAlbumById(result.first().songAlbumId!!)!!.requestArt(holder.image!!)
            }
        })
    }

}