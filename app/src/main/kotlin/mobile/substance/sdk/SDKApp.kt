package mobile.substance.sdk

import android.app.Application
import android.util.Log
import com.google.android.gms.cast.CastMediaControlIntent
import mobile.substance.sdk.music.core.MusicCoreOptions
import mobile.substance.sdk.music.loading.Library
import mobile.substance.sdk.music.loading.LibraryConfig
import mobile.substance.sdk.music.loading.MusicType
import mobile.substance.sdk.music.playback.MusicPlaybackOptions
import java.lang.reflect.Field

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

                var field: Field? = null
                try {
                    field = BuildConfig::class.java.getField("CAST_APPLICATION_ID")
                } catch (e: Exception) {
                    Log.i("SDKApp", "There is no BuildConfig field 'CAST_APPLICATION_ID', the default receiver id will be used")
                }
                MusicPlaybackOptions.castApplicationId = if (field == null) CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID else field.get(null) as String
            }
        }.start()
    }

}