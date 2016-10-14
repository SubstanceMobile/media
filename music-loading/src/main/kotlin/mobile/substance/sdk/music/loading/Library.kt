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
import android.util.Log
import mobile.substance.sdk.music.core.MusicApiError
import mobile.substance.sdk.music.core.MusicCoreOptions
import mobile.substance.sdk.music.core.dataLinkers.MusicData
import mobile.substance.sdk.music.core.dataLinkers.MusicLibraryData
import mobile.substance.sdk.music.core.objects.*
import mobile.substance.sdk.music.loading.tasks.*
import java.util.*

@SuppressWarnings("unused")
object Library : MusicLibraryData {

    private var context: Context? = null

    override fun getContext(): Context {
        if (context != null) return context as Context
        throw MusicApiError("Please call init() on Library")
    }

    ///////////////////////////////////////////////////////////////////////////
    // Main Getters
    ///////////////////////////////////////////////////////////////////////////

    private var songs: MutableList<Song> = mutableListOf()
    override fun getSongs(): MutableList<Song> = songs

    private var albums: MutableList<Album> = mutableListOf()
    override fun getAlbums(): MutableList<Album> = albums

    private var playlists: MutableList<Playlist> = mutableListOf()
    override fun getPlaylists(): MutableList<Playlist> = playlists

    private var artists: MutableList<Artist> = mutableListOf()
    override fun getArtists(): MutableList<Artist> = artists

    private var genres: MutableList<Genre> = mutableListOf()
    override fun getGenres(): MutableList<Genre> = genres

    ///////////////////////////////////////////////////////////////////////////
    // Setup and building
    ///////////////////////////////////////////////////////////////////////////

    private var LOADER_SONGS: Loader<Song>? = null
    private var LOADER_ALBUMS: Loader<Album>? = null
    private var LOADER_ARTISTS: Loader<Artist>? = null
    private var LOADER_PLAYLISTS: Loader<Playlist>? = null
    private var LOADER_GENRES: Loader<Genre>? = null

    private val buildFinishedListeners = ArrayList<() -> Any>()
    private val buildState: Array<Boolean>
        get() = arrayOf(LOADER_SONGS?.isFinished ?: true,
                LOADER_ALBUMS?.isFinished ?: true,
                LOADER_PLAYLISTS?.isFinished ?: true,
                LOADER_ARTISTS?.isFinished ?: true,
                LOADER_GENRES?.isFinished ?: true)

    private fun checkIsFinished() {
        if (isBuilt()) {
            buildFinishedListeners.forEach { it.invoke() }
            buildFinishedListeners.clear()
        }
    }

    fun isBuilt(): Boolean = buildState.all { it }

    @JvmOverloads fun init(context: Context, libraryConfig: LibraryConfig = LibraryConfig()): Library {
        Library.context = context.applicationContext
        if (libraryConfig.hookData) MusicData.hook(this)

        //Create tasks
        if (libraryConfig.contains(MusicType.SONGS)) {
            val songsTask = SongsTask(context)
            songsTask.addListener(object : Loader.TaskListener<Song> {
                override fun onOneLoaded(item: Song, pos: Int) {
                    for (listener in listeners) listener.onSongLoaded(item, pos)
                }

                override fun onCompleted(result: List<Song>) {
                    Log.d(Library.javaClass.simpleName, "Completed building songs")
                    songs = result.toMutableList()
                    for (listener in listeners) listener.onSongsCompleted(result)
                    checkIsFinished()
                }
            })
            LOADER_SONGS = songsTask
        }
        if (libraryConfig.contains(MusicType.ALBUMS)) {
            val albumsTask = AlbumsTask(context)
            albumsTask.addListener(object : Loader.TaskListener<Album> {
                override fun onOneLoaded(item: Album, pos: Int) {
                    for (listener in listeners) listener.onAlbumLoaded(item, pos)
                }

                override fun onCompleted(result: List<Album>) {
                    Log.d(Library.javaClass.simpleName, "Completed building albums")
                    albums = result.toMutableList()
                    for (listener in listeners) listener.onAlbumsCompleted(result)
                    checkIsFinished()
                }
            })
            LOADER_ALBUMS = albumsTask
        }
        if (libraryConfig.contains(MusicType.PLAYLISTS)) {
            val playlistsTask = PlaylistsTask(context)
            playlistsTask.addListener(object : Loader.TaskListener<Playlist> {
                override fun onOneLoaded(item: Playlist, pos: Int) {
                    for (listener in listeners) listener.onPlaylistLoaded(item, pos)
                }

                override fun onCompleted(result: List<Playlist>) {
                    Log.d(Library.javaClass.simpleName, "Completed building playlists")
                    playlists = result.toMutableList()
                    for (listener in listeners) listener.onPlaylistsCompleted(result)
                    checkIsFinished()
                }
            })
            LOADER_PLAYLISTS = playlistsTask
        }
        if (libraryConfig.contains(MusicType.ARTISTS)) {
            val artistsTask = ArtistsTask(context)
            artistsTask.addListener(object : Loader.TaskListener<Artist> {
                override fun onOneLoaded(item: Artist, pos: Int) {
                    for (listener in listeners) listener.onArtistLoaded(item, pos)
                }

                override fun onCompleted(result: List<Artist>) {
                    Log.d(Library.javaClass.simpleName, "Completed building artists")
                    artists = result.toMutableList()
                    for (listener in listeners) listener.onArtistsCompleted(result)
                    checkIsFinished()
                }
            })
            LOADER_ARTISTS = artistsTask
        }
        if (libraryConfig.contains(MusicType.GENRES)) {
            val genresTask = GenresTask(context)
            genresTask.addListener(object : Loader.TaskListener<Genre> {
                override fun onOneLoaded(item: Genre, pos: Int) {
                    for (listener in listeners) listener.onGenreLoaded(item, pos)
                }

                override fun onCompleted(result: List<Genre>) {
                    Log.d(Library.javaClass.simpleName, "Completed building genres")
                    genres = result.toMutableList()
                    for (listener in listeners) listener.onGenresCompleted(result)
                    checkIsFinished()
                }
            })
            LOADER_GENRES = genresTask
        }

        return this
    }

