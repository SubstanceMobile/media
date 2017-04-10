/*
 * Copyright 2017 Substance Mobile
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

package mobile.substance.media.sample

import android.app.Application
import android.util.Log
import com.google.android.gms.cast.CastMediaControlIntent
import mobile.substance.media.audio.local.MediaStoreAudioHolder
import mobile.substance.media.core.MediaCore
import mobile.substance.media.options.AudioCoreOptions
import mobile.substance.media.options.AudioLocalOptions
import mobile.substance.media.options.AudioPlaybackOptions
import java.lang.reflect.Field
import kotlin.concurrent.thread

class SampleApp : Application() {

    override fun onCreate() {
        super.onCreate()
        MediaCore.activate(MediaStoreAudioHolder)

        thread {
            AudioCoreOptions.defaultArtResId = R.drawable.default_artwork_gem
            AudioPlaybackOptions.statusbarIconResId = R.drawable.ic_audiotrack_white_24dp
            AudioPlaybackOptions.isCastEnabled = true
            AudioPlaybackOptions.isGaplessPlaybackEnabled = true
            AudioPlaybackOptions.isLockscreenArtworkBlurEnabled = true
            AudioLocalOptions.useEmbeddedArtwork = true

            var field: Field? = null
            try {
                field = BuildConfig::class.java.getField("CAST_APPLICATION_ID")
            } catch (e: Exception) {
                Log.i("SampleApp", "There is no BuildConfig field 'CAST_APPLICATION_ID', the default receiver id will be used")
            }
            AudioPlaybackOptions.castApplicationId = if (field == null) CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID else field.get(null) as String
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        MediaCore.deactivate(MediaStoreAudioHolder)
    }

}