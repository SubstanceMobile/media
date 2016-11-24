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

package mobile.substance.sdk.music.playback.service

import android.util.Log
import mobile.substance.sdk.music.core.objects.Song
import mobile.substance.sdk.music.playback.PlaybackRemote
import java.util.*

/**
 * This is the class that stores the queue for the playback library
 */
internal object MusicQueue {
    var POSITION: Int = 0
        private set

    @Volatile private var QUEUE: MutableList<Song> = ArrayList()

    fun getQueue(startAtPosition: Boolean = false): MutableList<Song> {
        if (startAtPosition) {
            return if (POSITION + 1 > QUEUE.lastIndex) ArrayList() else QUEUE.subList(POSITION + 1, QUEUE.size)
        } else return QUEUE
    }

    fun getCurrentSong(): Song? = get(POSITION)

    fun get(position: Int): Song? {
        if (QUEUE.isNotEmpty()) {
            return if (position < 0) QUEUE[QUEUE.lastIndex] else if (position > QUEUE.lastIndex) QUEUE[0] else QUEUE[position]
        }
        return null
    }

    fun moveForward(by: Int) {
        POSITION += by
        if (POSITION >= QUEUE.size) POSITION = 0

        notifyChanged()
    }

    fun moveBackward(by: Int) {
        POSITION -= by
        if (POSITION < 0) POSITION = QUEUE.lastIndex

        notifyChanged()
    }

    fun set(songs: MutableList<Song>, position: Int) {
        QUEUE = songs
        POSITION = position

        notifyChanged()
    }

    fun notifyChanged() = PlaybackRemote.delegate { callback { onQueueChanged(getQueue()) } }

}