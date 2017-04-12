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

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.BaseColumns
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.support.annotation.WorkerThread
import java.io.File
import java.lang.reflect.Method

object CoreUtil {

    /**
     * Given a media filename, returns it's id in the media content provider

     * @param providerUri
     * @param context
     * @param path
     * @return the file's id in the media content provider
     */
    @WorkerThread
    fun retrieveMediaId(providerUri: Uri, context: Context, path: String): Long {
        val projection = arrayOf(MediaStore.MediaColumns._ID, MediaStore.MediaColumns.DATA)
        val cursor = context.contentResolver.query(
                providerUri, projection,
                MediaStore.MediaColumns.DATA + "= ?", arrayOf(path), null)
        return cursor?.use {
            if (cursor.moveToFirst()) cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID)) else 0L
        } ?: 0
    }

}