    fun build() {
        LOADER_SONGS?.run()
        LOADER_ALBUMS?.run()
        LOADER_ARTISTS?.run()
        LOADER_PLAYLISTS?.run()
        LOADER_GENRES?.run()
        Log.d(Library.javaClass.simpleName, "Library is building...")
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
        return SearchResult(MusicCoreOptions.albumsString, results)
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
        return SearchResult(MusicCoreOptions.songsString, results)
    }

    fun filterPlaylists(query: String): SearchResult {
        val results = ArrayList<Playlist>()
        for (p in playlists) {
            if (p.playlistName!!.toLowerCase().contains(query.toLowerCase())) {
                if (!results.contains(p)) results.add(p)
            }
        }
        return SearchResult(MusicCoreOptions.playlistsString, results)
    }

    fun filterArtists(query: String): SearchResult {
        val results = ArrayList<Artist>()
        for (a in artists)
            if (a.artistName!!.toLowerCase().contains(query.toLowerCase()))
                if (!results.contains(a)) results.add(a)
        return SearchResult(MusicCoreOptions.artistsString, results)
    }

    fun filterGenres(query: String): SearchResult {
        val results = ArrayList<Genre>()
        for (g in genres) {
            if (g.genreName!!.toLowerCase().contains(query.toLowerCase())) {
                if (!results.contains(g)) results.add(g)
            }
        }
        return SearchResult(MusicCoreOptions.genresString, results)
    }

    fun search(query: String): List<SearchResult> {
        val output: MutableList<SearchResult> = mutableListOf()
        filterAlbums(query).addIfNotEmpty(output)
        filterSongs(query).addIfNotEmpty(output)
        filterPlaylists(query).addIfNotEmpty(output)
        filterArtists(query).addIfNotEmpty(output)
        filterGenres(query).addIfNotEmpty(output)
        return output
    }


    ///////////////////////////////////////////////////////////////////////////
    // Listeners
    ///////////////////////////////////////////////////////////////////////////

    /////////
    // All //
    /////////

    private val listeners = ArrayList<LibraryListener>()

    fun registerListener(listener: LibraryListener) {
        listeners.add(listener)
    }

    fun unregisterListener(listener: LibraryListener) {
        listeners.remove(listener)
    }

    fun registerBuildFinishedListener(listener: () -> Any, checkNow: Boolean = false) {
        if (checkNow)
            if (isBuilt()) {
                listener.invoke()
                return
            }
        buildFinishedListeners.add(listener)
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

    fun registerSongListener(songListener: Loader.TaskListener<Song>) {
        LOADER_SONGS?.addListener(songListener)
    }

    fun unregisterSongListener(songListener: Loader.TaskListener<Song>) {
        LOADER_SONGS?.removeListener(songListener)
    }

    ///////////
    // Album //
    ///////////

    fun registerAlbumListener(albumListener: Loader.TaskListener<Album>) {
        LOADER_ALBUMS?.addListener(albumListener)
    }

    fun unregisterAlbumListener(albumListener: Loader.TaskListener<Album>) {
        LOADER_ALBUMS?.removeListener(albumListener)
    }

    //////////////
    // Playlist //
    //////////////

    fun registerPlaylistListener(playlistListener: Loader.TaskListener<Playlist>) {
        LOADER_PLAYLISTS?.addListener(playlistListener)
    }

    fun unregisterPlaylistListener(playlistListener: Loader.TaskListener<Playlist>) {
        LOADER_PLAYLISTS?.removeListener(playlistListener)
    }

    ////////////
    // Artist //
    ////////////

    fun registerArtistListener(artistListener: Loader.TaskListener<Artist>) {
        LOADER_ARTISTS?.addListener(artistListener)
    }

    fun unregisterArtistListener(artistListener: Loader.TaskListener<Artist>) {
        LOADER_ARTISTS?.removeListener(artistListener)
    }

    ////////////
    // Genres //
    ////////////

    fun registerGenresListener(genreListener: Loader.TaskListener<Genre>) {
        LOADER_GENRES?.addListener(genreListener)
    }

    fun unregisterGenresListener(genreListener: Loader.TaskListener<Genre>) {
        LOADER_GENRES?.removeListener(genreListener)
    }

}
