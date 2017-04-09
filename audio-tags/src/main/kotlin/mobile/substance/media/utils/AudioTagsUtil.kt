/*
 * Copyright 2017 Substance Mobile
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

package mobile.substance.media.utils

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore

object AudioTagsUtil {

    fun notifyChange(context: Context, uri: Uri) = context.contentResolver.notifyChange(uri, null)

    /**
     * A method that inserts a new playlist into [com.android.providers.media.MediaProvider]
     *
     * @param context  A Context used to access the ContentResolver
     * @param name  The name of the new playlist
     * @return  The new playlist's row id, also the [mobile.substance.media.audio.local.objects.MediaStorePlaylist.id]
     */
    fun createPlaylist(context: Context, name: String): Long {
        val cursor = context.contentResolver.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, null, "${MediaStore.Audio.Playlists.NAME} = ?", arrayOf(name), null)
        if (cursor != null && cursor.moveToFirst()) {
            cursor.close()
            return 0
        } else return ContentUris.parseId(context.contentResolver.insert(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, ContentValues().apply {
                put(MediaStore.Audio.Playlists.NAME, name)
            }))
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

        var size = 0
        if (cursor.moveToFirst()) size = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.SIZE))
        cursor.close()

        val valuesArray = Array(songs.size, {
            ContentValues().apply {
                put(MediaStore.Audio.Playlists.Members.AUDIO_ID, songs[it])
                put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, it + size + 1)
            }
        })

        context.contentResolver.bulkInsert(MediaStore.Audio.Playlists.Members.getContentUri("external", id), valuesArray)
        return true
    }

    fun removeFromPlaylist(context: Context, songs: List<Long>, id: Long) {
        for (song in songs) {
            context.contentResolver.delete(MediaStore.Audio.Playlists.Members.getContentUri("external", id), "${MediaStore.Audio.Playlists.Members.AUDIO_ID} = ?", arrayOf(song.toString()))
        }
    }

    fun moveInPlaylist(context: Context, playlist: Long, fromPos: Int, toPos: Int): Boolean = MediaStore.Audio.Playlists.Members.moveItem(context.contentResolver, playlist, fromPos, toPos)

}
