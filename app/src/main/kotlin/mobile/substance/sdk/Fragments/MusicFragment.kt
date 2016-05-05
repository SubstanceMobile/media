package mobile.substance.sdk.Fragments

import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import mobile.substance.sdk.Adapters.MusicAdapter
import mobile.substance.sdk.R
import mobile.substance.sdk.music.core.objects.MediaObject
import mobile.substance.sdk.music.core.objects.Song
import mobile.substance.sdk.music.loading.Library
import mobile.substance.sdk.music.loading.tasks.Loader
import java.util.*

/**
 * Created by Julian Os on 03.05.2016.
 */
class MusicFragment : BaseFragment(), Loader.TaskListener<Song> {
    override fun onOneLoaded(item: Song?, pos: Int) {

    }

    override fun onCompleted(result: MutableList<Song>?) {
        list!!.layoutManager = LinearLayoutManager(activity)
        val mediaObjects = ArrayList<MediaObject>()
        for (song: Song in result!!) {
            mediaObjects.add(song)
        }
        list!!.adapter = MusicAdapter(mediaObjects)
    }

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
        Library.registerSongListener(this)
        Library.build()
    }


}