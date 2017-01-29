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

package mobile.substance.sdk.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.NetworkOnMainThreadException
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.support.annotation.WorkerThread
import android.support.v4.content.ContextCompat
import android.util.TypedValue
import android.webkit.URLUtil
import mobile.substance.sdk.options.MusicCoreOptions
import mobile.substance.sdk.music.core.dataLinkers.MusicData
import mobile.substance.sdk.music.core.objects.MediaObject
import mobile.substance.sdk.music.core.objects.Song
import java.io.File
import java.net.URL
import android.graphics.drawable.BitmapDrawable
import android.graphics.Canvas

object MusicCoreUtil {

    /**
     * Convenience method that simplifies calling intents and such

     * @param cxt The context to start the activity from
     *
     * @param url The url so start
     */
    fun startUrl(cxt: Context, url: String) = cxt.startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse(url)))

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
    // Android Version Utils
    ///////////////////////////////////////////////////////////////////////////

    val isKitKat: Boolean
        get() = Build.VERSION.SDK_INT >= 19

    val isLollipop: Boolean
        get() = Build.VERSION.SDK_INT >= 21

    val isMarshmallow: Boolean
        get() = Build.VERSION.SDK_INT >= 23

    ///////////////////////////////////////////////////////////////////////////
    // Unit conversions
    ///////////////////////////////////////////////////////////////////////////

    fun dpToPx(context: Context, dp: Float): Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)

    ///////////////////////////////////////////////////////////////////////////
    // Bitmap retrieval
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Convenience method that simplifies getting the artwork for a specific song. Needs to be called from a worker thread
     * in order not to cause a @code NetworkOnMainThreadException
     *
     * @param song song to get artwork of
     *
     * @param context required for the default artwork drawable fallback
     *
     * @return The retrieved Bitmap; null if a NetworkOnMainThreadException has been caught
     */
    @WorkerThread
    fun getArtwork(song: Song, context: Context): Bitmap? {
        try {
            val albumArtPath = MusicData.findAlbumById(song.songAlbumId ?: 0)?.albumArtworkPath
            if (albumArtPath != null && albumArtPath.isNotEmpty()) return BitmapFactory.decodeFile(albumArtPath)
            if (song.hasExplicitArtwork && song.explicitArtworkUri!! != Uri.EMPTY) {
                val url = getUrlFromUri(song.explicitArtworkUri!!)
                return if (url != null) BitmapFactory.decodeStream(URL(url).openStream()) else BitmapFactory.decodeFile(song.explicitArtworkUri!!.path)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is NetworkOnMainThreadException) return null
        }

        val drawable = ContextCompat.getDrawable(context, MusicCoreOptions.defaultArt)
        if (drawable is BitmapDrawable) return drawable.bitmap

        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    /**
     * Convenience method to get the file path of a content Uri
     *
     * @param context required to access the contentResolver
     *
     * @param uri the uri to get the file path of
     *
     * @return the file path
     */
    fun getFilePath(context: Context, uri: Uri): String? {
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                val docId : String= DocumentsContract.getDocumentId(uri)
                val split = (java.lang.String.valueOf(docId) as String).split(":")
                val type = split[0]
                if (("primary" as String).equals(uri.scheme, true)) {
                    return Environment.getExternalStorageDirectory().path + File.separator + split[1]
                }
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = (java.lang.String.valueOf(docId) as String).split(":")
                val type = split[0]
                var contentUri: Uri? = null
                if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf<String>(split[1])
                return getDataColumn(context, contentUri!!, selection, selectionArgs)
            }
        } else if (("content" as String).equals(uri.scheme, true)) {
            return getDataColumn(context, uri, null, null)
        } else if (("file" as String).equals(uri.scheme, true)) {
            return uri.path
        }
        return null
    }

    private fun getDataColumn(context: Context, uri: Uri, selection: String?, selectionArgs: Array<String>?): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            if (cursor != null)
                cursor.close()
        }
        return null
    }

    fun findByMediaId(id: Long, vararg data: List<MediaObject>): MediaObject? {
        return data
                .flatMap { it }
                .firstOrNull { it.id == id }
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean = "com.android.externalstorage.documents" == uri.authority

    private fun isMediaDocument(uri: Uri): Boolean = "com.android.providers.media.documents" == uri.authority

    ///////////////////////////////////////////////////////////////////////////
    // Url
    ///////////////////////////////////////////////////////////////////////////

    fun getUrlFromUri(uri: Uri): String? = if (URLUtil.isValidUrl(uri.toString()) && uri.toString().startsWith("http")) uri.toString() else null

}
