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

package mobile.substance.sdk.music.loading.tasks

import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import android.support.v4.app.LoaderManager
import android.support.v4.content.AsyncTaskLoader
import android.support.v4.content.ContentResolverCompat
import android.support.v4.content.Loader
import android.support.v4.os.CancellationSignal
import android.support.v4.os.OperationCanceledException
import android.support.v7.app.AppCompatActivity
import android.util.Log
import mobile.substance.sdk.music.core.objects.MediaObject
import java.io.FileDescriptor
import java.io.PrintWriter
import java.util.*

abstract class MediaLoader<Return : MediaObject>(private val activity: AppCompatActivity) : AsyncTaskLoader<List<Return>>(activity), LoaderManager.LoaderCallbacks<List<Return>> {
    val observer: ContentObserver = ForceLoadContentObserver()

    abstract val loaderId: Int
    abstract val uri: Uri
    open val projection: Array<String>? = null
    open val selection: String? = null
    open val selectionArgs: Array<String>? = null
    open val sortOrder: String? = null

    var cursor: Cursor? = null
    var cancellationSignal: CancellationSignal? = null

    protected var listeners: MutableList<TaskListener<Return>> = ArrayList()
    private var currentData: List<Return> = ArrayList()
    private val verifyListener = object : TaskListener<Return> {
        override fun onOneLoaded(item: Return, pos: Int) {
            /*if (!currentData.contains(item)) */
            for (listener in listeners) listener.onOneLoaded(item, pos)
        }

        override fun onCompleted(result: List<Return>) {
            if (currentData !== result)
                for (listener in listeners) listener.onCompleted(result)
            currentData = ArrayList<Return>()
        }
    }
    /**
     * Tells whether the loading did finish at least once in its lifetime
     */
    var finishedOnce: Boolean = false
    private var updatedQueue = false

    /**
     * The listener for [Loader] events

     * @param Return type of variable should be passed to the listener. When extending [Loader], you will specify what this should be
     */
    interface TaskListener<in Return> {
        fun onOneLoaded(item: Return, pos: Int)

        fun onCompleted(result: List<Return>)
    }

    ///////////////////////////////////////////////////////////////////////////
    // Listener
    ///////////////////////////////////////////////////////////////////////////

    @UiThread
    fun addListener(listener: TaskListener<Return>) {
        listeners.add(listener)
    }

    @UiThread
    fun removeListener(listener: TaskListener<Return>) {
        listeners.remove(listener)
    }

    @WorkerThread
    protected abstract fun buildObject(cursor: Cursor): Return?

    ///////////////////////////////////////////////////////////////////////////
    // Sorting
    ///////////////////////////////////////////////////////////////////////////
    @UiThread
    protected fun sort(data: List<Return>) {
        //Do nothing
    }

    @UiThread
    fun init() {
        destroy()
        activity.supportLoaderManager.initLoader(loaderId, Bundle.EMPTY, this)
    }

    @UiThread
    fun destroy() {
        try {
            activity.supportLoaderManager.destroyLoader(loaderId)
        } catch (ignored: Exception) {}
    }

    @UiThread
    fun run() = activity.supportLoaderManager.initLoader(loaderId, Bundle.EMPTY, this).forceLoad()

    @WorkerThread
    override fun loadInBackground(): List<Return> {
        synchronized(this) {
            if (isLoadInBackgroundCanceled) {
                throw OperationCanceledException()
            }
            cancellationSignal = CancellationSignal()
        }
        try {
            val cursor = ContentResolverCompat.query(context.contentResolver,
                    uri, projection, selection, selectionArgs, sortOrder,
                    cancellationSignal)
            if (cursor != null) {
                try {
                    // Ensure the cursor window is filled.
                    cursor.count
                    cursor.registerContentObserver(observer)
                } catch (ex: RuntimeException) {
                    cursor.close()
                    throw ex
                }
                if (!cursor.moveToFirst()) return emptyList()
            } else return emptyList()

            //If there is data then continue
            val generated = ArrayList<Return>()
            do {
                val obj = buildObject(cursor)
                if (obj != null) {
                    obj.positionInList = cursor.position
                    obj.setContext(context).lock()
                    generated.add(obj)
                    verifyListener.onOneLoaded(obj, cursor.position)
                }
            } while (cursor.moveToNext() && !cursor.isClosed && !isReset && !isAbandoned)
            return generated
        } finally {
            synchronized(this) {
                cancellationSignal = null
            }
        }
    }

    override fun cancelLoadInBackground() {
        super.cancelLoadInBackground()

        synchronized(this) {
            if (cancellationSignal != null) {
                cancellationSignal!!.cancel()
            }
        }
    }

    /* Runs on the UI thread */
    override fun deliverResult(list: List<Return>) {
        if (isReset) {
            // An async query came in while the loader is stopped
            cursor?.close()
            return
        }
        val oldCursor = this.cursor
        this.cursor = cursor

        if (isStarted) {
            super.deliverResult(list)
        }

        if (oldCursor != null && oldCursor !== cursor && !oldCursor.isClosed) {
            oldCursor.close()
        }
    }

    /**
     * Starts an asynchronous load of the contacts list data. When the result is ready the callbacks
     * will be called on the UI thread. If a previous load has been completed and is still valid
     * the result may be passed to the callbacks immediately.

     * Must be called from the UI thread
     */
    override fun onStartLoading() {
        if (cursor != null) {
            deliverResult(currentData)
        }
        if (takeContentChanged() || cursor == null) {
            forceLoad()
        }
    }

    /**
     * Must be called from the UI thread
     */
    override fun onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad()
    }

    override fun onCanceled(list: List<Return>?) {
        if (cursor != null && !cursor!!.isClosed) {
            cursor!!.close()
        }
    }

    override fun onReset() {
        super.onReset()

        // Ensure the loader is stopped
        onStopLoading()

        if (cursor != null && !cursor!!.isClosed) {
            cursor!!.close()
        }
        cursor = null
    }

    override fun dump(prefix: String, fd: FileDescriptor?, writer: PrintWriter, args: Array<String>?) {
        super.dump(prefix, fd, writer, args)
        writer.print(prefix)
        writer.print("uri=")
        writer.println(uri)
        writer.print(prefix)
        writer.print("projection=")
        writer.println(Arrays.toString(projection))
        writer.print(prefix)
        writer.print("selection=")
        writer.println(selection)
        writer.print(prefix)
        writer.print("selectionArgs=")
        writer.println(Arrays.toString(selectionArgs))
        writer.print(prefix)
        writer.print("sortOrder=")
        writer.println(sortOrder)
        writer.print(prefix)
        writer.print("cursor=")
        writer.println(cursor)
        writer.print(prefix)
    }

    ///////////////////////////////////////////////////////////////////////////
    // LoaderManager.LoaderCallbacks
    ///////////////////////////////////////////////////////////////////////////

    override fun onLoaderReset(loader: Loader<List<Return>>?) {
        // Do nothing
    }

    override fun onLoadFinished(loader: Loader<List<Return>>?, data: List<Return>?) {
        if (!finishedOnce) finishedOnce = true
        if (data != null) sort(data)
        val result = data ?: emptyList()
        verifyListener.onCompleted(result)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<Return>> {
        return this
    }

    override fun onContentChanged() {
        Log.i(MediaLoader::class.java.simpleName, "onContentChanged() has been called - the Loader has received an update notification. Loader id $loaderId")
        super.onContentChanged()
    }

}
