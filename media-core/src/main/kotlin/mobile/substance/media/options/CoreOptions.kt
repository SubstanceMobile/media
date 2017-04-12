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

package mobile.substance.media.options

import android.graphics.Bitmap
import android.net.Uri
import android.support.annotation.DrawableRes
import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import android.widget.ImageView

object CoreOptions {
    var imageLoadAdapter: ImageLoadAdapter? = null

    interface ImageLoadAdapter {

        @UiThread
        fun onRequestLoad(imageSrc: Uri, @DrawableRes defaultRes: Int, target: ImageView)

        @UiThread
        fun onRequestLoad(image: ByteArray?, @DrawableRes defaultRes: Int, target: ImageView)

        @WorkerThread
        fun onRequestBitmap(imageSrc: Uri, @DrawableRes defaultRes: Int): Bitmap

        @WorkerThread
        fun onRequestBitmap(image: ByteArray?, @DrawableRes defaultRes: Int): Bitmap

    }

}