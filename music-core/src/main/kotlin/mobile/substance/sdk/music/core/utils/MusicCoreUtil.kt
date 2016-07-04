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

package mobile.substance.sdk.music.core.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.util.TypedValue
import mobile.substance.sdk.music.core.R
import mobile.substance.sdk.music.core.objects.*
import java.io.File
import java.util.*

object MusicCoreUtil {

    /**
     * Convenience method that simplifies calling intents and such

     * @param cxt The context to start the activity from
     * *
     * @param url The url so start
     */
    fun startUrl(cxt: Context, url: String) {
        cxt.startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse(url)))
    }

    /**
     * Formats strings to match time. Is either hh:mm:ss or mm:ss

     * @param time The raw time in ms
     * *
     * @return The formatted string value
     */
    @SuppressLint("DefaultLocale")
    fun stringForTime(time: Long): String {
        val totalSeconds = time.toInt() / 1000
        val seconds = totalSeconds % 60
        val minutes = totalSeconds / 60 % 60
        val hours = totalSeconds / 3600
        return if (hours > 0) java.lang.String.format("%d:%02d:%02d", hours, minutes, seconds) else java.lang.String.format("%02d:%02d", minutes, seconds)
    }

    ///////////////////////////////////////////////////////////////////////////
    // Android Version Utils
    ///////////////////////////////////////////////////////////////////////////

    val isKitKat: Boolean
        get() = Build.VERSION.SDK_INT >= 19

    val isLollipop: Boolean
        get() = Build.VERSION.SDK_INT >= 21

    val isMarshmallow: Boolean
        get() = Build.VERSION.SDK_INT >= 23

    ///////////////////////////////////////////////////////////////////////////
    // Unit conversions
    ///////////////////////////////////////////////////////////////////////////

    fun dpToPx(context: Context, dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)
    }


    fun getFilePath(context: Context, uri: Uri): String? {
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                val docId : String= DocumentsContract.getDocumentId(uri)
                val split = (java.lang.String.valueOf(docId) as java.lang.String).split(":")
                val type = split[0]
                if (("primary" as java.lang.String).equalsIgnoreCase(uri.scheme)) {
                    return Environment.getExternalStorageDirectory().path + File.separator + split[1]
                }
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = (java.lang.String.valueOf(docId) as java.lang.String).split(":")
                val type = split[0]
                var contentUri: Uri? = null
                if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf<String>(split[1])
                return getDataColumn(context, contentUri!!, selection, selectionArgs)
            }
        } else if (("content" as java.lang.String).equalsIgnoreCase(uri.scheme)) {
            return getDataColumn(context, uri, null, null)
        } else if (("file" as java.lang.String).equalsIgnoreCase(uri.scheme)) {
            return uri.path
        }
        return null
    }

    private fun getDataColumn(context: Context, uri: Uri, selection: String?, selectionArgs: Array<String>?): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            if (cursor != null)
                cursor.close()
        }
        return null
    }

    fun findByMediaId(id: Long, vararg data: List<MediaObject>): MediaObject? {
        for (list in data)
            for (item in list)
                if (item.id == id) return item
        return null
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    ///////////////////////////////////////////////////////////////////////////
    // Methods for finding a media object by ID
    ///////////////////////////////////////////////////////////////////////////

    fun findSongById(id: Long, songs: List<Song>): Song? {
        for (song in songs) if (song.id == id) return song
        return null
    }

    fun findSongByUri(uri: Uri, songs: List<Song>): Song? {
        for (song in songs) if (song.uri === uri) return song
        return null
    }

    fun findAlbumById(id: Long, albums: List<Album>): Album? {
        for (album in albums) if (album.id == id) return album
        return null
    }

    fun findArtistById(id: Long, artists: List<Artist>): Artist? {
        for (artist in artists) if (artist.id == id) return artist
        return null
    }

    fun findPlaylistById(id: Long, playlists: List<Playlist>): Playlist? {
        for (playlist in playlists)
            if (playlist.id == id) return playlist
        return null
    }

    ///////////////////////////////////////////////////////////////////////////
    // Find X for Y
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

    fun findSongsForArtist(artist: Artist, input: List<Song>): List<Song> {
        val songs = ArrayList<Song>()
        for (song in input) {
            if (song.songArtistId == artist.id)
                songs.add(song)
        }
        return songs
    }

    @SuppressWarnings("unchecked")
    fun findSongsForArtistAsync(artist: Artist, songs: List<Song>, callback: QueryResult<List<Song>>) {
        QueryTask(callback).execute({ findSongsForArtist(artist, songs) })
    }

    fun findAlbumsForArtist(artist: Artist, input: List<Album>): List<Album> {
        val albums = ArrayList<Album>()
        for (album in input) {
            if (album.albumArtistName == artist.artistName)
                albums.add(album)
        }
        return albums
    }

    @SuppressWarnings("unchecked")
    fun findAlbumsForArtistAsync(artist: Artist, albums: List<Album>, callback: QueryResult<List<Album>>) {
        QueryTask(callback).execute({ findAlbumsForArtist(artist, albums) })
    }

    fun findSongsForAlbum(album: Album, input: List<Song>): List<Song> {
        val songs = ArrayList<Song>()
        for (song in input) {
            if (song.songAlbumId == album.id)
                songs.add(song)
        }
        return songs
    }

    @SuppressWarnings("unchecked")
    fun findSongsForAlbumAsync(album: Album, songs: List<Song>, callback: QueryResult<List<Song>>) {
        QueryTask(callback).execute({ findSongsForAlbum(album, songs) })
    }

    fun findArtistForAlbum(album: Album, artists: List<Artist>): Artist? {
        for (artist in artists) {
            if (artist.artistName == album.albumArtistName)
                return artist
        }
        return null
    }

    @SuppressWarnings("unchecked")
    fun findArtistForAlbumAsync(album: Album, artists: List<Artist>, callback: QueryResult<Artist?>) {
        QueryTask(callback).execute({ findArtistForAlbum(album, artists) })
    }

    @Throws(NullPointerException::class)
    fun findSongsForPlaylist(context: Context, playlist: Playlist, input: List<Song>): List<Song> {
        val songs = ArrayList<Song>()
        try {
            val playlistSongsCursor = context.contentResolver.query(MediaStore.Audio.Playlists.Members.getContentUri("external", playlist.id),
                    null, null, null,
                    MediaStore.Audio.Playlists.Members.PLAY_ORDER)
            val idColumn = playlistSongsCursor!!.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID)
            playlistSongsCursor.moveToFirst()
            do {
                val s = findSongById(playlistSongsCursor.getLong(idColumn), input)
                if (s != null) songs.add(s)
            } while (playlistSongsCursor.moveToNext())
            playlistSongsCursor.close()
            return songs
        } catch (e: IndexOutOfBoundsException) {
            return emptyList()
        }

    }

    @SuppressWarnings("unchecked")
    fun findSongsForPlaylistAsync(context: Context, playlist: Playlist, songs: List<Song>, callback: QueryResult<List<Song>>) {
        QueryTask(callback).execute({ findSongsForPlaylist(context, playlist, songs) })
    }

    @Throws(NullPointerException::class)
    fun findSongsForGenre(context: Context, genre: Genre, input: List<Song>): List<Song> {
        val songs = ArrayList<Song>()
        try {
            val genreSongsCursor = context.contentResolver.query(MediaStore.Audio.Genres.Members.getContentUri("external", genre.id),
                    null, null, null, null)
            val idColumn = genreSongsCursor!!.getColumnIndex(MediaStore.Audio.Genres.Members.AUDIO_ID)
            genreSongsCursor.moveToFirst()
            do {
                val s = findSongById(genreSongsCursor.getLong(idColumn), input)
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

    @SuppressWarnings("unchecked")
    fun findSongsForGenreAsync(context: Context, genre: Genre, songs: List<Song>, callback: QueryResult<List<Song>>) {
        QueryTask(callback).execute({ findSongsForGenre(context, genre, songs) })
    }

    ///////////////////////////////////////////////////////////////////////////
    // Search
    ///////////////////////////////////////////////////////////////////////////

    fun filterAlbums(query: String, albums: List<Album>, context: Context): SearchResult {
        val results = ArrayList<Album>()
        for (a in albums) {
            if (a.albumName!!.toLowerCase().contains(query.toLowerCase())) {
                if (!results.contains(a)) results.add(a)
            }

            if (a.albumArtistName!!.toLowerCase().contains(query.toLowerCase())) {
                if (!results.contains(a)) results.add(a)
            }
        }
        return SearchResult(context.getString(R.string.albums), results)
    }

    fun filterSongs(query: String, songs: List<Song>, context: Context): SearchResult {
        val results = ArrayList<Song>()
        for (s in songs) {
            if (s.songTitle!!.toLowerCase().contains(query.toLowerCase())) {
                if (!results.contains(s)) results.add(s)
            }

            if (s.songArtistName!!.toLowerCase().contains(query.toLowerCase())) {
                if (!results.contains(s)) results.add(s)
            }
        }
        return SearchResult(context.getString(R.string.songs), results)
    }

    fun filterArtists(query: String, artists: List<Artist>, context: Context): SearchResult {
        val results = ArrayList<Artist>()
        for (a in artists)
            if (a.artistName!!.toLowerCase().contains(query.toLowerCase()))
                if (!results.contains(a)) results.add(a)
        return SearchResult(context.getString(R.string.artists), results)
    }

    fun filterPlaylists(query: String, playlists: List<Playlist>, context: Context): SearchResult {
        val results = ArrayList<Playlist>()
        for (p in playlists) {
            if (p.playlistName!!.toLowerCase().contains(query.toLowerCase())) {
                if (!results.contains(p)) results.add(p)
            }
        }
        return SearchResult(context.getString(R.string.playlists), results)
    }

    fun filterGenres(query: String, genres: List<Genre>, context: Context): SearchResult {
        val results = ArrayList<Genre>()
        for (g in genres) {
            if (g.genreName!!.toLowerCase().contains(query.toLowerCase())) {
                if (!results.contains(g)) results.add(g)
            }
        }
        return SearchResult(context.getString(R.string.playlists), results)
    }

    fun search(query: String, data: List<MediaObject>, context: Context): List<SearchResult> {
        val albums = ArrayList<Album>()
        val songs = ArrayList<Song>()
        val artists = ArrayList<Artist>()
        val playlists = ArrayList<Playlist>()
        val genres = ArrayList<Genre>()

        data.forEach {
            if (it is Album) albums.add(it)
            if (it is Song) songs.add(it)
            if (it is Artist) artists.add(it)
            if (it is Playlist) playlists.add(it)
            if (it is Genre) genres.add(it)
        }

        val output = ArrayList<SearchResult>()
        filterAlbums(query, albums, context).addIfNotEmpty(output)
        filterSongs(query, songs, context).addIfNotEmpty(output)
        filterArtists(query, artists, context).addIfNotEmpty(output)
        filterPlaylists(query, playlists, context).addIfNotEmpty(output)
        filterGenres(query, genres, context).addIfNotEmpty(output)
        return Collections.unmodifiableList(output)
    }

}
