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

package mobile.substance.media.core.audio

import android.graphics.Bitmap
import android.net.Uri
import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import android.widget.ImageView
import mobile.substance.media.core.MediaObject

// An interface used to implement an image/artwork, whether for a video thumbnail, an actual image or a song's album cover
interface ArtworkHolder {
    @UiThread
    fun requestArtworkLoad(target: ImageView) = Unit

    @WorkerThread
    fun requestArtworkBitmap(): Bitmap
}