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

package mobile.substance.sdk.activities

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.CardView
import android.widget.ImageView
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog
import mobile.substance.sdk.R
import mobile.substance.sdk.music.core.objects.Song
import mobile.substance.sdk.music.loading.Library
import mobile.substance.sdk.music.loading.LibraryConfig
import mobile.substance.sdk.music.loading.LibraryData
import mobile.substance.sdk.music.playback.MusicQueue
import mobile.substance.sdk.music.playback.PlaybackRemote
import mobile.substance.sdk.music.playback.PlaybackState

class MainActivity : NavigationDrawerActivity(), PlaybackRemote.RemoteCallback, PlaybackRemote.NotificationCallback {

    override fun onProgressChanged(progress: Int) {

    }

    override fun onDurationChanged(duration: Int) {

    }

    override fun onSongChanged(song: Song) {
        if (currentSongCard!!.translationY != 0.0f) currentSongCard!!.animate().translationY(0.0f).setDuration(200).start()
        currentSongTitle!!.text = song.songTitle
        Library.findAlbumById(song.songAlbumId!!)!!.requestArt(currentSongImage!!)
    }

    override fun onStateChanged(state: PlaybackState, isRepeating: Boolean) {

    }

    override fun onQueueChanged(queue: List<Song>) {

    }

    var drawerLayout: DrawerLayout? = null
    var navigationView: NavigationView? = null
    var currentSongImage: ImageView? = null
    var currentSongTitle: TextView? = null
    var currentSongCard: CardView? = null

    override fun getLayoutResId(): Int {
        return R.layout.activity_main
    }

    override fun init() {
        PlaybackRemote.setNotificationCallback(this)
        navigationView!!.setNavigationItemSelectedListener { it ->
            drawerLayout!!.closeDrawer(GravityCompat.START)
            handleNavigationClick(it)
        }
        Library.init(this, LibraryConfig()
                .put(LibraryData.SONGS)
                .put(LibraryData.ALBUMS)
                .put(LibraryData.ARTISTS)
                .put(LibraryData.PLAYLISTS)
                .put(LibraryData.GENRES))
        Library.build()
        currentSongCard!!.setOnClickListener {
            val dialog = MaterialStyledDialog(this)
                    .setTitle(MusicQueue.getCurrentSong()!!.songTitle!!)
                    .setDescription(MusicQueue.getCurrentSong()!!.songArtistName + "\n" + MusicQueue.getCurrentSong()!!.songAlbumName)
                    .setPositive("Ok", MaterialDialog.SingleButtonCallback { materialDialog, dialogAction ->
                        materialDialog.dismiss()
                    })

            try {
                dialog.setHeaderDrawable(BitmapDrawable(resources, BitmapFactory.decodeFile(Library.findAlbumById(MusicQueue.getCurrentSong()!!.songAlbumId!!)!!.albumArtworkPath)))
            } catch(e: NullPointerException) {}
            dialog.show()
        }
        super.init()
    }

    override fun initViews() {
        navigationView = findViewById(R.id.activity_main_navigationview) as NavigationView
        drawerLayout = findViewById(R.id.activity_main_drawerlayout) as DrawerLayout
        currentSongImage = findViewById(R.id.activity_main_current_song_image) as ImageView
        currentSongTitle = findViewById(R.id.activity_main_current_song_title) as TextView
        currentSongCard = findViewById(R.id.activity_main_current_song_card) as CardView
    }

    override fun getDrawer(): DrawerLayout? {
        return drawerLayout
    }

    override fun onStart() {
        super.onStart()
        PlaybackRemote.setup(this, this)
    }

    override fun onStop() {
        super.onStop()
        PlaybackRemote.cleanup()
    }

}