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
import mobile.substance.sdk.music.core.objects.Song

/**
 * Created by julian on 09/10/2016.
 */
object TagUtils {

    fun notifyChange(context: Context, uri: Uri) = context.contentResolver.notifyChange(uri, null)

    fun createPlaylist(context: Context, name: String): Boolean {
        val cursor = context.contentResolver.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, null, "${MediaStore.Audio.Playlists.NAME} = ?", arrayOf(name), null)
        if (cursor != null && cursor.moveToFirst()) {
            cursor.close()
            return false
        } else {
            val cv = ContentValues()
            cv.put(MediaStore.Audio.Playlists.NAME, name)
            context.contentResolver.insert(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, cv)
            return true
        }
    }

    fun deletePlaylist(context: Context, id: Long) = context.contentResolver.delete(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, "${MediaStore.Audio.Playlists._ID} = ?", arrayOf(id.toString()))

    fun renamePlaylist(context: Context, id: Long, newName: String): Boolean {
        val cursor = context.contentResolver.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, null, "${MediaStore.Audio.Playlists._ID} = ?", arrayOf(id.toString()), null)
        if (cursor != null && cursor.moveToFirst()) {
            cursor.close()
            val cv = ContentValues()
            cv.put(MediaStore.Audio.Playlists.NAME, newName)
            context.contentResolver.update(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, cv, "${MediaStore.Audio.Playlists._ID} = ?", arrayOf(id.toString()))
            return true
        } else return false
    }

    fun addToPlaylist(context: Context, songs: List<Long>, id: Long): Boolean {
        val cursor = context.contentResolver.query(MediaStore.Audio.Playlists.Members.getContentUri("external", id), null, null, null, null) ?: return false

        var count = 0
        if (cursor.moveToFirst()) count = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members._COUNT))
        cursor.close()

        val valuesArray = Array(songs.size, {
            val cv = ContentValues()
            cv.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, songs[it])
            cv.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, it + count + 1)
            cv
        })

        context.contentResolver.bulkInsert(MediaStore.Audio.Playlists.Members.getContentUri("external", id), valuesArray)
        return true
    }

    fun removeFromPlaylist(context: Context, songs: List<Long>, id: Long) {
        for (song in songs)
            context.contentResolver.delete(MediaStore.Audio.Playlists.Members.getContentUri("external", id), "${MediaStore.Audio.Playlists.Members.AUDIO_ID} = ?", arrayOf(song.toString()))
    }

    fun moveInPlaylist(context: Context, playlist: Long, fromPos: Int, toPos: Int): Boolean = MediaStore.Audio.Playlists.Members.moveItem(context.contentResolver, playlist, fromPos, toPos)

}