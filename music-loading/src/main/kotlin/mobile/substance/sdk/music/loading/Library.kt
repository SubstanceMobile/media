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

package mobile.substance.sdk.music.loading

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import mobile.substance.sdk.music.core.objects.*
import mobile.substance.sdk.music.loading.tasks.*
import java.util.*

@SuppressWarnings("unused")
object Library {
    @Volatile private var context: Context? = null
    ///////////////////////////////////////////////////////////////////////////
    // Getters
    ///////////////////////////////////////////////////////////////////////////

    @Volatile var songs: List<Song> = ArrayList()
        private set
    @Volatile var albums: List<Album> = ArrayList()
        private set
    @Volatile var playlists: List<Playlist> = ArrayList()
        private set
    @Volatile var artists: List<Artist> = ArrayList()
        private set
    @Volatile var genres: List<Genre> = ArrayList()
        private set

    @Volatile private var LOADER_SONGS: Loader<Song>? = null
    @Volatile private var LOADER_ALBUMS: Loader<Album>? = null
    @Volatile private var LOADER_ARTISTS: Loader<Artist>? = null
    @Volatile private var LOADER_PLAYLISTS: Loader<Playlist>? = null
    @Volatile private var LOADER_GENRES: Loader<Genre>? = null

    private val listeners = ArrayList<LibraryListener>()

