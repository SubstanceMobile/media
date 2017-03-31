package mobile.substance.media.core

import android.app.Activity

abstract class MediaHolder {

    fun getActivity() = MediaCore.getActivity()

    fun isActivityStarted() = getActivity() != null

    fun isActive() = MediaCore.isActive(this)

    @MediaCore.MediaType
    abstract fun getType(): Long

    abstract fun onStartActivity(activity: Activity)

    abstract fun onStopActivity(activity: Activity)

    open fun onHook(isActivityStarted: Boolean) = Unit

    open fun onUnhook() = Unit

    abstract fun onInvalidateHolder()

}