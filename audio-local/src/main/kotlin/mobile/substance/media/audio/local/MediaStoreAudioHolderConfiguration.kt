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

package mobile.substance.media.audio.local

import mobile.substance.media.core.audio.AudioType

/**
 * A class that tells [MediaStoreAudioHolder] which loaders are needed for the Activity which it is being initialized on
 */
class MediaStoreAudioHolderConfiguration {
    private val config = arrayListOf<@AudioType Long>()

    fun clear() = config.clear()

    fun load(@AudioType type: Long) : MediaStoreAudioHolderConfiguration {
        if (!contains(type)) config += type
        return this
    }

    fun contains(@AudioType type: Long) = config.contains(type)

}