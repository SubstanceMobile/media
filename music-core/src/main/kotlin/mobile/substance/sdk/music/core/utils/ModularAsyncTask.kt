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

package mobile.substance.sdk.music.core.utils

import android.os.AsyncTask


/**
 * Created by Julian on 18.06.16.
 */
class ModularAsyncTask<T> @JvmOverloads constructor(callback: TaskCallback<T>, printException: Boolean = true) : AsyncTask<() -> T, Void, T>() {
    private val callback = callback
    private val printException = printException

    override fun onPreExecute() {
        callback.onTaskStart()
    }

    override fun onPostExecute(result: T) {
        callback.onTaskResult(result)
    }

    override fun doInBackground(vararg p0: (() -> T)?): T {
        try {
            return p0[0]!!.invoke()
        } catch(e: Exception) {
            if(printException) e.printStackTrace()
            callback.onTaskFailed(e)
            return null as T
        }
    }

    interface TaskCallback<T> {

        fun onTaskStart()

        fun onTaskFailed(e: Exception)

        fun onTaskResult(result: T)

    }

}