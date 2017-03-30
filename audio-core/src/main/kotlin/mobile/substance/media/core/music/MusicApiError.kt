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

package mobile.substance.media.core.music

import android.util.Log

/**
 * Do not catch this error
 */
class MusicApiError(detail: String = "Common mistakes may include not registering your own library hooks or not properly registering components like services") :
        Error("This error indicates incorrect use of the Music API. $detail") {
    init { Log.w("Notice", "Please do not catch MusicApiError anywhere. These will not be called unless you are doing something wrong. Thank you") }
}

/**
 * This is called when the SDK creates an error. Feel free to catch this
 */
class MusicApiInternalError(code: Int = -1) : Error("It looks like the library threw an error that isn't caused by you. Please open an issue with this code: $code")

