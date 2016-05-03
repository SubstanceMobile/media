package mobile.substance.sdk.Fragments

import android.provider.MediaStore
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import mobile.substance.sdk.Adapters.MusicAdapter
import mobile.substance.sdk.R
import mobile.substance.sdk.music.core.objects.Album
import mobile.substance.sdk.music.core.objects.MediaObject
import mobile.substance.sdk.music.core.objects.Song
import java.util.*

/**
 * Created by Julian Os on 03.05.2016.
 */
class MusicFragment : BaseFragment() {
    var list: RecyclerView? = null
    var swiprefresh: SwipeRefreshLayout? = null

    override fun init() {
        swiprefresh!!.isRefreshing = true
        initList()
        super.init()
    }

    override fun initViews(root: View) {
        list = root.findViewById(R.id.fragment_music_recyclerview) as RecyclerView
        swiprefresh = root.findViewById(R.id.fragment_music_swiperefresh) as SwipeRefreshLayout
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_music
    }

    private fun initList() {
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        val songsCursor = activity.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, selection, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER)

        val songsList: MutableList<Song> = ArrayList()

        songsCursor.moveToFirst()
        do {
            val song = Song()
            song.title = songsCursor.getString(songsCursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
            song.songArtist = songsCursor.getString(songsCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
            song.albumID = songsCursor.getLong(songsCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
            songsList.add(song)
        } while (songsCursor.moveToNext())

        songsCursor.close()

        val albumsCursor = activity.contentResolver.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Albums.DEFAULT_SORT_ORDER)

        val albumsList: MutableList<Album> = ArrayList()
        albumsCursor.moveToFirst()
        do {
            val album = Album()
            album.title = albumsCursor.getString(albumsCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM))
            album.id = albumsCursor.getLong(albumsCursor.getColumnIndex(MediaStore.Audio.Albums._ID))
            album.albumArtPath = albumsCursor.getString(albumsCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART))
            albumsList.add(album)
        } while (albumsCursor.moveToNext())
        albumsCursor.close()

        for (album: Album in albumsList) {
            for (song: Song in songsList) {
                if (song.albumID == album.id) {
                    song.album = album
                    // Album songs have to be fixed / implemented correctly
                }
            }
        }

        val objectsList = ArrayList<MediaObject>()
        for (song: Song in songsList) {
            objectsList.add(song)
        }

        list!!.adapter = MusicAdapter(objectsList)
        list!!.layoutManager = LinearLayoutManager(activity)

        swiprefresh!!.isRefreshing = false
    }

}