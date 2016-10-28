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
import java.util.*

/**
 * This is the class that stores the queue for the playback library
 */
internal object MusicQueue {
    var POSITION: Int = 0
        private set

    @Volatile private var QUEUE: MutableList<Song>? = ArrayList()

    internal fun getQueue(startAtPosition: Boolean): List<Song> {
        if (QUEUE != null) {
            if (startAtPosition) {
                return if (POSITION + 1 > QUEUE!!.lastIndex) emptyList() else QUEUE!!.subList(POSITION + 1, QUEUE!!.lastIndex)
            } else return QUEUE!!
        } else return emptyList()
    }

    internal fun getMutableQueue(): MutableList<Song>? {
        if (QUEUE != null) return QUEUE!! else return Collections.emptyList()
    }


    fun getCurrentSong(): Song? {
        if (QUEUE != null && QUEUE!!.size > 0) return QUEUE!![POSITION]
        return null
    }

    internal fun moveForward(by: Int) {
        Log.d(MusicQueue::class.java.simpleName, "moveForward($by)")
        POSITION += by
        if (POSITION >= QUEUE!!.size) POSITION = 0
    }

    internal fun moveBackward(by: Int) {
        POSITION -= by
        if (POSITION < 0) POSITION = QUEUE!!.lastIndex
    }

    internal fun set(songs: MutableList<Song>, position: Int) {
        Log.d(MusicQueue::class.java.simpleName, "set(${songs.size}), $position")
        QUEUE = songs
        POSITION = position
    }


}