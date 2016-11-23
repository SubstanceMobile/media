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

import android.content.Context
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask
import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import android.util.Log
import mobile.substance.sdk.music.core.objects.MediaObject
import java.util.*

/**
 * A class that needs to be extended in order to load a list of a certain type of object.

 * @param Return The type to return when done loading. This type should **NOT** be a [List]
 */
abstract class Loader<Return : MediaObject>(context: Context, vararg params: Any) {
    protected var contentObserver: ContentObserver? = null
    /**
     * @return Application congress taken from provided congress
     */
    var context: Context
        protected set
    protected var runParams: Array<Any>

    /**
     * Tells whether the loading did finish at least once in its lifetime
     */
    var finishedOnce: Boolean = false

    ///////////////////////////////////////////////////////////////////////////
    // Used for generating the Cursor
    ///////////////////////////////////////////////////////////////////////////
    protected var listeners: MutableList<TaskListener<Return>> = ArrayList()
    private var task: LoadTask? = null
    //Currently loaded data. When calling update() this will be set, then emptied
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
    private var updatedQueue = false

    ///////////////////////////////////////////////////////////////////////////
    // Underlying AsyncTask
    ///////////////////////////////////////////////////////////////////////////
    private var observerLock = false

    init {
        this.context = context.applicationContext
        this.runParams = arrayOf(params)
        contentObserver = observer
    }

    ///////////////////////////////////////////////////////////////////////////
    // Run
    ///////////////////////////////////////////////////////////////////////////

    @WorkerThread
    protected abstract fun buildObject(cursor: Cursor): Return?

    protected abstract val uri: Uri

    ///////////////////////////////////////////////////////////////////////////
    // Sorting
    ///////////////////////////////////////////////////////////////////////////

    protected open val projection: Array<String>?
        get() = null

    ///////////////////////////////////////////////////////////////////////////
    // Update
    ///////////////////////////////////////////////////////////////////////////

    protected open val selection: String?
        get() = null

    protected open val selectionArgs: Array<String>?
        get() = null

    protected open val sortOrder: String?
        get() = null

    @UiThread
    fun run() {
        try {
            task?.cancel(true)
        } catch (ignored: IllegalStateException) {
        }
        task = LoadTask()
        task!!.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, *runParams)
    }

    @UiThread
    protected fun sort(data: List<Return>) {
        //Do nothing
    }

    /**
     * Make a content observer to be registered/unregistered. Do **NOT** pass variables. Whatever is returned here will be set in a final variable and accessed from there

     * @return The content observer
     */
    protected open val observer: ContentObserver?
        @UiThread
        get() = null

    @UiThread
    fun update(currentData: List<Return>?) {
        if (task != null && !task!!.isExecuting && currentData != null) {
            updatedQueue = false
            this.currentData = currentData
            run()
        } else {
            Log.e(javaClass.simpleName, "Update: FAILED")
            updatedQueue = true
        }
    }

    /**
     * Registers a [ContentObserver] and removes any previous calls to [.unregisterMediaStoreListener]
     */
    @UiThread
    fun registerMediaStoreListener() {
        if (contentObserver != null) {
            context.contentResolver.registerContentObserver(uri, true, contentObserver)
            observerLock = false
        }
    }


    /**
     * Unregisters a content observer until the async task will register it again when it completes.
     */
    @UiThread
    protected fun unregisterMediaStoreListenerTemporarily() {
        if (contentObserver != null) {
            context.contentResolver.unregisterContentObserver(contentObserver)
        }
    }

    /**
     * Same as [.unregisterMediaStoreListenerTemporarily] except it also makes sure the async task doesn't ever register the listener again until [.registerMediaStoreListener] is called from an outside source
     */
    @UiThread
    fun unregisterMediaStoreListener() {
        if (contentObserver != null) {
            unregisterMediaStoreListenerTemporarily()
            observerLock = true
        }
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

    /**
     * Wrapper method around [AsyncTask.publishProgress]

     * @param progress What to pass to the AsyncTask
     */
    @WorkerThread
    fun notifyOneLoaded(progress: Return) {
        task!!.oneLoaded(progress)
    }

    ///////////////////////////////////////////////////////////////////////////
    // Misc.
    ///////////////////////////////////////////////////////////////////////////

    /**
     * The listener for [Loader] events

     * @param Return type of variable should be passed to the listener. When extending [Loader], you will specify what this should be
     */
    interface TaskListener<in Return> {
        fun onOneLoaded(item: Return, pos: Int)

        fun onCompleted(result: List<Return>)
    }

    internal inner class LoadTask : AsyncTask<Any, Return, List<Return>>() {
        var isExecuting = false
            private set

        override fun onPreExecute() {
            super.onPreExecute()
            unregisterMediaStoreListenerTemporarily()
        }

        override fun doInBackground(vararg params: Any): List<Return> {
            isExecuting = true
            val cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)
            try {
                //If there is no data return an empty list
                if (cursor == null || !cursor.moveToFirst()) return ArrayList()

                //If there is data then continue
                val generated = ArrayList<Return>()
                do {
                    val obj = buildObject(cursor)
                    if (obj != null) {
                        obj.positionInList = cursor.position
                        obj.setContext(context).lock()
                        generated.add(obj)
                        notifyOneLoaded(obj)
                    }
                } while (cursor.moveToNext() && !cursor.isClosed && !isCancelled)
                return generated
            } finally {
                if (cursor != null && !cursor.isClosed) cursor.close()
                isExecuting = false
            }
        }

        @SafeVarargs
        @WorkerThread
        fun oneLoaded(vararg progress: Return) {
            publishProgress(*progress)
        }

        @SafeVarargs
        override fun onProgressUpdate(vararg values: Return) {
            super.onProgressUpdate(*values)
            for (`val` in values) verifyListener.onOneLoaded(`val`, `val`.positionInList)
        }

        override fun onPostExecute(result: List<Return>) {
            super.onPostExecute(result)
            if (!finishedOnce)
                finishedOnce = true
            sort(result)
            verifyListener.onCompleted(result)
            if (!observerLock) registerMediaStoreListener()
            if (updatedQueue) update(result)

        }
    }
}
