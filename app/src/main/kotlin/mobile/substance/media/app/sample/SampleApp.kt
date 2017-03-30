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

package mobile.substance.sdk.app.sample

import android.app.Application
import android.util.Log
import com.google.android.gms.cast.CastMediaControlIntent
import mobile.substance.sdk.BuildConfig
import mobile.substance.sdk.R
import mobile.substance.sdk.music.loading.Library
import mobile.substance.sdk.options.MusicCoreOptions
import mobile.substance.sdk.options.MusicPlaybackOptions
import java.lang.reflect.Field
import kotlin.concurrent.thread

/**
 * Created by Julian Os on 09.05.2016.
 */

class SampleApp : Application() {


    override fun onCreate() {
        super.onCreate()

        Library.enable()

        thread {
            MusicCoreOptions.defaultArt = R.drawable.default_artwork_gem
            MusicPlaybackOptions.statusbarIconResId = R.drawable.ic_audiotrack_white_24dp
            MusicPlaybackOptions.isCastEnabled = true
            MusicPlaybackOptions.isGaplessPlaybackEnabled = true
            MusicPlaybackOptions.isLockscreenArtworkBlurEnabled = true

            var field: Field? = null
            try {
                field = BuildConfig::class.java.getField("CAST_APPLICATION_ID")
            } catch (e: Exception) {
                Log.i("SampleApp", "There is no BuildConfig field 'CAST_APPLICATION_ID', the default receiver id will be used")
            }
            MusicPlaybackOptions.castApplicationId = if (field == null) CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID else field.get(null) as String
        }
    }

}