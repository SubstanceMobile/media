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
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.BaseColumns
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.MediaStore.MediaColumns
import android.support.annotation.WorkerThread
import android.support.v4.content.ContextCompat
import android.webkit.URLUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import jp.wasabeef.glide.transformations.BlurTransformation
import mobile.substance.sdk.music.core.dataLinkers.MusicData
import mobile.substance.sdk.music.core.objects.MediaObject
import mobile.substance.sdk.music.core.objects.Song
import mobile.substance.sdk.options.MusicCoreOptions
import java.io.File


object MusicCoreUtil {

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
    // Bitmap retrieval
    ///////////////////////////////////////////////////////////////////////////

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                val docId: String = DocumentsContract.getDocumentId(uri)
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
                return queryDataColumnForContentUri(context, contentUri!!, selection, selectionArgs)
            }
        } else if (("content" as String).equals(uri.scheme, true)) {
            return queryDataColumnForContentUri(context, uri, null, null)
        } else if (("file" as String).equals(uri.scheme, true)) {
            return uri.path
        }
        return null
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean = "com.android.externalstorage.documents" == uri.authority

    private fun isMediaDocument(uri: Uri): Boolean = "com.android.providers.media.documents" == uri.authority

    private fun queryDataColumnForContentUri(context: Context, uri: Uri, selection: String?, selectionArgs: Array<String>?): String? {
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

    /**
     * Given a media filename, returns it's id in the media content provider

     * @param providerUri
     * @param context
     * @param path
     * @return the file's id in the media content provider
     */
    @WorkerThread
    fun retrieveMediaId(providerUri: Uri, context: Context, path: String): Long {
        val projection = arrayOf(MediaColumns._ID, MediaColumns.DATA)
        val cursor = context.contentResolver.query(
                providerUri, projection,
                MediaColumns.DATA + "= ?", arrayOf(path), null)
        return cursor?.use {
            if (cursor.moveToFirst()) cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID)) else 0L
        } ?: 0
    }

    fun findByMediaId(id: Long, vararg data: List<MediaObject>): MediaObject? {
        return data
                .flatMap { it }
                .firstOrNull { it.id == id }
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
        var bitmap: Bitmap?
        val albumArtPath = MusicData.findAlbumById(song.songAlbumId ?: 0)?.albumArtworkPath
        val requestBuilder = Glide.with(context)
                .load(if (song.hasExplicitArtwork && song.explicitArtworkUri!! != Uri.EMPTY) song.explicitArtworkUri.toString() else albumArtPath)
                .asBitmap()
                .error(MusicCoreOptions.defaultArt)
        if (blurIfPossible) requestBuilder.transform(BlurTransformation(context))
        bitmap = requestBuilder.into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get()
        if (bitmap == null) {
            val drawable = ContextCompat.getDrawable(context, MusicCoreOptions.defaultArt)
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

}
