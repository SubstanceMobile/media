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

package mobile.substance.media.audio.playback.service

import android.support.v4.media.session.PlaybackStateCompat
import mobile.substance.media.core.audio.Song
import mobile.substance.media.audio.playback.PlaybackRemote
import java.util.*
import kotlin.collections.ArrayList

/**
 * This is the class that stores the queue for the playback library
 */
internal object AudioQueue {
    var POSITION: Int = 0
        private set

    @Volatile private var QUEUE_PRIMARY: MutableList<Song> = ArrayList()
    @Volatile private var QUEUE_SECONDARY: MutableList<Song> = ArrayList()

    var isSecondaryActive = false
        set(value) {
            field = value
            notifyChanged()
        }

    private fun getActiveQueue(): MutableList<Song> = if (isSecondaryActive) QUEUE_SECONDARY else QUEUE_PRIMARY

    fun size() = getActiveQueue().size
    fun isLastPosition() = POSITION == getActiveQueue().lastIndex
    fun isFirstPosition() = POSITION == 0

    fun getQueue(startAtPosition: Boolean = false): MutableList<Song> {
        if (startAtPosition) {
            return if (POSITION + 1 > getActiveQueue().lastIndex) ArrayList() else getActiveQueue().subList(POSITION + 1, getActiveQueue().size)
        } else return getActiveQueue()
    }

    fun getCurrentSong(): Song? = get(POSITION)

    fun get(position: Int): Song? {
        if (QUEUE_PRIMARY.isNotEmpty()) {
            return if (position < 0) getActiveQueue()[getActiveQueue().lastIndex] else if (position > getActiveQueue().lastIndex) getActiveQueue()[0] else getActiveQueue()[position]
        }
        return null
    }

    fun moveForward(by: Int) {
        POSITION += by
        if (POSITION >= getActiveQueue().size) {
            POSITION = if (PlaybackRemote.getRepeatMode() == PlaybackStateCompat.REPEAT_MODE_ALL) 0 else getActiveQueue().lastIndex
        }

        notifyChanged()
    }

    fun moveBackward(by: Int) {
        POSITION -= by
        if (POSITION < 0) POSITION = getActiveQueue().lastIndex

        notifyChanged()
    }

    fun set(songs: MutableList<Song>, position: Int) {
        QUEUE_PRIMARY = songs
        POSITION = position

        if (isSecondaryActive) PlaybackRemote.useShuffledQueue(false) else notifyChanged(true)
    }

    fun initSecondaryQueue() {
        QUEUE_SECONDARY.clear()
        QUEUE_SECONDARY.addAll(QUEUE_PRIMARY)
        Collections.shuffle(QUEUE_SECONDARY)
    }

    fun notifyChanged(fullSwap: Boolean = false, isInternalChange: Boolean = false) = PlaybackRemote.delegate {
        if (fullSwap) control()?.transportControls?.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_NONE)
        engine.onQueueChanged()
        if (!isInternalChange) callback { onQueueChanged(getQueue()) }
    }

}