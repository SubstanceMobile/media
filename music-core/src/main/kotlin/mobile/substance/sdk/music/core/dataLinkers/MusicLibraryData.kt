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

    fun findSongById(id: Long) : Song? {
        for (song in getSongs()) if (song.id == id) return song
        return null
    }

    fun findSongByUri(uri: Uri): Song? {
        for (song in getSongs()) if (song.uri === uri) return song
        return null
    }

    fun findAlbumById(id: Long) : Album? {
        for (album in getAlbums()) if (album.id == id) return album
        return null
    }

    fun findArtistById(id: Long) : Artist? {
        for (artist in getArtists()) if (artist.id == id) return artist
        return null
    }

    fun findPlaylistById(id: Long) : Playlist? {
        for (playlist in getPlaylists()) if (playlist.id == id) return playlist
        return null
    }

    fun findGenreById(id: Long) : Genre? {
        for (genre in getGenres()) if (genre.id == id) return genre
        return null
    }

    ///////////////////////////////////////////////////////////////////////////
    // findXforY()
    // NOTE: These have default implementations. For convenience
    ///////////////////////////////////////////////////////////////////////////

    interface QueryResult<T> {
        fun onQueryResult(result: T)
    }

    class QueryTask<Result>(private val callback: QueryResult<Result>) : AsyncTask<() -> Result, Void, Result>() {

        @SafeVarargs
        override fun doInBackground(vararg params: () -> Result): Result? {
            try {
                return params[0].invoke()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return null
        }

        override fun onPostExecute(result: Result) {
            callback.onQueryResult(result)
        }
    }

    fun findSongsForArtist(artist: Artist): List<Song> {
        val songs = ArrayList<Song>()
        for (song in getSongs()) {
            if (song.songArtistId == artist.id)
                songs.add(song)
        }
        return songs
    }

    fun findSongsForArtistAsync(artist: Artist, callback: QueryResult<List<Song>>) {
        QueryTask(callback).execute({ findSongsForArtist(artist) })
    }

    fun findAlbumsForArtist(artist: Artist): List<Album> {
        val albums = ArrayList<Album>()
        for (album in getAlbums()) {
            if (album.albumArtistName == artist.artistName)
                albums.add(album)
        }
        return albums
    }

    fun findAlbumsForArtistAsync(artist: Artist, callback: QueryResult<List<Album>>) {
        QueryTask(callback).execute({ findAlbumsForArtist(artist) })
    }

    fun findSongsForAlbum(album: Album): List<Song> {
        val songs = ArrayList<Song>()
        for (song in getSongs()) {
            if (song.songAlbumId == album.id)
                songs.add(song)
        }
        return songs
    }

    fun findSongsForAlbumAsync(album: Album, callback: QueryResult<List<Song>>) {
        QueryTask(callback).execute({ findSongsForAlbum(album) })
    }

    fun findArtistForAlbum(album: Album): Artist? {
        for (artist in getArtists()) {
            if (artist.artistName == album.albumArtistName)
                return artist
        }
        return null
    }

    fun findArtistForAlbumAsync(album: Album, callback: QueryResult<Artist?>) {
        QueryTask(callback).execute({ findArtistForAlbum(album) })
    }

    @Throws(NullPointerException::class) fun findSongsForPlaylist(playlist: Playlist): List<Song> {
        val songs = ArrayList<Song>()
        try {
            val playlistSongsCursor = getContext().contentResolver.query(MediaStore.Audio.Playlists.Members.getContentUri("external", playlist.id),
                    null, null, null,
                    MediaStore.Audio.Playlists.Members.PLAY_ORDER)
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

    fun findSongsForPlaylistAsync(playlist: Playlist, callback: QueryResult<List<Song>>) {
        QueryTask(callback).execute({ findSongsForPlaylist(playlist) })
    }

    @Throws(NullPointerException::class) fun findSongsForGenre(genre: Genre): List<Song> {
        val songs = ArrayList<Song>()
        try {
            val genreSongsCursor = getContext().contentResolver.query(MediaStore.Audio.Genres.Members.getContentUri("external", genre.id),
                    null, null, null, null)
            val idColumn = genreSongsCursor!!.getColumnIndex(MediaStore.Audio.Genres.Members.AUDIO_ID)
            genreSongsCursor.moveToFirst()
            do {
                val s = findSongById(genreSongsCursor.getLong(idColumn))
                if (s != null) {
                    songs.add(s)
                    Log.d("GENRE: " + genre.genreName, "Found Song: " + s.songTitle)
                }
            } while (genreSongsCursor.moveToNext())
            genreSongsCursor.close()
            return songs
        } catch (e: IndexOutOfBoundsException) {
            return emptyList()
        }

    }

    fun findSongsForGenreAsync(genre: Genre, callback: QueryResult<List<Song>>) {
        QueryTask(callback).execute({ findSongsForGenre(genre) })
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