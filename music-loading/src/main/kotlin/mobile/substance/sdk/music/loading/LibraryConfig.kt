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

import java.util.*

/**
 * Created by Julian Os on 05.05.2016.
 */
class LibraryConfig {
    internal val config = ArrayList<LibraryData>()
    internal var playbackHook = false
    internal var tagsHook = false

    fun put(item: LibraryData): LibraryConfig {
        config.add(item)
        return this
    }

    fun hookPlayback(): LibraryConfig {
        playbackHook = true
        return this
    }

    fun hookTags(): LibraryConfig {
        tagsHook = true
        return this
    }

}
