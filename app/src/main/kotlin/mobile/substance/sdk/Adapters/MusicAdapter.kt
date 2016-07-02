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
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import mobile.substance.sdk.R
import mobile.substance.sdk.music.core.objects.*
import mobile.substance.sdk.music.loading.Library
import mobile.substance.sdk.music.loading.LibraryData
import mobile.substance.sdk.music.loading.LibraryListener
import mobile.substance.sdk.music.playback.PlaybackRemote
import mobile.substance.sdk.viewholders.MusicViewHolder

class MusicAdapter<T : MediaObject>(private val type: LibraryData) : RecyclerView.Adapter<MusicViewHolder>(), LibraryListener {

    var items: List<T>? = null
    var context: Context? = null

    init {
        Library.registerListener(this)

        if (type == LibraryData.SONGS) items = Library.songs as List<T>
        if (type == LibraryData.ALBUMS) items = Library.albums as List<T>
        if (type == LibraryData.ARTISTS) items = Library.artists as List<T>
        if (type == LibraryData.PLAYLISTS) items = Library.playlists as List<T>
        if (type == LibraryData.GENRES) items = Library.genres as List<T>
    }

    override fun onSongLoaded(item: Song, pos: Int) {}

    override fun onSongsCompleted(result: List<Song>) {
        if (type == LibraryData.SONGS) {
            items = result as List<T>
            notifyDataSetChanged()
        }
    }

    override fun onAlbumLoaded(item: Album, pos: Int) {}

    override fun onAlbumsCompleted(result: List<Album>) {
        if (type == LibraryData.ALBUMS) {
            items = result as List<T>
            notifyDataSetChanged()
        }
    }

    override fun onArtistLoaded(item: Artist, pos: Int) {}

    override fun onArtistsCompleted(result: List<Artist>) {
        if (type == LibraryData.ARTISTS) {
            items = result as List<T>
            notifyDataSetChanged()
        }
    }

    override fun onPlaylistLoaded(item: Playlist, pos: Int) {}

    override fun onPlaylistsCompleted(result: List<Playlist>) {
        if (type == LibraryData.PLAYLISTS) {
            items = result as List<T>
            notifyDataSetChanged()
        }
    }

    override fun onGenreLoaded(item: Genre, pos: Int) {}

    override fun onGenresCompleted(result: List<Genre>) {
        if (type == LibraryData.GENRES) {
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
        holder.title!!.text = album.albumName
        holder.subtitle!!.text = album.albumArtistName
        album.requestArt(holder.image!!)
    }

    private fun bindSong(song: Song, holder: MusicViewHolder) {
        holder.title!!.text = song.songTitle
        holder.subtitle!!.text = song.songArtistName
        Library.findAlbumById(song.songAlbumId!!)!!.requestArt(holder.image!!)
        holder.itemView.setOnClickListener { it ->
            PlaybackRemote.play(song)
        }
    }

    private fun bindArtist(artist: Artist, holder: MusicViewHolder) {
        holder.title!!.text = artist.artistName
        Glide.with(context)
                .load(R.drawable.ic_person_black_24dp)
                .crossFade()
                .centerCrop()
                .into(holder.image)
    }

    private fun bindGenre(genre: Genre, holder: MusicViewHolder) {
        holder.title!!.text = genre.genreName
        Library.findSongsForGenreAsync(context!!, genre, object : Library.QueryResult<List<Song>> {
            override fun onQueryResult(result: List<Song>) {
                if (result.size > 0) Library.findAlbumById(result.first().songAlbumId!!)!!.requestArt(holder.image!!)
            }
        })

    }

    private fun bindPlaylist(playlist: Playlist, holder: MusicViewHolder) {
        holder.title!!.text = playlist.playlistName
        Library.findSongsForPlaylistAsync(context!!, playlist, object : Library.QueryResult<List<Song>> {
            override fun onQueryResult(result: List<Song>) {
                if (result.size > 0) Library.findAlbumById(result.first().songAlbumId!!)!!.requestArt(holder.image!!)
            }
        })
    }

}