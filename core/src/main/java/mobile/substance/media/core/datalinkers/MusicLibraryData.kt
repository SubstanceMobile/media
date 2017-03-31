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

package mobile.substance.sdk.music.core.dataLinkers

import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import android.provider.MediaStore
import android.util.Log
import mobile.substance.sdk.music.core.objects.*
import java.util.*

/**
 * This class is used in order to get the data for the entire music library. This will be automatically used by the music-loading library.
 */
interface MusicLibraryData {

    ///////////////////////////////////////////////////////////////////////////
    // Main lists
    ///////////////////////////////////////////////////////////////////////////

    fun getSongs() : MutableList<Song>

    fun getAlbums() : MutableList<Album>

    fun getArtists() : MutableList<Artist>

    fun getPlaylists() : MutableList<Playlist>

    fun getGenres() : MutableList<Genre>

    fun getContext() : Context

    ///////////////////////////////////////////////////////////////////////////
    // findXByID()
    // NOTE: These have default implementations. For convenience
    /////////////////////////////////////////////////////////////////////////

    fun findSongById(id: Long): Song? = getSongs().firstOrNull { it.id == id }

    fun findSongByUri(uri: Uri): Song? = getSongs().firstOrNull { it.uri === uri }

    fun findAlbumById(id: Long): Album? = getAlbums().firstOrNull { it.id == id }

    fun findArtistById(id: Long): Artist? = getArtists().firstOrNull { it.id == id }

    fun findPlaylistById(id: Long): Playlist? = getPlaylists().firstOrNull { it.id == id }

    fun findGenreById(id: Long): Genre? = getGenres().firstOrNull { it.id == id }

    ///////////////////////////////////////////////////////////////////////////
    // findXforY()
    // NOTE: These have default implementations. For convenience
    ///////////////////////////////////////////////////////////////////////////

    fun findSongsForArtist(artist: Artist): List<Song> = getSongs().filter { it.songArtistId == artist.id }

    fun findAlbumsForArtist(artist: Artist): List<Album> = getAlbums().filter { it.albumArtistName == artist.artistName }

    fun findSongsForAlbum(album: Album): List<Song> {
        val songs = getSongs().filter { it.songAlbumId == album.id }
        return songs.sortedBy(Song::songTrackNumber)
    }

    fun findArtistForAlbum(album: Album): Artist? = getArtists().firstOrNull { it.artistName == album.albumArtistName }

    fun findSongsForPlaylist(playlist: Playlist): List<Song> {
        val songs = ArrayList<Song>()
        try {
            val playlistSongsCursor = getContext().contentResolver.query(MediaStore.Audio.Playlists.Members.getContentUri("external", playlist.id),
                    null, null, null, MediaStore.Audio.Playlists.Members.PLAY_ORDER)
            val idColumn = playlistSongsCursor!!.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID)
            playlistSongsCursor.moveToFirst()
            do {
                val s = findSongById(playlistSongsCursor.getLong(idColumn))
                if (s != null) songs.add(s)
            } while (playlistSongsCursor.moveToNext())
            playlistSongsCursor.close()
            return songs
        } catch (e: IndexOutOfBoundsException) {
            return emptyList()
        }
    }

    fun findSongsForGenre(genre: Genre): List<Song> {
        val songs = ArrayList<Song>()
        try {
            val genreSongsCursor = getContext().contentResolver.query(MediaStore.Audio.Genres.Members.getContentUri("external", genre.id),
                    null, null, null, MediaStore.Audio.Genres.Members.DEFAULT_SORT_ORDER)
            val idColumn = genreSongsCursor!!.getColumnIndex(MediaStore.Audio.Genres.Members.AUDIO_ID)
            genreSongsCursor.moveToFirst()
            do {
                val s = findSongById(genreSongsCursor.getLong(idColumn))
                if (s != null) songs.add(s)
            } while (genreSongsCursor.moveToNext())
            genreSongsCursor.close()
            return songs
        } catch (e: IndexOutOfBoundsException) {
            return emptyList()
        }

    }

    ///////////////////////////////////////////////////////////////////////////
    // Other methods
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Retrieves the items that include a dateAdded property, ordered by descending date
     */
    fun getLastAddedObjects(): List<MediaObject> {
        val objects = ArrayList<MediaObject>()
        objects.addAll(getSongs())
        objects.addAll(getPlaylists())
        return objects.sortedByDescending {
            if (it is Song) it.dateAdded else (it as Playlist).dateAdded
        }
    }

}