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
import android.os.Handler
import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import android.support.v4.content.AsyncTaskLoader
import android.support.v4.content.ContentResolverCompat
import android.support.v4.content.Loader
import android.support.v4.os.CancellationSignal
import android.support.v4.os.OperationCanceledException
import android.util.Log
import mobile.substance.sdk.music.core.objects.MediaObject
import java.util.*

abstract class MediaLoader<Return : MediaObject>(context: Context) : AsyncTaskLoader<List<Return>>(context) {
    val observer = ForceLoadContentObserver()
    abstract val loaderId: Int
    abstract val uri: Uri
    open val selection: String? = null
    open val selectionArgs: Array<String>? = null
    open val sortOrder: String? = null

    private var cancellationSignal: CancellationSignal? = CancellationSignal()

    protected var listeners: MutableList<TaskListener<Return>> = ArrayList()
    private var currentData: List<Return> = ArrayList()
    internal val verifyListener = object : TaskListener<Return> {
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
     * The listener for [Loader] events

     * @param Return type of variable should be passed to the listener. When extending [MediaLoader], you will specify what this should be
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
    open fun sort(data: List<Return>) = Unit

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
                    uri, null, selection, selectionArgs, sortOrder,
                    cancellationSignal)

            if (cursor == null || !cursor.moveToFirst()) return emptyList()

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
            sort(generated)
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
                cancellationSignal?.cancel()
            }
        }
    }

    /* Runs on the UI thread */
    override fun deliverResult(list: List<Return>) {
        if (isStarted) super.deliverResult(list)
    }

    override fun onStartLoading() {
        println("onStartLoading() $id")
        if (takeContentChanged()) {
            unregisterObserver()
            forceLoad()
        }
    }

    override fun onContentChanged() {
        println("onContentChanged() $id")
        if (isStarted) unregisterObserver()
        super.onContentChanged()
    }

    override fun onStopLoading() {
        println("onStopLoading() $id")
        cancelLoad()
    }

    override fun onReset() {
        println("onReset() $id")
        super.onReset()
        onStopLoading()
    }

    internal fun registerObserver() = context.contentResolver.registerContentObserver(uri, true, observer)

    internal fun unregisterObserver() = context.contentResolver.unregisterContentObserver(observer)

}
