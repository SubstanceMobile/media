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
import android.provider.MediaStore
import mobile.substance.media.audio.local.objects.MediaStoreAlbum
import mobile.substance.media.audio.local.objects.MediaStoreArtist
import mobile.substance.media.audio.local.objects.MediaStoreGenre
import mobile.substance.media.audio.local.objects.MediaStorePlaylist
import mobile.substance.media.audio.playback.PlaybackRemote
import mobile.substance.media.core.audio.Song
import mobile.substance.media.extensions.mainThread
import mobile.substance.media.options.AudioLocalOptions
import kotlin.concurrent.thread

object AudioLocalPlaybackUtil {

    fun playFromSearch(query: String, extras: Bundle) {
        var songs: List<Song>? = null
        try {
            when (extras.get(MediaStore.EXTRA_MEDIA_FOCUS)) {
                MediaStore.Audio.Media.ENTRY_CONTENT_TYPE -> songs = AudioLocalOptions.localAudioHolder?.filterSongs(extras.getString(MediaStore.EXTRA_MEDIA_TITLE))
                MediaStore.Audio.Albums.ENTRY_CONTENT_TYPE -> {
                    val matchingAlbums = AudioLocalOptions.localAudioHolder?.filterAlbums(extras.getString(MediaStore.EXTRA_MEDIA_ALBUM))
                    songs = matchingAlbums?.first()?.getSongs()
                }
                MediaStore.Audio.Artists.ENTRY_CONTENT_TYPE -> {
                    val matchingArtists = AudioLocalOptions.localAudioHolder?.filterArtists(extras.getString(MediaStore.EXTRA_MEDIA_ARTIST))
                    songs = matchingArtists?.first()?.getSongs()
                }
                MediaStore.Audio.Playlists.ENTRY_CONTENT_TYPE -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        val matchingPlaylists = AudioLocalOptions.localAudioHolder?.filterPlaylists(extras.getString(MediaStore.EXTRA_MEDIA_PLAYLIST))
                        songs = matchingPlaylists?.first()?.getSongs()
                    }
                }
                MediaStore.Audio.Genres.ENTRY_CONTENT_TYPE -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        val matchingGenres = AudioLocalOptions.localAudioHolder?.filterGenres(extras.getString(MediaStore.EXTRA_MEDIA_GENRE))
                        songs = matchingGenres?.first()?.getSongs()
                    }
                }
                else -> songs = AudioLocalOptions.localAudioHolder?.filterSongs(query)
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
                "content" -> song = AudioLocalOptions.localAudioHolder?.findSongById(ContentUris.parseId(uri))
                "file" -> song = AudioLocalOptions.localAudioHolder?.findSongById(CoreUtil.retrieveMediaId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, context, uri.path))
                "http", "https" -> song = Song.from(uri)
            }
            if (song != null) mainThread {
                PlaybackRemote.play(song!!)
            }
        }
    }

    fun playFromMediaId(mediaId: String?, extras: Bundle?) {
        val song = AudioLocalOptions.localAudioHolder?.findSongById(mediaId?.toLong() ?: 0L)
        if (song != null) PlaybackRemote.play(song)
    }

}