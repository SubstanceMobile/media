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

package mobile.substance.sdk.Adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import mobile.substance.sdk.R
import mobile.substance.sdk.ViewHolders.MusicViewHolder
import mobile.substance.sdk.music.core.objects.*
import mobile.substance.sdk.music.loading.Library

class MusicAdapter<T : MediaObject>(items: MutableList<T>) : RecyclerView.Adapter<MusicViewHolder>() {
    var items: MutableList<T>? = null
    var context: Context? = null

    init {
        this.items = items
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
        return items!!.size
    }

    private fun bindAlbum(album: Album, holder: MusicViewHolder) {
        holder.title!!.text = album.albumName
        holder.subtitle!!.text = album.albumArtistName
        album.requestArt(holder.image!!, context!!.resources.getDrawable(R.drawable.ic_library_music_black_24dp))
    }

    private fun bindSong(song: Song, holder: MusicViewHolder) {
        holder.title!!.text = song.songTitle
        holder.subtitle!!.text = song.songArtistName
        Library.findAlbumById(song.songAlbumID)!!.requestArt(holder.image!!, context!!.resources.getDrawable(R.drawable.ic_library_music_black_24dp))
    }

    private fun bindArtist(artist: Artist, holder: MusicViewHolder) {
        holder.title!!.text = artist.artistName
        Glide.with(context).load(R.drawable.ic_library_music_black_24dp).into(holder.image)
    }

    private fun bindGenre(genre: Genre, holder: MusicViewHolder) {
        holder.title!!.text = genre.genreName
        Library.findSongsForGenreAsync(context, genre, Library.QueryResult { it ->
            if (it.size > 0) Library.findAlbumById(it.first().songAlbumID)!!.requestArt(holder.image, context!!.resources.getDrawable(R.drawable.ic_library_music_black_24dp))
        })
    }

    private fun bindPlaylist(playlist: Playlist, holder: MusicViewHolder) {
        holder.title!!.text = playlist.playlistName
        Library.findSongsForPlaylistAsync(context, playlist, Library.QueryResult { it ->
            if (it.size > 0) Library.findAlbumById(it.first().songAlbumID)!!.requestArt(holder.image, context!!.resources.getDrawable(R.drawable.ic_library_music_black_24dp))
        })
    }

}