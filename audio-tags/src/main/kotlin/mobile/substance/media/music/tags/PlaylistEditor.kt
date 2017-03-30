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

package mobile.substance.media.music.tags

import android.content.Context
import android.util.Log

import java.util.ArrayList

import mobile.substance.media.core.music.objects.Playlist
import mobile.substance.media.core.music.objects.Song
import mobile.substance.media.utils.MusicTagsUtil

/**
 * Created by Julian Os on 04.05.2016.
 */
class PlaylistEditor(private val context: Context, playlist: Playlist) {
    var playlist: Playlist? = null
        private set
    private var name: String? = null
    private val songsToRemove = ArrayList<Song>()
    private val songsToAdd = ArrayList<Song>()
    private var delete = false
    private var positions: Pair<Int, Int>? = null


    init {
        this.playlist = playlist
    }

    fun setName(name: String): PlaylistEditor {
        this.name = name
        return this
    }

    fun remove(songs: List<Song>): PlaylistEditor {
        this.songsToRemove.addAll(songs)
        return this
    }

    fun remove(song: Song): PlaylistEditor {
        this.songsToRemove.add(song)
        return this
    }

    fun add(song: Song): PlaylistEditor {
        this.songsToAdd.add(song)
        return this
    }

    fun add(songs: List<Song>): PlaylistEditor {
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

        if (songsToAdd.isNotEmpty()) results.add(MusicTagsUtil.addToPlaylist(context, Array(songsToAdd.size, { songsToAdd[it].id }).asList(), playlist!!.id))
        if (songsToRemove.size > 0) MusicTagsUtil.removeFromPlaylist(context, Array(songsToRemove.size, { songsToRemove[it].id }).asList(), playlist!!.id)
        if (name != null) results.add(MusicTagsUtil.renamePlaylist(context, playlist!!.id, name!!))
        if (positions != null) results.add(MusicTagsUtil.moveInPlaylist(context, playlist!!.id, positions!!.first, positions!!.second))
        if (delete) MusicTagsUtil.deletePlaylist(context, playlist!!.id)

        Log.d(PlaylistEditor::class.java.simpleName, results.toString())

        return results
    }

    companion object {
        val MAX_PLAYLIST_ITEMS = 500
    }


}
