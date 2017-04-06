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

package mobile.substance.media.sample.activities

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.CardView
import android.util.Log
import android.view.Menu
import android.widget.ImageView
import android.widget.TextView
import butterknife.bindView
import com.afollestad.materialdialogs.MaterialDialog
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog
import com.google.android.gms.cast.framework.CastButtonFactory
import mobile.substance.media.audio.local.MediaStoreAudioHolder
import mobile.substance.media.audio.playback.PlaybackRemote
import mobile.substance.media.core.MediaCore
import mobile.substance.media.core.audio.*
import mobile.substance.media.sample.MyMusicServiceSubclass
import mobile.substance.media.sample.R
import mobile.substance.media.utils.AudioCoreUtil
import java.util.*

class MainActivity : NavigationDrawerActivity(), PlaybackRemote.RemoteCallback {
    private val drawerLayout: DrawerLayout by bindView<DrawerLayout>(R.id.activity_main_drawerlayout)
    private val navigationView: NavigationView by bindView<NavigationView>(R.id.activity_main_navigationview)
    private val currentSongImage: ImageView by bindView<ImageView>(R.id.activity_main_current_song_image)
    private val currentSongTitle: TextView by bindView<TextView>(R.id.activity_main_current_song_title)
    private val currentSongCard: CardView by bindView<CardView>(R.id.activity_main_current_song_card)
    private val currentSongProgress: TextView by bindView<TextView>(R.id.activity_main_current_song_progress)

    override fun onError() {

    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        MediaStoreAudioHolder.configure()
                .load(AUDIO_TYPE_SONGS)
                .load(AUDIO_TYPE_ALBUMS)
                .load(AUDIO_TYPE_ARTISTS)
                .load(AUDIO_TYPE_GENRES)
                .load(AUDIO_TYPE_PLAYLISTS)
        if (!MediaStoreAudioHolder.isBuilt()) MediaStoreAudioHolder.build()

        navigationView.setNavigationItemSelectedListener { it ->
            drawerLayout.closeDrawer(GravityCompat.START)
            handleNavigationClick(it)
        }
        currentSongCard.setOnClickListener {
            val dialog = MaterialStyledDialog(this)
                    .setTitle(PlaybackRemote.getCurrentSong()!!.title!!)
                    .setDescription(PlaybackRemote.getCurrentSong()!!.artistName + "\n" + PlaybackRemote.getCurrentSong()!!.albumTitle)
                    .setPositive("Ok", MaterialDialog.SingleButtonCallback { materialDialog, dialogAction ->
                        materialDialog.dismiss()
                    })

            try {
                dialog.setHeaderDrawable(BitmapDrawable(resources, BitmapFactory.decodeFile(PlaybackRemote.getCurrentSong()?.artworkUri?.path)))
            } catch(e: NullPointerException) {}
            dialog.show()
        }
    }

    override fun onResume() {
        super.onResume()
        MediaStoreAudioHolder.registerBuildFinishedListener({
            handleIntent()
        }, true)
    }

    private fun handleIntent() {
        println("handleIntent()")
        if (intent.action.endsWith("SHUFFLE")) {
            val songs = AudioData.getSongs()
            Collections.shuffle(songs)
            PlaybackRemote.play(songs, 0)
        }
    }

    var duration: Long? = null

    override val drawer: DrawerLayout?
        get() = drawerLayout

    override fun onProgressChanged(progress: Int) {
        currentSongProgress.text = "${AudioCoreUtil.stringForTime(progress.toLong())} / ${AudioCoreUtil.stringForTime(duration?.toLong() ?: 0L)}"
    }

    override fun onDurationChanged(duration: Long, durationString: String) {
        this.duration = duration
    }

    override fun onSongChanged(song: Song) {
        Log.d("MainActivity.kt", "onSongChanged(), title: ${song.title}, artistName: ${song.artistName}")
        if (currentSongCard.translationY != 0.0f) currentSongCard.animate().translationY(0.0f).setDuration(200).start()
        currentSongTitle.text = song.title
        song.loadArtwork(currentSongImage)
    }

    override fun onStateChanged(state: Int) {
        Log.d("MainActivity.kt", "onStateChanged(), $state")
    }

    override fun onRepeatModeChanged(mode: Int) {
    }

    override fun onQueueChanged(queue: List<Song>) {
    }

    override val layoutResId: Int = R.layout.activity_main

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_music, menu)
        CastButtonFactory.setUpMediaRouteButton(this, menu, R.id.media_route_menu_item)
        return true
    }

    override fun onStart() {
        super.onStart()
        Log.d("MainActivity.kt", "onStart()")
        MediaCore.dispatchOnStartActivity(this)
        PlaybackRemote.withActivity(MyMusicServiceSubclass::class.java, this)
        PlaybackRemote.registerCallback(this)
        if (PlaybackRemote.getCurrentSong() != null) PlaybackRemote.requestUpdates(this)
    }

    override fun onStop() {
        Log.d("MainActivity.kt", "onStop()")
        MediaCore.dispatchOnStopActivity(this)
        PlaybackRemote.unregisterCallback(this)
        PlaybackRemote.cleanup()
        super.onStop()
    }

}