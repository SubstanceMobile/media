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
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.widget.Toast
import mobile.substance.media.audio.local.MediaStoreAudioHolder
import mobile.substance.media.audio.local.objects.MediaStoreAlbum
import mobile.substance.media.audio.local.objects.MediaStoreArtist
import mobile.substance.media.audio.local.objects.MediaStoreGenre
import mobile.substance.media.audio.local.objects.MediaStorePlaylist
import mobile.substance.media.audio.playback.PlaybackRemote
import mobile.substance.media.core.audio.Album
import mobile.substance.media.core.audio.AudioData
import mobile.substance.media.core.audio.AudioHolder
import mobile.substance.media.core.audio.Song
import mobile.substance.media.extensions.mainThread
import mobile.substance.media.local.core.MediaStoreAttributes
import kotlin.concurrent.thread

object AudioLocalPlaybackUtil {

    fun playFromSearch(query: String, extras: Bundle) {
        var songs: List<Song>? = null
        try {
            when (extras.get(MediaStore.EXTRA_MEDIA_FOCUS)) {
                MediaStore.Audio.Media.ENTRY_CONTENT_TYPE -> songs = AudioData.filterSongs(extras.getString(MediaStore.EXTRA_MEDIA_TITLE))
                MediaStore.Audio.Albums.ENTRY_CONTENT_TYPE -> songs = MediaStoreAudioHolder.findSongsForAlbum(MediaStoreAudioHolder.filterAlbums(extras.getString(MediaStore.EXTRA_MEDIA_ALBUM)).first() as MediaStoreAlbum)
                MediaStore.Audio.Artists.ENTRY_CONTENT_TYPE -> songs = MediaStoreAudioHolder.findSongsForArtist(MediaStoreAudioHolder.filterArtists(extras.getString(MediaStore.EXTRA_MEDIA_ARTIST)).first() as MediaStoreArtist)
                MediaStore.Audio.Playlists.ENTRY_CONTENT_TYPE -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) songs = MediaStoreAudioHolder.findSongsForPlaylist(MediaStoreAudioHolder.filterPlaylists(extras.getString(MediaStore.EXTRA_MEDIA_PLAYLIST)).first() as MediaStorePlaylist)
                }
                MediaStore.Audio.Genres.ENTRY_CONTENT_TYPE -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) songs = MediaStoreAudioHolder.findSongsForGenre(MediaStoreAudioHolder.filterGenres(extras.getString(MediaStore.EXTRA_MEDIA_GENRE)).first() as MediaStoreGenre)
                }
                else -> songs = AudioData.filterSongs(query)
            }
        } catch (e: KotlinNullPointerException) {
            e.printStackTrace()
        } finally {
            if (songs != null && songs.isNotEmpty()) PlaybackRemote.play(songs, 0)
        }
    }

    fun playFromUri(uri: Uri?, extras: Bundle?, context: Context) {
        if (uri != null) thread {
            var song: Song? = null
            when (uri.scheme) {
                "content" -> song = MediaStoreAudioHolder.findSongById(ContentUris.parseId(uri))
                "file" -> song = MediaStoreAudioHolder.findSongById(CoreUtil.retrieveMediaId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, context, uri.path))
                "http", "https" -> song = Song.from(uri)
            }
            if (song != null) mainThread {
                PlaybackRemote.play(song!!)
            }
        }
    }

    fun playFromMediaId(mediaId: String?, extras: Bundle?) {
        val song = MediaStoreAudioHolder.findSongById(mediaId?.toLong() ?: 0L)
        if (song != null) PlaybackRemote.play(song)
    }

}