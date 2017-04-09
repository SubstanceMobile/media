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

package mobile.substance.media.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.support.annotation.WorkerThread
import android.webkit.URLUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import jp.wasabeef.glide.transformations.BlurTransformation
import mobile.substance.media.core.audio.Song
import mobile.substance.media.options.AudioCoreOptions
import java.io.ByteArrayOutputStream


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
