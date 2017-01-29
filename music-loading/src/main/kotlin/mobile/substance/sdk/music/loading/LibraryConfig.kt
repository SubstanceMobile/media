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
        for (item in items) config.add(item)
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
        application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks{

            override fun onActivityStarted(p0: Activity?) {
                try {
                    Library.registerMediaStoreListeners()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onActivityStopped(p0: Activity?) {
                try {
                    Library.unregisterMediaStoreListeners()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onActivityDestroyed(p0: Activity?) {
                //Do nothing
            }

            override fun onActivitySaveInstanceState(p0: Activity?, p1: Bundle?) {
                //Do nothing
            }

            override fun onActivityCreated(p0: Activity?, p1: Bundle?) {
                //Do nothing
            }

            override fun onActivityResumed(p0: Activity?) {
                //Do nothing
            }

            override fun onActivityPaused(p0: Activity?) {
                //Do nothing
            }

        })
        return this
    }

    fun hookIntoActivityLifecycle(activity: Activity) = hookIntoActivityLifecycle(activity.application)

    fun apply(context: Context) = Library.init(context, this)
}