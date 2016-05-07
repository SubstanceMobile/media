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

package mobile.substance.sdk.Fragments

import android.content.Context
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import android.view.View
import mobile.substance.sdk.Helpers.NavigationHelper
import mobile.substance.sdk.R
import mobile.substance.sdk.music.core.objects.*
import mobile.substance.sdk.music.loading.LibraryData
import mobile.substance.sdk.music.loading.LibraryListener

/**
 * Created by Julian Os on 03.05.2016.
 */
class MusicFragment : NavigationDrawerFragment() {
    private var tabs: TabLayout? = null
    private var pager: ViewPager? = null
    private var toolbar: Toolbar? = null

    override fun init() {
        pager!!.adapter = MusicPagerAdapter(activity, activity.supportFragmentManager)
        tabs!!.setupWithViewPager(pager)
        NavigationHelper.setupNavigation(getDrawerLayout(), toolbar!!)
        super.init()
    }

    override fun initViews(root: View) {
        tabs = root.findViewById(R.id.fragment_music_tabs) as TabLayout
        pager = root.findViewById(R.id.fragment_music_viewpager) as ViewPager
        toolbar = root.findViewById(R.id.fragment_music_toolbar) as Toolbar
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_music
    }

    class MusicPagerAdapter(context: Context, fm: FragmentManager) : FragmentPagerAdapter(fm), LibraryListener {

        override fun onSongLoaded(item: Song?, pos: Int) {

        }

        override fun onSongsCompleted(result: MutableList<Song>?) {

        }

        override fun onAlbumLoaded(item: Album?, pos: Int) {

        }

        override fun onAlbumsCompleted(result: MutableList<Album>?) {

        }

        override fun onArtistLoaded(item: Artist?, pos: Int) {

        }

        override fun onArtistsCompleted(result: MutableList<Artist>?) {

        }

        override fun onPlaylistLoaded(item: Playlist?, pos: Int) {

        }

        override fun onPlaylistsCompleted(result: MutableList<Playlist>?) {

        }

        override fun onGenreLoaded(item: Genre?, pos: Int) {

        }

        override fun onGenresCompleted(result: MutableList<Genre>?) {

        }

        var context: Context? = context
        val titleResIds: Array<Int> = arrayOf(R.string.songs, R.string.albums, R.string.artists, R.string.playlists, R.string.genres)

        override fun getCount(): Int {
            return 5
        }

        override fun getItem(position: Int): Fragment? {

            when (position) {
                0 -> return RecyclerViewFragment(LibraryData.SONGS)
                1 -> return RecyclerViewFragment(LibraryData.ALBUMS)
                2 -> return RecyclerViewFragment(LibraryData.ARTISTS)
                3 -> return RecyclerViewFragment(LibraryData.PLAYLISTS)
                4 -> return RecyclerViewFragment(LibraryData.GENRES)
            }
            return null
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return context!!.getString(titleResIds[position])
        }

    }

}