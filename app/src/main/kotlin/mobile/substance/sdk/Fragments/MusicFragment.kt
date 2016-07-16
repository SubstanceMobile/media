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

package mobile.substance.sdk.fragments

import android.content.Context
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import butterknife.bindView
import mobile.substance.sdk.R
import mobile.substance.sdk.helpers.NavigationHelper
import mobile.substance.sdk.music.loading.MusicType

/**
 * Created by Julian Os on 03.05.2016.
 */
class MusicFragment : NavigationDrawerFragment() {
    private val tabs: TabLayout by bindView<TabLayout>(R.id.fragment_music_tabs)
    private val pager: ViewPager by bindView<ViewPager>(R.id.fragment_music_viewpager)
    private val toolbar: Toolbar by bindView<Toolbar>(R.id.fragment_music_toolbar)

    override fun init(savedInstanceState: Bundle?) {
        pager.adapter = MusicPagerAdapter(activity, activity.supportFragmentManager)
        tabs.setupWithViewPager(pager)
        NavigationHelper.setupNavigation(drawerLayout!!, toolbar)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
    }

    override val layoutResId: Int = R.layout.fragment_music

    class MusicPagerAdapter(context: Context, fm: FragmentManager) : FragmentPagerAdapter(fm) {

        var context: Context? = context
        val titleResIds = arrayOf(R.string.songs, R.string.albums, R.string.artists, R.string.playlists, R.string.genres)

        override fun getCount(): Int {
            return 5
        }

        override fun getItem(position: Int): Fragment? {
            when (position) {
                0 -> return RecyclerViewFragment().setType(MusicType.SONGS)
                1 -> return RecyclerViewFragment().setType(MusicType.ALBUMS)
                2 -> return RecyclerViewFragment().setType(MusicType.ARTISTS)
                3 -> return RecyclerViewFragment().setType(MusicType.PLAYLISTS)
                4 -> return RecyclerViewFragment().setType(MusicType.GENRES)
            }
            return null
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return context!!.getString(titleResIds[position])
        }

    }

}