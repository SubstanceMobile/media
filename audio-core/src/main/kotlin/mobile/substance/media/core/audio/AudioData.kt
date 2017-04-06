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

package mobile.substance.media.core.audio

import android.app.Activity
import mobile.substance.media.core.MediaCore

object AudioData : AudioHolder() {

    override fun getType(): Long {
        return MediaCore.MEDIA_TYPE_AUDIO
    }

    override fun onStartActivity(activity: Activity) {

    }

    override fun onStopActivity(activity: Activity) {

    }

    override fun onInvalidateHolder() {

    }

    override fun getSongs(): List<Song> {
        val songs = ArrayList<Song>()
        MediaCore.getHoldersOfType(getType()).forEach {
            songs.addAll((it as AudioHolder).getSongs())
        }
        return songs
    }

    override fun getAlbums(): List<Album> {
        val albums = ArrayList<Album>()
        MediaCore.getHoldersOfType(getType()).forEach {
            albums.addAll((it as AudioHolder).getAlbums())
        }
        return albums
    }

    override fun getArtists(): List<Artist> {
        val artists = ArrayList<Artist>()
        MediaCore.getHoldersOfType(getType()).forEach {
            artists.addAll((it as AudioHolder).getArtists())
        }
        return artists
    }

    override fun getPlaylists(): List<Playlist> {
        val playlists = ArrayList<Playlist>()
        MediaCore.getHoldersOfType(getType()).forEach {
            playlists.addAll((it as AudioHolder).getPlaylists())
        }
        return playlists
    }

    override fun getGenres(): List<Genre> {
        val genres = ArrayList<Genre>()
        MediaCore.getHoldersOfType(getType()).forEach {
            genres.addAll((it as AudioHolder).getGenres())
        }
        return genres
    }


}