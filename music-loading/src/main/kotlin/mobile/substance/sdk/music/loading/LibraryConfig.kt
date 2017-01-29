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

package mobile.substance.sdk.music.loading

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle

class LibraryConfig {

    ///////////////////////////////////////////////////////////////////////////
    // Main configuration
    ///////////////////////////////////////////////////////////////////////////

    private val config = arrayListOf<MusicType>()

    fun load(vararg items: MusicType) : LibraryConfig {
        config += items
        return this
    }

    fun contains(item: MusicType) = config.contains(item)

    ///////////////////////////////////////////////////////////////////////////
    // Optional configuration objects
    ///////////////////////////////////////////////////////////////////////////

    internal var hookData = true

    fun doNotHookData(): LibraryConfig {
        hookData = false
        return this
    }

    fun hookIntoActivityLifecycle(application: Application) : LibraryConfig {
        // TODO: Integrate into lifecycle
        return this
    }

    fun hookIntoActivityLifecycle(activity: Activity) = hookIntoActivityLifecycle(activity.application)
}