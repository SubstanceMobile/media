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

package mobile.substance.sdk.Activities

import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import mobile.substance.sdk.R
import mobile.substance.sdk.music.core.objects.Song
import mobile.substance.sdk.music.loading.Library
import mobile.substance.sdk.music.loading.LibraryConfig
import mobile.substance.sdk.music.loading.LibraryData
import mobile.substance.sdk.music.playback.PlaybackRemote

class MainActivity : NavigationDrawerActivity(), PlaybackRemote.RemoteCallback {

    override fun onProgressChanged(progress: Int) {

    }

    override fun onDurationChanged(duration: Int) {

    }

    override fun onSongChanged(song: Song) {

    }

    override fun onStateChanged(state: Int, isRepeating: Boolean) {

    }

    override fun onQueueChanged(queue: List<Song>) {

    }


    var drawerLayout: DrawerLayout? = null
    var navigationView: NavigationView? = null

    override fun getLayoutResId(): Int {
        return R.layout.activity_main
    }

    override fun init() {
        navigationView!!.setNavigationItemSelectedListener { it ->
            drawerLayout!!.closeDrawer(GravityCompat.START)
            handleNavigationClick(it)
        }
        Library(this, LibraryConfig()
                .put(LibraryData.SONGS)
                .put(LibraryData.ALBUMS)
                .put(LibraryData.ARTISTS)
                .put(LibraryData.PLAYLISTS)
                .put(LibraryData.GENRES))
        Library.build()
        super.init()
    }


    override fun initViews() {
        navigationView = findViewById(R.id.activity_main_navigationview) as NavigationView
        drawerLayout = findViewById(R.id.activity_main_drawerlayout) as DrawerLayout
    }

    override fun getDrawer(): DrawerLayout? {
        return drawerLayout
    }

    override fun onStart() {
        super.onStart()
        PlaybackRemote.registerActivity(this, this)
    }

    override fun onStop() {
        super.onStop()
        PlaybackRemote.unregisterActivity()
    }

}