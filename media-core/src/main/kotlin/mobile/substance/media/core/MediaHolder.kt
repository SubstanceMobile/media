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

abstract class MediaHolder {
    var isActive
        set(value) = if (value) Media.activate(this) else Media.deactivate(this)
        get() = Media.isActive(this)

    fun getActivity() = Media.getActivity()

    fun isActivityStarted() = getActivity() != null

    abstract fun onStartActivity(activity: Activity)

    abstract fun onStopActivity(activity: Activity)

    open fun onHook(isActivityStarted: Boolean) = Unit

    open fun onUnhook() = Unit

    abstract fun onInvalidateHolder()

}