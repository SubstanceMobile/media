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
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.CardView
import android.widget.ImageView
import android.widget.TextView
import butterknife.bindView
import com.afollestad.materialdialogs.MaterialDialog
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog
import mobile.substance.sdk.R
import mobile.substance.sdk.music.core.objects.Song
import mobile.substance.sdk.music.loading.Library
import mobile.substance.sdk.music.playback.PlaybackRemote
import mobile.substance.sdk.music.playback.PlaybackState

class MainActivity : NavigationDrawerActivity(), PlaybackRemote.RemoteCallback {

    private val drawerLayout: DrawerLayout by bindView<DrawerLayout>(R.id.activity_main_drawerlayout)
    private val navigationView: NavigationView by bindView<NavigationView>(R.id.activity_main_navigationview)
    private val currentSongImage: ImageView by bindView<ImageView>(R.id.activity_main_current_song_image)
    private val currentSongTitle: TextView by bindView<TextView>(R.id.activity_main_current_song_title)
    private val currentSongCard: CardView by bindView<CardView>(R.id.activity_main_current_song_card)

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        navigationView.setNavigationItemSelectedListener { it ->
            drawerLayout.closeDrawer(GravityCompat.START)
            handleNavigationClick(it)
        }
        currentSongCard.setOnClickListener {
            val dialog = MaterialStyledDialog(this)
                    .setTitle(PlaybackRemote.getCurrentSong()!!.songTitle!!)
                    .setDescription(PlaybackRemote.getCurrentSong()!!.songArtistName + "\n" + PlaybackRemote.getCurrentSong()!!.songAlbumName)
                    .setPositive("Ok", MaterialDialog.SingleButtonCallback { materialDialog, dialogAction ->
                        materialDialog.dismiss()
                    })

            try {
                dialog.setHeaderDrawable(BitmapDrawable(resources, BitmapFactory.decodeFile(Library.findAlbumById(PlaybackRemote.getCurrentSong()!!.songAlbumId!!)!!.albumArtworkPath)))
            } catch(e: NullPointerException) {}
            dialog.show()
        }
    }

    override val drawer: DrawerLayout?
        get() = drawerLayout

    override fun onProgressChanged(progress: Int) {}

    override fun onDurationChanged(duration: Int) {}

    override fun onSongChanged(song: Song) {
        if (currentSongCard.translationY != 0.0f) currentSongCard.animate().translationY(0.0f).setDuration(200).start()
        currentSongTitle.text = song.songTitle
        Library.findAlbumById(song.songAlbumId!!)!!.requestArt(currentSongImage)
    }

    override fun onStateChanged(state: PlaybackState, isRepeating: Boolean) {}

    override fun onQueueChanged(queue: List<Song>) {}

    override val layoutResId: Int = R.layout.activity_main

    override fun onStart() {
        super.onStart()
        PlaybackRemote.init(this)
        PlaybackRemote.registerCallback(this)
    }

    override fun onStop() {
        super.onStop()
        PlaybackRemote.unregisterCallback(this)
        PlaybackRemote.cleanup()
    }

}