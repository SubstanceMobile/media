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

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import mobile.substance.media.core.MediaHolder

abstract class LocalMediaHolder : MediaHolder(), LoaderManager.LoaderCallbacks<List<*>> {
    private val loaderIds = ArrayList<Int>()
    private val buildFinishedListeners = ArrayList<OnBuildFinishedListener>()
    private val buildState = ArrayList<Boolean>()

    interface OnBuildFinishedListener {
        fun onBuildFinished()
    }

    private fun checkIsBuilt() {
        if (isBuilt()) {
            buildFinishedListeners.forEach { it.onBuildFinished() }
            buildFinishedListeners.clear()
        }
    }

    fun isBuilt(): Boolean = isActive && isActivityStarted() && buildState.all { it }

    fun listenForBuildFinish(listener: OnBuildFinishedListener) {
        if (isBuilt()) {
            listener.onBuildFinished()
            return
        }
        buildFinishedListeners.add(listener)
    }

    override fun onStartActivity(activity: Activity) = initLoaders()

    protected fun <Output : MediaStoreAttributes> addLoader(id: Int, listener: MediaLoader.Listener<Output>, applicator: MediaLoader.Applicator<Output>? = null) {
        loaderIds.add(id)
        buildState.add(false)
        val mediaLoader = getActivity()?.supportLoaderManager?.initLoader(id, Bundle.EMPTY, this) as MediaLoader<Output>
        mediaLoader.listener = listener
        mediaLoader.applicator = applicator
    }

    protected fun removeLoader(id: Int) {
        val index = loaderIds.indexOf(id)
        loaderIds.remove(id)
        buildState.removeAt(index)
        getActivity()?.supportLoaderManager?.destroyLoader(id)
    }

    override fun onStopActivity(activity: Activity) {
        // Do nothing, activity handles Loader lifecycle here
    }

    fun build() = getLoaders().forEach { it?.forceLoad() }

    fun startObserving() = getLoaders().forEach { it?.registerObserver() }

    fun stopObserving() = getLoaders().forEach { it?.unregisterObserver() }

    final override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<*>>? = createLoader(id) as Loader<List<*>>?

    final override fun onLoadFinished(loader: Loader<List<*>>?, data: List<*>?) {
        (loader as MediaLoader<*>).listener?.onLoaded(data as List<Nothing>)
        buildState[loaderIds.indexOf(loader.id)] = true
        checkIsBuilt()
    }

    final override fun onLoaderReset(loader: Loader<List<*>>?) = Unit

    protected fun getLoaders(): List<MediaLoader<*>?> = List(loaderIds.size, { getActivity()?.supportLoaderManager?.initLoader(loaderIds[it], Bundle.EMPTY, this) as MediaLoader<*>? })

    protected abstract fun initLoaders()

    protected abstract fun createLoader(id: Int): MediaLoader<*>?

}