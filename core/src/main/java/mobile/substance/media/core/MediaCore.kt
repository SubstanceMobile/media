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

import android.app.Activity
import android.content.Context
import android.support.annotation.IntDef
import android.support.v7.app.AppCompatActivity
import java.util.*

object MediaCore {
    private var activity: AppCompatActivity? = null
    private val audioHolders: MutableList<MediaHolder> = ArrayList()
    private val videoHolders: MutableList<MediaHolder> = ArrayList()
    private val imageHolders: MutableList<MediaHolder> = ArrayList()

    fun getHoldersOfType(@MediaType type: Long): List<MediaHolder> {
        when (type) {
            MEDIA_TYPE_AUDIO -> return audioHolders
            MEDIA_TYPE_VIDEO -> return videoHolders
            MEDIA_TYPE_IMAGES -> return imageHolders
            else -> return emptyList()
        }
    }

    private fun dispatchEvent(event: (MediaHolder).() -> Any) {
        audioHolders.forEach { it.event() }
        videoHolders.forEach { it.event() }
        imageHolders.forEach { it.event() }
    }

    @IntDef(MEDIA_TYPE_AUDIO, MEDIA_TYPE_VIDEO, MEDIA_TYPE_IMAGES)
    annotation class MediaType
    const val MEDIA_TYPE_AUDIO = 1L
    const val MEDIA_TYPE_VIDEO = 2L
    const val MEDIA_TYPE_IMAGES = 3L

    fun dispatchOnStartActivity(activity: AppCompatActivity) {
        this.activity = activity
        dispatchEvent { onStartActivity(activity) }
    }

    fun dispatchOnStopActivity(activity: AppCompatActivity) {
        dispatchEvent { onStopActivity(activity) }
    }

    fun clearHolders() {
        audioHolders.clear()
        videoHolders.clear()
        imageHolders.clear()
    }

    fun destroy() {
        dispatchEvent { onInvalidateHolder() }
        clearHolders()
    }

    fun activate(holder: MediaHolder) {
        when (holder.getType()) {
            MEDIA_TYPE_AUDIO -> audioHolders.add(holder)
            MEDIA_TYPE_VIDEO -> videoHolders.add(holder)
            MEDIA_TYPE_IMAGES -> imageHolders.add(holder)
            else -> return
        }
        holder.onHook(activity != null)
    }

    fun deactivate(holder: MediaHolder) {
        when (holder.getType()) {
            MEDIA_TYPE_AUDIO -> audioHolders.remove(holder)
            MEDIA_TYPE_VIDEO -> videoHolders.remove(holder)
            MEDIA_TYPE_IMAGES -> imageHolders.remove(holder)
            else -> return
        }
        holder.onUnhook()
    }

    internal fun getActivity() = activity

    internal fun getContext() = getActivity()?.applicationContext

    internal fun isActive(holder: MediaHolder): Boolean {
        when (holder.getType()) {
            MEDIA_TYPE_AUDIO -> return audioHolders.contains(holder)
            MEDIA_TYPE_VIDEO -> return videoHolders.contains(holder)
            MEDIA_TYPE_IMAGES -> return imageHolders.contains(holder)
            else -> return false
        }
    }

}