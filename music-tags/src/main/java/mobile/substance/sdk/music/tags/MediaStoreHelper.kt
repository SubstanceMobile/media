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

import android.content.Context
import android.media.MediaScannerConnection

object MediaStoreHelper {

    fun updateMedia(paths: Array<String>, context: Context, callbacks: MediaStoreCallback) {
        val mimeTypes = arrayOfNulls<String>(paths.size)
        for (i in paths.indices) {
            mimeTypes[i] = "audio/" + paths[i].substring(paths[i].lastIndexOf(".") + 1, paths[i].length)
        }

        val count = intArrayOf(0)
        MediaScannerConnection.scanFile(context, paths, mimeTypes) { path, uri ->
            count[0]++
            callbacks.onScanFinished(path, uri)

            if (count[0] == paths.size)
                callbacks.onAllFinished()
        }
    }
}
