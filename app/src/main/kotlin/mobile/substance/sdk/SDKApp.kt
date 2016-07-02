package mobile.substance.sdk

import android.app.Application
import mobile.substance.sdk.music.core.MusicCoreOptions;
import mobile.substance.sdk.music.loading.Library
import mobile.substance.sdk.music.loading.LibraryConfig
import mobile.substance.sdk.music.loading.LibraryData
import mobile.substance.sdk.music.playback.MusicPlaybackOptions

/**
 * Created by Julian Os on 09.05.2016.
 */

class SDKApp : Application() {


    override fun onCreate() {
        super.onCreate()

        Library.init(this, LibraryConfig()
                .hookPlayback()
                .hookTags()
                .put(LibraryData.SONGS)
                .put(LibraryData.ALBUMS)
                .put(LibraryData.ARTISTS)
                .put(LibraryData.PLAYLISTS)
                .put(LibraryData.GENRES))
        Library.build()

        Thread() {
            run {
                MusicCoreOptions.defaultArt = R.drawable.default_artwork_gem
                MusicCoreOptions.statusbarIconResId = R.drawable.ic_audiotrack_white_24dp
                MusicPlaybackOptions.isCastEnabled = true
                MusicPlaybackOptions.castApplicationId = BuildConfig.CAST_APPLICATION_ID
            }
        }.start()
    }

}