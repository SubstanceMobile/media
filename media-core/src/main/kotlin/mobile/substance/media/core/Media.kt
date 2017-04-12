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

import android.support.annotation.IntDef
import android.support.v7.app.AppCompatActivity
import kotlin.collections.ArrayList

object Media {
    private var activity: AppCompatActivity? = null
    private val holders: MutableList<MediaHolder> = ArrayList()

    @IntDef(MEDIA_TYPE_AUDIO, MEDIA_TYPE_VIDEO, MEDIA_TYPE_IMAGES)
    annotation class MediaType

    const val MEDIA_TYPE_AUDIO = 1L
    const val MEDIA_TYPE_VIDEO = 2L
    const val MEDIA_TYPE_IMAGES = 3L

    fun dispatchOnStartActivity(activity: AppCompatActivity) {
        this.activity = activity
        holders.forEach { it.onStartActivity(activity) }
    }

    fun dispatchOnStopActivity(activity: AppCompatActivity) {
        holders.forEach { it.onStopActivity(activity) }
    }

    fun clearHolders() {
        holders.forEach { it.onUnhook() }
    }

    fun destroy() {
        holders.forEach { it.onInvalidateHolder() }
        clearHolders()
    }

    internal fun activate(holder: MediaHolder) {
        holders.add(holder)
        holder.onHook(activity != null)
    }

    internal fun deactivate(holder: MediaHolder) {
        holder.onUnhook()
        holders.remove(holder)
    }

    internal fun getActivity() = activity

    internal fun getContext() = getActivity()?.applicationContext

    internal fun isActive(holder: MediaHolder): Boolean = holders.contains(holder)

}