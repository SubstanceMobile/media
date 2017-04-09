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

package mobile.substance.media.audio.tags

import android.content.Context
import android.util.Log
import mobile.substance.media.audio.local.objects.MediaStorePlaylist
import mobile.substance.media.audio.local.objects.MediaStoreSong

import java.util.ArrayList

import mobile.substance.media.utils.AudioTagsUtil

class PlaylistEditor(private val context: Context, playlist: MediaStorePlaylist) {
    var playlist: MediaStorePlaylist? = null
        private set
    private var name: String? = null
    private val songsToRemove = ArrayList<MediaStoreSong>()
    private val songsToAdd = ArrayList<MediaStoreSong>()
    private var delete = false
    private var positions: Pair<Int, Int>? = null


    init {
        this.playlist = playlist
    }

    fun setName(name: String): PlaylistEditor {
        this.name = name
        return this
    }

    fun remove(songs: List<MediaStoreSong>): PlaylistEditor {
        this.songsToRemove.addAll(songs)
        return this
    }

    fun remove(song: MediaStoreSong): PlaylistEditor {
        this.songsToRemove.add(song)
        return this
    }

    fun add(song: MediaStoreSong): PlaylistEditor {
        this.songsToAdd.add(song)
        return this
    }

    fun add(songs: List<MediaStoreSong>): PlaylistEditor {
        this.songsToAdd.addAll(songs)
        return this
    }

    fun moveSong(fromPos: Int, toPos: Int): PlaylistEditor {
        this.positions = Pair(fromPos, toPos)
        return this
    }

    fun delete(): PlaylistEditor {
        this.delete = true
        return this
    }

    fun commit(): List<Boolean> {
        val results = ArrayList<Boolean>()

        if (songsToAdd.isNotEmpty()) results.add(AudioTagsUtil.addToPlaylist(context, Array(songsToAdd.size, { songsToAdd[it].id }).asList(), playlist!!.id))
        if (songsToRemove.size > 0) AudioTagsUtil.removeFromPlaylist(context, Array(songsToRemove.size, { songsToRemove[it].id }).asList(), playlist!!.id)
        if (name != null) results.add(AudioTagsUtil.renamePlaylist(context, playlist!!.id, name!!))
        if (positions != null) results.add(AudioTagsUtil.moveInPlaylist(context, playlist!!.id, positions!!.first, positions!!.second))
        if (delete) AudioTagsUtil.deletePlaylist(context, playlist!!.id)

        Log.d(PlaylistEditor::class.java.simpleName, results.toString())

        return results
    }

    companion object {
        val MAX_PLAYLIST_ITEMS = 500
    }


}
