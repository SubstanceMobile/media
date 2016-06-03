package mobile.substance.sdk

import android.app.Application
import mobile.substance.sdk.music.core.MusicCoreOptions;

/**
 * Created by Julian Os on 09.05.2016.
 */

class SDKApp : Application() {


    override fun onCreate() {
        super.onCreate()

        Thread() {
            run {
                MusicCoreOptions.defaultArt = R.drawable.default_artwork_gem
                MusicCoreOptions.statusbarIconResId = R.drawable.ic_audiotrack_white_24dp
            }
        }.start()
    }

}