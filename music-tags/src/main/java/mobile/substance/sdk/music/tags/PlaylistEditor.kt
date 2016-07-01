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

package mobile.substance.sdk.music.tags

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.util.Pair
import android.util.Log
import mobile.substance.sdk.music.core.objects.Playlist
import mobile.substance.sdk.music.core.objects.Song
import java.util.*

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

    fun commit(): Playlist {
        val results = ArrayList<TagResult>()

        if (songsToAdd.size > 0) results.add(addToPlaylist(songsToAdd))
        if (songsToRemove.size > 0) results.add(removeFromPlaylist(songsToRemove))
        if (name != null) results.add(renamePlaylist(name!!))
        if (delete) results.add(deletePlaylist())
        if (positions != null) results.add(movePlaylistItem(positions!!.first, positions!!.second))

        Log.d(PlaylistEditor::class.java.simpleName, results.toString())

        return playlist!!
    }

    private fun addToPlaylist(songs: List<Song>): TagResult {
        val s = arrayOf("max(play_order)")
        val uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlist!!.id)
        try {
            val query = context.contentResolver.query(uri, s, null, null, null)
            if (query != null) {
                try {
                    if (query.moveToFirst()) {
                        val count = query.getInt(query.getColumnIndex(MediaStore.Audio.Playlists.Members._COUNT)) + 1
                        query.close()
                        context.contentResolver.bulkInsert(uri, newPlaylistMemberValues(songs, count))
                        return TagResult.SUCCESS
                    } else
                        return TagResult.ERR_ADD_FAILED
                } catch (th: Throwable) {
                    th.printStackTrace()
                    return TagResult.ERR_ADD_FAILED
                }

            } else
                return TagResult.ERR_ADD_FAILED
        } catch (th2: Throwable) {
            th2.printStackTrace()
            return TagResult.ERR_ADD_FAILED
        }

    }

    private fun deletePlaylist(): TagResult {
        try {
            context.contentResolver.delete(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, "_id IN (" + playlist!!.id + ")", null)
            playlist = null
            return TagResult.SUCCESS
        } catch (ignored: Exception) {
            return TagResult.ERR_DELETE_FAILED
        }

    }

    private fun newPlaylistMemberValues(songs: List<Song>, count: Int): Array<ContentValues?> {
        val cv = arrayOfNulls<ContentValues>(songs.size)
        for (i in songs.indices) {
            cv[i] = ContentValues()
            cv[i]!!.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, count + i + 1)
            cv[i]!!.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, songs[i].id)
        }
        return cv
    }

    private fun movePlaylistItem(fromPos: Int, toPos: Int): TagResult {
        try {
            MediaStore.Audio.Playlists.Members.moveItem(context.contentResolver, playlist!!.id, fromPos, toPos)
            return TagResult.SUCCESS
        } catch (e: Exception) {
            e.printStackTrace()
            return TagResult.ERR_MOVE_FAILED
        }

    }

    private fun removeFromPlaylist(songs: List<Song>): TagResult {
        try {
            for (song in songs) {
                context.contentResolver.delete(MediaStore.Audio.Playlists.Members.getContentUri("external", playlist!!.id), "audio_id =?", arrayOf(song.id.toString()))
            }
            return TagResult.SUCCESS
        } catch (e: Exception) {
            e.printStackTrace()
            return TagResult.ERR_REMOVE_FAILED
        }

    }


    private fun renamePlaylist(newName: String): TagResult {
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Audio.Playlists.NAME, newName)
        try {
            context.contentResolver.update(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, contentValues, "_id=?", arrayOf(playlist!!.id.toString()))
            context.contentResolver.notifyChange(Uri.parse("content://media"), null)
            playlist!!.playlistName = newName
            return TagResult.SUCCESS
        } catch (e: Exception) {
            e.printStackTrace()
            return TagResult.ERR_NAME_UNAVAILABLE
        }

    }

    companion object {
        val MAX_PLAYLIST_ITEMS = 500
    }


}
