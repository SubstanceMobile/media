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
import android.graphics.Bitmap
import android.net.Uri
import android.support.v4.media.MediaMetadataCompat
import android.util.Log
import mobile.substance.sdk.music.core.utils.CoreUtil
import java.util.*


open class MediaObject {
    var metadata: MediaMetadataCompat? = null
    internal var extraVars: HashMap<String, Any>? = null

    var id: Long = 0
    var timeLoaded: Long = 0
    var isLocked: Boolean = false
    var isAnimated = false
    ///////////////////////////////////////////////////////////////////////////
    // Uri
    ///////////////////////////////////////////////////////////////////////////
    private var posInList: Int = 0

    ///////////////////////////////////////////////////////////////////////////
    //Title
    ///////////////////////////////////////////////////////////////////////////
    private var context: Context? = null

    protected open val baseUri: Uri?
        get() = null

    open val uri: Uri
        get() = ContentUris.withAppendedId(baseUri, id)

    ///////////////////////////////////////////////////////////////////////////
    // Handles time data
    ///////////////////////////////////////////////////////////////////////////

    open val filePath: String
        get() = CoreUtil.getFilePath(context!!, uri)!!

    fun lock(): MediaObject {
        timeLoaded = System.currentTimeMillis()
        isLocked = true
        return this
    }

    ///////////////////////////////////////////////////////////////////////////
    // Context
    ///////////////////////////////////////////////////////////////////////////

    fun unlock(): MediaObject {
        timeLoaded = 0
        isLocked = false
        return this
    }

    protected fun onContextSet(context: Context) {
        //Override if you want to do something when the context is set
    }

    protected fun putLong(key: String, value: Long) {
        if (isLocked) throw Error("Object locked. Cannot edit")
        builder.putLong(key, value)
        metadata = builder.build()
    }

    protected fun putString(key: String, value: String) {
        if (isLocked) throw Error("Object locked. Cannot edit")
        builder.putString(key, value)
        metadata = builder.build()
    }

    protected fun putBitmap(key: String, value: Bitmap) {
        if (isLocked) throw Error("Object locked. Cannot edit")
        builder.putBitmap(key, value)
        metadata = builder.build()
    }

    protected fun putInteger(key: String, value: Int) {
        if (isLocked) throw Error("Object locked. Cannot edit")
        builder.putLong(key, value.toLong())
        metadata = builder.build()
    }

    protected open //Override to change
    val isContextRequired: Boolean
        get() = false

    ///////////////////////////////////////////////////////////////////////////
    // Position in list
    ///////////////////////////////////////////////////////////////////////////

    fun getContext(): Context {
        return context!!.applicationContext
    }

    fun setContext(context: Context): MediaObject {
        if (isContextRequired) {
            this.context = context
            onContextSet(context)
        } else
            Log.d(MediaObject.javaClass.simpleName, "Context was not requested. Ignoring")
        return this
    }

    fun getPosInList(): Int {
        return posInList
    }

    ///////////////////////////////////////////////////////////////////////////
    // Extra Data Storage
    ///////////////////////////////////////////////////////////////////////////

    fun setPosInList(posInList: Int): MediaObject {
        this.posInList = posInList
        return this
    }

    fun putData(key: String, data: Any) {
        if (extraVars == null) extraVars = HashMap<String, Any>()
        extraVars!!.put(key, data)
    }

    fun getData(key: String): Any? {
        if (extraVars == null || !extraVars!!.containsKey(key)) return null
        return extraVars!![key]
    }

    fun removeData(key: String): Boolean {
        return !(extraVars == null || !extraVars!!.containsKey(key)) && extraVars!!.remove(key) != null
    }

    companion object {
        internal val builder = MediaMetadataCompat.Builder()
    }

}
