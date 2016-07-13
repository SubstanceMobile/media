package mobile.substance.sdk

import android.app.Application
import android.content.Context
import mobile.substance.sdk.music.core.MusicCoreOptions;
import mobile.substance.sdk.music.loading.Library
import mobile.substance.sdk.music.loading.LibraryConfig
import mobile.substance.sdk.music.loading.MusicType
import mobile.substance.sdk.music.playback.MusicPlaybackOptions

/**
 * Created by Julian Os on 09.05.2016.
 */

class SDKApp : Application() {


    override fun onCreate() {
        super.onCreate()

        Library.init(this, LibraryConfig()
                .hookIntoActivityLifecycle(this)
                .load(MusicType.SONGS, MusicType.ALBUMS, MusicType.ARTISTS, MusicType.GENRES, MusicType.PLAYLISTS))
                .build()

        Thread() {
            run {
                MusicCoreOptions.defaultArt = R.drawable.default_artwork_gem
                MusicPlaybackOptions.statusbarIconResId = R.drawable.ic_audiotrack_white_24dp
                MusicPlaybackOptions.isCastEnabled = true
                MusicPlaybackOptions.castApplicationId = BuildConfig.CAST_APPLICATION_ID
            }
        }.start()
    }

}