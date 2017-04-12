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
import android.graphics.Bitmap
import android.net.Uri
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.gms.cast.CastMediaControlIntent
import mobile.substance.media.audio.local.LocalAudioHolder
import mobile.substance.media.core.Media
import mobile.substance.media.options.AudioCoreOptions
import mobile.substance.media.options.AudioLocalOptions
import mobile.substance.media.options.AudioPlaybackOptions
import mobile.substance.media.options.CoreOptions
import java.lang.reflect.Field
import com.bumptech.glide.request.target.Target
import mobile.substance.media.extensions.asBitmap
import java.security.AccessController.getContext
import kotlin.concurrent.thread

class SampleApp : Application(), CoreOptions.ImageLoadAdapter {
    override fun onRequestLoad(imageSrc: Uri, defaultRes: Int, target: ImageView) {
        Glide.with(this)
                .load(imageSrc)
                .placeholder(defaultRes)
                .error(defaultRes)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .crossFade()
                .centerCrop()
                .into(target)
    }

    override fun onRequestLoad(image: ByteArray?, defaultRes: Int, target: ImageView) {
        Glide.with(this)
                .load(image)
                .placeholder(defaultRes)
                .error(defaultRes)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .crossFade()
                .centerCrop()
                .into(target)
    }

    override fun onRequestBitmap(imageSrc: Uri, defaultRes: Int): Bitmap {
        try {
            return Glide.with(this)
                    .load(imageSrc)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .centerCrop()
                    .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .get()
        } catch (e: Exception) {
            return ContextCompat.getDrawable(this, defaultRes).asBitmap()
        }
    }

    override fun onRequestBitmap(image: ByteArray?, defaultRes: Int): Bitmap {
        try {
            return Glide.with(this)
                    .load(image)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .centerCrop()
                    .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .get()
        } catch (e: Exception) {
            return ContextCompat.getDrawable(this, defaultRes).asBitmap()
        }
    }

    override fun onCreate() {
        super.onCreate()
        SampleAudioHolder.isActive = true

        thread {
            AudioCoreOptions.defaultSongArtworkRes = R.drawable.default_artwork_gem
            AudioPlaybackOptions.statusbarIconResId = R.drawable.ic_audiotrack_white_24dp
            AudioPlaybackOptions.isCastEnabled = true
            AudioPlaybackOptions.isGaplessPlaybackEnabled = true
            AudioPlaybackOptions.isLockscreenArtworkBlurEnabled = true
            AudioLocalOptions.useEmbeddedArtwork = true
            CoreOptions.imageLoadAdapter = this

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
        SampleAudioHolder.isActive = false
    }

}