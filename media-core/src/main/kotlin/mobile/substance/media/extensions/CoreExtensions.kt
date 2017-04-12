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

package mobile.substance.media.extensions

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.DocumentsContract
import android.provider.MediaStore
import java.io.File

fun mainThread(runnable: () -> Unit) = Handler(Looper.getMainLooper()).post(runnable)

fun Drawable.asBitmap(): Bitmap {
    if (this is BitmapDrawable) {
        return bitmap
    } else {
        val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        setBounds(0, 0, canvas.width, canvas.height)
        draw(canvas)
        return bitmap
    }
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
fun Uri.toFilePath(context: Context): String? {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, this)) {
        if (isExternalStorageDocument(this)) {
            val docId: String = DocumentsContract.getDocumentId(this)
            val split = (java.lang.String.valueOf(docId) as String).split(":")
            val type = split[0]
            if (("primary" as String).equals(this.scheme, true)) {
                return Environment.getExternalStorageDirectory().path + File.separator + split[1]
            }
        } else if (isMediaDocument(this)) {
            val docId = DocumentsContract.getDocumentId(this)
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
    } else if (("content" as String).equals(this.scheme, true)) {
        return queryDataColumnForContentUri(context, this, null, null)
    } else if (("file" as String).equals(this.scheme, true)) {
        return this.path
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
