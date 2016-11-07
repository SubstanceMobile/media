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

package mobile.substance.sdk.music.tags

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import mobile.substance.sdk.music.core.objects.Album

object MediaStoreHelper {

    fun updateMedia(paths: Array<String?>, context: Context, callbacks: MediaStoreCallback) {
        var count = 0
        MediaScannerConnection.scanFile(context, paths, Array<String>(paths.size, { paths[it]?.substring(paths[it]?.lastIndexOf(".")!! + 1)!! })) { path, uri ->
            Log.d(MediaStoreHelper::class.java.simpleName, "successfully scanned $path")
            count++
            callbacks.onScanFinished(path, uri)

            if (count == paths.size)
                callbacks.onAllFinished()
        }
    }

    fun updateMedia(paths: Array<String?>, context: Context, callbacks: MediaStoreCallback, album: Album? = null, newArtworkPath: String) {
        var count = 0
        MediaScannerConnection.scanFile(context, paths, Array<String>(paths.size, { paths[it]?.substring(paths[it]?.lastIndexOf(".")!! + 1)!! })) { path, uri ->
            Log.d(MediaStoreHelper::class.java.simpleName, "successfully scanned $path")
            count++
            callbacks.onScanFinished(path, uri)

            if (count == paths.size)
                callbacks.onAllFinished()
        }

        if (album != null) refreshArtwork(context, album, newArtworkPath)
    }

    fun refreshArtwork(context: Context, album: Album, newArtworkPath: String) {
        if (album.albumArtworkPath != null)
            context.contentResolver.delete(Uri.parse("content://media/external/audio/albumart"), null, null)
        val cv = ContentValues()
        cv.put("album_id", album.id)
        cv.put("_data", newArtworkPath)
        context.contentResolver.insert(Uri.parse("content://media/external/audio/albumart"), cv)
    }

}
