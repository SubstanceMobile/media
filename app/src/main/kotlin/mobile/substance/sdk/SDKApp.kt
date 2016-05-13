package mobile.substance.sdk

import android.app.Application
import mobile.substance.sdk.music.core.MusicOptions

/**
 * Created by Julian Os on 09.05.2016.
 */

class SDKApp : Application() {


    override fun onCreate() {
        super.onCreate()

        Thread() {
            run {
                MusicOptions.setDefaultArt(R.drawable.default_artwork_gem)
                MusicOptions.setStatusbarIconResId(R.drawable.ic_audiotrack_white_24dp)
            }
        }.start()
    }

}