    fun init(context: Context, config: LibraryConfig) {
        Library.context = context.applicationContext

        //Creates tasks
        if (config.get().contains(LibraryData.SONGS)) {
            val songsTask = SongsTask(context)
            songsTask.addListener(object : Loader.TaskListener<Song> {
                override fun onOneLoaded(item: Song, pos: Int) {
                    for (listener in listeners)
                        listener.onSongLoaded(item, pos)
                    // updateLinks();
                }

                override fun onCompleted(result: List<Song>) {
                    Log.d(Library.javaClass.simpleName, "Completed building songs")
                    songs = result
                    for (listener in listeners)
                        listener.onSongsCompleted(result)
                }
            })
            LOADER_SONGS = songsTask
        }
        if (config.get().contains(LibraryData.ALBUMS)) {
            val albumsTask = AlbumsTask(context)
            albumsTask.addListener(object : Loader.TaskListener<Album> {
                override fun onOneLoaded(item: Album, pos: Int) {
                    for (listener in listeners)
                        listener.onAlbumLoaded(item, pos)
                    // updateLinks();
                }

                override fun onCompleted(result: List<Album>) {
                    Log.d(Library.javaClass.simpleName, "Completed building albums")
                    albums = result
                    for (listener in listeners)
                        listener.onAlbumsCompleted(result)
                }
            })
            LOADER_ALBUMS = albumsTask
        }
        if (config.get().contains(LibraryData.PLAYLISTS)) {
            val playlistsTask = PlaylistsTask(context)
            playlistsTask.addListener(object : Loader.TaskListener<Playlist> {
                override fun onOneLoaded(item: Playlist, pos: Int) {
                    for (listener in listeners)
                        listener.onPlaylistLoaded(item, pos)
                    // updateLinks();
                }

                override fun onCompleted(result: List<Playlist>) {
                    Log.d(Library.javaClass.simpleName, "Completed building playlists")
                    playlists = result
                    for (listener in listeners)
                        listener.onPlaylistsCompleted(result)
                }
            })
            LOADER_PLAYLISTS = playlistsTask
        }
        if (config.get().contains(LibraryData.ARTISTS)) {
            val artistsTask = ArtistsTask(context)
            artistsTask.addListener(object : Loader.TaskListener<Artist> {
                override fun onOneLoaded(item: Artist, pos: Int) {
                    for (listener in listeners)
                        listener.onArtistLoaded(item, pos)
                    // updateLinks();
                }

                override fun onCompleted(result: List<Artist>) {
                    Log.d(Library.javaClass.simpleName, "Completed building artists")
                    artists = result
                    for (listener in listeners)
                        listener.onArtistsCompleted(result)
                }
            })
            LOADER_ARTISTS = artistsTask
        }
        if (config.get().contains(LibraryData.GENRES)) {
            val genresTask = GenresTask(context)
            genresTask.addListener(object : Loader.TaskListener<Genre> {
                override fun onOneLoaded(item: Genre, pos: Int) {
                    for (listener in listeners)
                        listener.onGenreLoaded(item, pos)
                    // updateLinks();
                }

                override fun onCompleted(result: List<Genre>) {
                    Log.d(Library.javaClass.simpleName, "Completed building genres")
                    genres = result
                    for (listener in listeners)
                        listener.onGenresCompleted(result)
                }
            })
            LOADER_GENRES = genresTask
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Builds the media library
    ///////////////////////////////////////////////////////////////////////////

    fun build() {
        LOADER_SONGS?.run()
        LOADER_ALBUMS?.run()
        LOADER_ARTISTS?.run()
        LOADER_PLAYLISTS?.run()
        LOADER_GENRES?.run()
        Log.d(Library::class.java.simpleName, "Library is building...")
    }

    ///////////////////////////////////////////////////////////////////////////
    // Update Listener from MediaStore
    ///////////////////////////////////////////////////////////////////////////

    /////////////
    // Library //
    /////////////

    fun registerListener(listener: LibraryListener) {
        listeners.add(listener)
    }

    fun unregisterListener(listener: LibraryListener) {
        listeners.remove(listener)
    }

    /////////////////
    // Media Store //
    /////////////////

    fun registerMediaStoreListeners() {
        LOADER_SONGS?.registerMediaStoreListener()
        LOADER_ALBUMS?.registerMediaStoreListener()
        LOADER_ARTISTS?.registerMediaStoreListener()
        LOADER_PLAYLISTS?.registerMediaStoreListener()
        LOADER_GENRES?.registerMediaStoreListener()
    }

    fun unregisterMediaStoreListeners() {
        LOADER_SONGS?.unregisterMediaStoreListener()
        LOADER_ALBUMS?.unregisterMediaStoreListener()
        LOADER_ARTISTS?.unregisterMediaStoreListener()
        LOADER_PLAYLISTS?.unregisterMediaStoreListener()
        LOADER_GENRES?.unregisterMediaStoreListener()
    }

    //////////
    // Song //
    //////////

    fun registerSongListener(songListener: Loader.TaskListener<Song>) { LOADER_SONGS?.addListener(songListener) }

    fun unregisterSongListener(songListener: Loader.TaskListener<Song>) { LOADER_SONGS?.removeListener(songListener) }

    ///////////
    // Album //
    ///////////

    fun registerAlbumListener(albumListener: Loader.TaskListener<Album>) { LOADER_ALBUMS?.addListener(albumListener) }

    fun unregisterAlbumListener(albumListener: Loader.TaskListener<Album>) { LOADER_ALBUMS?.removeListener(albumListener) }

    //////////////
    // Playlist //
    //////////////

    fun registerPlaylistListener(playlistListener: Loader.TaskListener<Playlist>) { LOADER_PLAYLISTS?.addListener(playlistListener) }

    fun unregisterPlaylistListener(playlistListener: Loader.TaskListener<Playlist>) { LOADER_PLAYLISTS?.removeListener(playlistListener) }

    ////////////
    // Artist //
    ////////////

    fun registerArtistListener(artistListener: Loader.TaskListener<Artist>) { LOADER_ARTISTS?.addListener(artistListener) }

    fun unregisterArtistListener(artistListener: Loader.TaskListener<Artist>) { LOADER_ARTISTS?.removeListener(artistListener) }

    ////////////
    // Genres //
    ////////////

    fun registerGenresListener(genreListener: Loader.TaskListener<Genre>) { LOADER_GENRES?.addListener(genreListener) }

    fun unregisterGenresListener(genreListener: Loader.TaskListener<Genre>) { LOADER_GENRES?.removeListener(genreListener) }

    ///////////////////////////////////////////////////////////////////////////
    // Methods for finding a media object by ID
    ///////////////////////////////////////////////////////////////////////////

    fun findSongById(id: Long): Song? {
        for (song in songs) if (song.id == id) return song
        return null
    }

    fun findSongByUri(uri: Uri): Song? {
        for (song in songs) if (song.uri === uri) return song
        return null
    }

    fun findAlbumById(id: Long): Album? {
        for (album in albums) if (album.id == id) return album
        return null
    }

    fun findArtistById(id: Long): Artist? {
        for (artist in artists) if (artist.id == id) return artist
        return null
    }

    fun findPlaylistById(id: Long): Playlist? {
        for (playlist in playlists)
            if (playlist.id == id) return playlist
        return null
    }

    ///////////////////////////////////////////////////////////////////////////
    // Find X for Y
    ///////////////////////////////////////////////////////////////////////////

    fun findSongsForArtist(artist: Artist): List<Song> {
        val songs = ArrayList<Song>()
        for (song in this.songs) {
            if (song.songArtistId == artist.id)
                songs.add(song)
        }
        return songs
    }

    @SuppressWarnings("unchecked")
    fun findSongsForArtistAsync(artist: Artist, callback: ModularAsyncTask.TaskCallback<List<Song>>) {
        ModularAsyncTask<List<Song>>(callback).execute({ findSongsForArtist(artist) })
    }

    fun findAlbumsForArtist(artist: Artist): List<Album> {
        val albums = ArrayList<Album>()
        for (album in this.albums) {
            if (album.albumArtistName == artist.artistName)
                albums.add(album)
        }
        return albums
    }

    @SuppressWarnings("unchecked")
    fun findAlbumsForArtistAsync(artist: Artist, callback: ModularAsyncTask.TaskCallback<List<Album>>) {
        ModularAsyncTask<List<Album>>(callback).execute({ findAlbumsForArtist(artist) })
    }

    fun findSongsForAlbum(album: Album): List<Song> {
        val songs = ArrayList<Song>()
        for (song in this.songs) {
            if (song.songAlbumId == album.id)
                songs.add(song)
        }
        return songs
    }

    @SuppressWarnings("unchecked")
    fun findSongsForAlbumAsync(album: Album, callback: ModularAsyncTask.TaskCallback<List<Song>>) {
        ModularAsyncTask<List<Song>>(callback).execute({ findSongsForAlbum(album) })
    }

    fun findArtistForAlbum(album: Album): Artist? {
        for (artist in artists) {
            if (artist.artistName == album.albumArtistName)
                return artist
        }
        return null
    }

    @SuppressWarnings("unchecked")
    fun findArtistForAlbumAsync(album: Album, callback: ModularAsyncTask.TaskCallback<Artist>) {
        ModularAsyncTask<Artist>(callback).execute({ findArtistForAlbum(album)!! })
    }

    @Throws(NullPointerException::class)
    fun findSongsForPlaylist(context: Context, playlist: Playlist): List<Song> {
        val songs = ArrayList<Song>()
        try {
            val playlistSongsCursor = context.contentResolver.query(MediaStore.Audio.Playlists.Members.getContentUri("external", playlist.id),
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

    @SuppressWarnings("unchecked")
    fun findSongsForPlaylistAsync(context: Context, playlist: Playlist, callback: ModularAsyncTask.TaskCallback<List<Song>>) {
        ModularAsyncTask<List<Song>>(callback).execute({ findSongsForPlaylist(context, playlist) })
    }

    @Throws(NullPointerException::class)
    fun findSongsForGenre(context: Context, genre: Genre): List<Song> {
        val songs = ArrayList<Song>()
        try {
            val genreSongsCursor = context.contentResolver.query(MediaStore.Audio.Genres.Members.getContentUri("external", genre.id),
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

    @SuppressWarnings("unchecked")
    fun findSongsForGenreAsync(context: Context, genre: Genre, callback: ModularAsyncTask.TaskCallback<List<Song>>) {
        ModularAsyncTask<List<Song>>(callback).execute({ findSongsForGenre(context, genre) })
    }

    ///////////////////////////////////////////////////////////////////////////
    // Search
    ///////////////////////////////////////////////////////////////////////////

    fun filterAlbums(query: String): SearchResult {
        val results = ArrayList<Album>()
        for (a in albums) {
            if (a.albumName!!.toLowerCase().contains(query.toLowerCase())) {
                if (!results.contains(a)) results.add(a)
            }

            if (a.albumArtistName!!.toLowerCase().contains(query.toLowerCase())) {
                if (!results.contains(a)) results.add(a)
            }
        }
        return SearchResult(context!!.getString(R.string.albums), results)
    }

    fun filterSongs(query: String): SearchResult {
        val results = ArrayList<Song>()
        for (s in songs) {
            if (s.songTitle!!.toLowerCase().contains(query.toLowerCase())) {
                if (!results.contains(s)) results.add(s)
            }

            if (s.songArtistName!!.toLowerCase().contains(query.toLowerCase())) {
                if (!results.contains(s)) results.add(s)
            }
        }
        return SearchResult(context!!.getString(R.string.songs), results)
    }

    fun filterPlaylists(query: String): SearchResult {
        val results = ArrayList<Playlist>()
        for (p in playlists) {
            if (p.playlistName!!.toLowerCase().contains(query.toLowerCase())) {
                if (!results.contains(p)) results.add(p)
            }
        }
        return SearchResult(context!!.getString(R.string.playlists), results)
    }

    fun search(query: String): List<SearchResult> {
        val output = ArrayList<SearchResult>()
        filterAlbums(query).addIfNotEmpty(output)
        filterSongs(query).addIfNotEmpty(output)
        filterPlaylists(query).addIfNotEmpty(output)
        return Collections.unmodifiableList(output)
    }


}
