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

package mobile.substance.sdk.music.core.objects

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.support.v4.media.MediaMetadataCompat
import android.util.Log
import java.util.*


abstract class MediaObject {
    internal var data: HashMap<String, Any>? = null
    var id: Long = 0
    var instanceCreatedAt: Long = 0
    var isLocked: Boolean = false

    ///////////////////////////////////////////////////////////////////////////
    // MediaMetadataCompat conversion
    ///////////////////////////////////////////////////////////////////////////

    // METADATA_KEY_MEDIA_ID key cannot be used to store a Long
    fun getMetadata(): MediaMetadataCompat = toMetadataCompat(MediaMetadataCompat.Builder().putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, id.toString()).build())

    internal abstract fun toMetadataCompat(source: MediaMetadataCompat): MediaMetadataCompat

    ///////////////////////////////////////////////////////////////////////////
    // Uri
    ///////////////////////////////////////////////////////////////////////////

    protected open val baseUri: Uri?
        get() = null

    open val uri: Uri
        get() = ContentUris.withAppendedId(baseUri, id)

    ///////////////////////////////////////////////////////////////////////////
    // Handles time data
    ///////////////////////////////////////////////////////////////////////////

    fun lock(): MediaObject {
        instanceCreatedAt = System.currentTimeMillis()
        isLocked = true
        return this
    }

    fun unlock(): MediaObject {
        instanceCreatedAt = 0
        isLocked = false
        return this
    }

    ///////////////////////////////////////////////////////////////////////////
    // Context
    ///////////////////////////////////////////////////////////////////////////

    private var context: Context? = null

    protected fun onContextSet(context: Context) {
        //Override if you want to do something when the context is set
    }

    protected open //Override to change
    val isContextRequired: Boolean
        get() = false

    fun getContext(): Context {
        return context!!.applicationContext
    }

    fun setContext(context: Context): MediaObject {
        if (isContextRequired) {
            this.context = context
            onContextSet(context)
        }
        return this
    }

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
