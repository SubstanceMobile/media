package mobile.substance.sdk.Adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import mobile.substance.sdk.R
import mobile.substance.sdk.ViewHolders.MusicViewHolder
import mobile.substance.sdk.music.core.objects.*

class MusicAdapter(items: MutableList<MediaObject>) : RecyclerView.Adapter<MusicViewHolder>() {
    var items: MutableList<MediaObject>? = null
    var context: Context? = null

    init {
        this.items = items
    }

    override fun onBindViewHolder(holder: MusicViewHolder?, position: Int) {
        val item = items!!.get(position)
        when (item) {
            is Song -> {
                bindSong(item, holder!!)
            }
            is Album -> {
                bindAlbum(item, holder!!)
            }
            is Artist -> {
                bindArtist(item, holder!!)
            }
            is Genre -> {
                bindGenre(item, holder!!)
            }
            is Playlist -> {
                bindPlaylist(item, holder!!)
            }
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
        holder.title!!.text = album.title
        holder.subtitle!!.text = album.albumArtistName
        album.requestArt(holder.image!!, context!!.resources.getDrawable(R.drawable.ic_library_music_black_24dp))
    }

    private fun bindSong(song: Song, holder: MusicViewHolder) {
        holder.title!!.text = song.title
        holder.subtitle!!.text = song.songArtist
        song.album.requestArt(holder.image!!, context!!.resources.getDrawable(R.drawable.ic_library_music_black_24dp))
    }

    private fun bindArtist(artist: Artist, holder: MusicViewHolder) {
        holder.title!!.text = artist.name
        Glide.with(context).load(R.drawable.ic_library_music_black_24dp).into(holder.image)
    }

    private fun bindGenre(genre: Genre, holder: MusicViewHolder) {
        holder.title!!.text = genre.name
        Glide.with(context).load(R.drawable.ic_library_music_black_24dp).into(holder.image)
    }

    private fun bindPlaylist(playlist: Playlist, holder: MusicViewHolder) {
        holder.title!!.text = playlist.name
        playlist.songs.first().album.requestArt(holder.image, context!!.resources.getDrawable(R.drawable.ic_library_music_black_24dp))
    }

}