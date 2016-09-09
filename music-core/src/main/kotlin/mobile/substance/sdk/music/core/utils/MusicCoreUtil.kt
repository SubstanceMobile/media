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

package mobile.substance.sdk.music.core.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.TypedValue
import android.webkit.URLUtil
import mobile.substance.sdk.music.core.MusicCoreOptions
import mobile.substance.sdk.music.core.dataLinkers.MusicData
import mobile.substance.sdk.music.core.objects.MediaObject
import mobile.substance.sdk.music.core.objects.Song
import java.io.File
import java.net.URL

object MusicCoreUtil {

    /**
     * Convenience method that simplifies calling intents and such

     * @param cxt The context to start the activity from
     * *
     * @param url The url so start
     */
    fun startUrl(cxt: Context, url: String) {
        cxt.startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse(url)))
    }

    /**
     * Formats strings to match time. Is either hh:mm:ss or mm:ss

     * @param time The raw time in ms
     * *
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

    fun dpToPx(context: Context, dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)
    }

    ///////////////////////////////////////////////////////////////////////////
    // Bitmap retrieval
    ///////////////////////////////////////////////////////////////////////////

    fun getArtwork(song: Song?, context: Context): Bitmap? {
        try {
            val albumArtPath = MusicData.findAlbumById(song?.songAlbumId!!)?.albumArtworkPath
            if (albumArtPath != null && albumArtPath.length > 0) return BitmapFactory.decodeFile(albumArtPath)
            if (song?.hasExplicitArtwork!! && song?.explicitArtworkPath!!.length > 0) {
                val url = getUrlFromUri(Uri.parse(song?.explicitArtworkPath))
                if (url != null) BitmapFactory.decodeStream(URL(url).openStream()) else BitmapFactory.decodeFile(url)
            }
        } catch (e: Exception) { e.printStackTrace() }

        return BitmapFactory.decodeResource(context.resources, MusicCoreOptions.defaultArt)
    }

    @JvmStatic fun getFilePath(context: Context, uri: Uri): String? {
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                val docId : String= DocumentsContract.getDocumentId(uri)
                val split = (java.lang.String.valueOf(docId) as java.lang.String).split(":")
                val type = split[0]
                if (("primary" as java.lang.String).equalsIgnoreCase(uri.scheme)) {
                    return Environment.getExternalStorageDirectory().path + File.separator + split[1]
                }
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = (java.lang.String.valueOf(docId) as java.lang.String).split(":")
                val type = split[0]
                var contentUri: Uri? = null
                if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf<String>(split[1])
                return getDataColumn(context, contentUri!!, selection, selectionArgs)
            }
        } else if (("content" as java.lang.String).equalsIgnoreCase(uri.scheme)) {
            return getDataColumn(context, uri, null, null)
        } else if (("file" as java.lang.String).equalsIgnoreCase(uri.scheme)) {
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
        for (list in data)
            for (item in list)
                if (item.id == id) return item
        return null
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    ///////////////////////////////////////////////////////////////////////////
    // Url
    ///////////////////////////////////////////////////////////////////////////

    fun getUrlFromUri(uri: Uri): String? {
        if (URLUtil.isValidUrl(uri.toString()) && uri.toString().startsWith("http")) return uri.toString() else return null
    }

}
