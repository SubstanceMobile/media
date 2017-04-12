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

package mobile.substance.media.local.core

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import android.support.v4.content.AsyncTaskLoader
import android.support.v4.content.ContentResolverCompat
import android.support.v4.content.Loader
import android.support.v4.os.CancellationSignal
import android.support.v4.os.OperationCanceledException
import mobile.substance.media.core.mediaApiError
import java.lang.reflect.ParameterizedType
import java.util.*

abstract class MediaLoader<Output : MediaStoreAttributes>(context: Context) : AsyncTaskLoader<List<Output>>(context) {
    val observer = ForceLoadContentObserver()
    abstract val uri: Uri
    open val selection: String? = null
    open val selectionArgs: Array<String>? = null
    open val sortOrder: String? = null
    private var cancellationSignal: CancellationSignal? = CancellationSignal()
    var applicator: Applicator<Output>? = null
    var listener: Listener<Output>? = null

    /**
     * A callback allowing users of a X-local library to apply additional attributes
     */
    interface Applicator<Output> {
        fun newInstance(): Output
        fun Output.apply()
    }

    /**
     * The listener for [Loader] events

     * @param Output type of variable should be passed to the listener. When extending [MediaLoader], you will specify what this should be
     */
    interface Listener<in Output> {
        fun onLoaded(item: Output)

        fun onLoaded(output: List<Output>)
    }

    @WorkerThread
    protected abstract fun Output.applyDefault(cursor: Cursor)

    ///////////////////////////////////////////////////////////////////////////
    // Sorting
    ///////////////////////////////////////////////////////////////////////////

    @UiThread
    open fun sort(data: List<Output>) = Unit

    @WorkerThread
    override fun loadInBackground(): List<Output> {
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
            val generated = ArrayList<Output>()
            do {
                val item = applicator?.newInstance()
                if (item != null) {
                    println("item is NOT null!")
                    item.applyDefault(cursor)
                    with(applicator!!) { item.apply() }
                    generated.add(item)
                    listener?.onLoaded(item)
                } else cancelLoadInBackground()
                println("We are actually doing stuff...")
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

    @UiThread
    override fun deliverResult(list: List<Output>) {
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
        if (isStarted) {
            println("onContentChanged() -> isStarted!")
            unregisterObserver()
        }
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

    fun registerObserver() = context.contentResolver.registerContentObserver(uri, true, observer)

    fun unregisterObserver() = context.contentResolver.unregisterContentObserver(observer)

}
