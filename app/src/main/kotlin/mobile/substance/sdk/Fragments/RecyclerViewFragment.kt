package mobile.substance.sdk.Fragments

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import mobile.substance.sdk.Adapters.MusicAdapter
import mobile.substance.sdk.R
import mobile.substance.sdk.music.core.objects.*
import mobile.substance.sdk.music.loading.Library
import mobile.substance.sdk.music.loading.LibraryData
import mobile.substance.sdk.music.loading.LibraryListener

/**
 * Created by Julian on 06/05/16.
 */
class RecyclerViewFragment(type: LibraryData) : BaseFragment(), LibraryListener {

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
    private val type: LibraryData = type

    override fun getLayoutResId(): Int {
        return R.layout.fragment_recyclerview
    }

    override fun init() {
        recyclerview!!.layoutManager = LinearLayoutManager(activity)
        Library.registerListener(this)
        setAdapter()
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

    override fun onDestroyView() {
        super.onDestroyView()
        Library.unRegisterListener(this)
    }


}