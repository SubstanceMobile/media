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

package mobile.substance.media.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.support.annotation.WorkerThread
import android.support.v4.content.ContextCompat
import android.webkit.URLUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import jp.wasabeef.glide.transformations.BlurTransformation
import mobile.substance.media.core.audio.Song
import mobile.substance.media.options.AudioCoreOptions


object AudioCoreUtil {
    private val TAG = AudioCoreUtil::class.java.simpleName

    /**
     * Formats strings to match time. Is either hh:mm:ss or mm:ss

     * @param time The raw time in ms
     *
     * @return The formatted string value
     */
    @SuppressLint("DefaultLocale")
    fun stringForTime(time: Long): String {
        val totalSeconds = time.toInt() / 1000
        val seconds = totalSeconds % 60
        val minutes = totalSeconds / 60 % 60
        val hours = totalSeconds / 3600
        return if (hours > 0) java.lang.String.format("%d:%02d:%02d", hours, minutes, seconds) else java.lang.String.format("%02d:%02d", minutes, seconds)
    }



    /**
     * Convenience method that simplifies getting the artwork for a specific song
     *
     * @param song song to get artwork of
     *
     * @param context required for the default artwork drawable fallback
     *
     * @return The retrieved Bitmap; null if a NetworkOnMainThreadException has been caught
     */
    @WorkerThread
    fun getArtwork(song: Song, context: Context, blurIfPossible: Boolean = false): Bitmap? {
        var bitmap: Bitmap? = null
        val requestBuilder = Glide.with(context)
                .load(song.artworkUri.toString())
                .asBitmap()
                .error(AudioCoreOptions.defaultArtResId)
        if (blurIfPossible) requestBuilder.transform(BlurTransformation(context))
        try {
            bitmap = requestBuilder.into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get()
        } catch (ignored: Exception) {}
        if (bitmap == null) {
            val drawable = ContextCompat.getDrawable(context, AudioCoreOptions.defaultArtResId)
            if (drawable is BitmapDrawable) {
                bitmap = drawable.bitmap
            } else {
                bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
            }
        }
        return bitmap
    }

    ///////////////////////////////////////////////////////////////////////////
    // Url
    ///////////////////////////////////////////////////////////////////////////

    fun isHttpUrl(url: String): Boolean = URLUtil.isValidUrl(url) && url.startsWith("http")


    /*
    @WorkerThread
    fun createSongFromFile(path: String): Song {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(path)
        return Song.Builder()
                .setTitle(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: path.substring(path.lastIndexOf("/") + 1))
                .setArtistName(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: "<unknown>")
                .setAlbumName(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM) ?: "<unknown>")
                .setDuration(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION).toLong())
                .build()
    }
    */

}
