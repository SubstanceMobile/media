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
        this.activity = null
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