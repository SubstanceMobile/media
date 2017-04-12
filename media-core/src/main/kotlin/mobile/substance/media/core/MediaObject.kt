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

package mobile.substance.media.core

import android.content.Context
import android.support.v4.media.MediaMetadataCompat
import java.util.*

abstract class MediaObject {
    private var data: HashMap<String, Any>? = null
    var isLocked: Boolean = false

    fun getContext(): Context? = Media.getContext()

    ///////////////////////////////////////////////////////////////////////////
    // MediaMetadataCompat conversion
    ///////////////////////////////////////////////////////////////////////////

    // METADATA_KEY_MEDIA_ID key cannot be used to store a Long
    fun getMetadata(): MediaMetadataCompat = MediaMetadataCompat.Builder()
            .withMetadata()
            .build()

    protected abstract fun MediaMetadataCompat.Builder.withMetadata(): MediaMetadataCompat.Builder

    ///////////////////////////////////////////////////////////////////////////
    // Extra Data Storage
    ///////////////////////////////////////////////////////////////////////////

    fun containsKey(key: String): Boolean? {
        return this.data?.containsKey(key)
    }

    fun put(key: String, data: Any) {
        if (this.data == null) this.data = HashMap<String, Any>()
        this.data!!.put(key, data)
    }

    fun get(key: String): Any? {
        if (data == null || !data!!.containsKey(key)) return null
        return data!![key]
    }

    fun remove(key: String): Boolean {
        return !(data == null || !data!!.containsKey(key)) && data!!.remove(key) != null
    }

}
