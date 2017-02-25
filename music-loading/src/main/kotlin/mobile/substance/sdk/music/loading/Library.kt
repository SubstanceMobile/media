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
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.support.v7.app.AppCompatActivity
import android.util.Log
import mobile.substance.sdk.music.core.MusicApiError
import mobile.substance.sdk.options.MusicCoreOptions
import mobile.substance.sdk.music.core.dataLinkers.MusicData
import mobile.substance.sdk.music.core.dataLinkers.MusicLibraryData
import mobile.substance.sdk.music.core.objects.*
import mobile.substance.sdk.music.loading.tasks.*
import java.util.*

@SuppressWarnings("unused")
object Library : MusicLibraryData {
    private var activity: AppCompatActivity? = null

    override fun getContext(): Context {
        if (activity != null) return activity as Context
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

    private var LOADER_SONGS: MediaLoader<Song>? = null
    private var LOADER_ALBUMS: MediaLoader<Album>? = null
    private var LOADER_ARTISTS: MediaLoader<Artist>? = null
    private var LOADER_PLAYLISTS: MediaLoader<Playlist>? = null
    private var LOADER_GENRES: MediaLoader<Genre>? = null

    private val buildFinishedListeners = ArrayList<() -> Any>()
    private val buildState = kotlin.arrayOfNulls<Boolean>(5)

    private fun checkIsFinished() {
        if (isBuilt()) {
            buildFinishedListeners.forEach { it.invoke() }
            buildFinishedListeners.clear()
        }
    }

    fun isBuilt(): Boolean = buildState.all { it ?: true }

    fun enable() = MusicData.hook(this)

    @JvmOverloads fun init(activity: AppCompatActivity, libraryConfig: LibraryConfig = LibraryConfig()): Library {
        Library.activity = activity

        //Create tasks
        if (libraryConfig.contains(MusicType.SONGS)) {
            val songsLoader = SongsLoader(activity)
            songsLoader.init()
            if (buildState[0] == null) buildState[0] = false
            songsLoader.addListener(object : MediaLoader.TaskListener<Song> {
                override fun onOneLoaded(item: Song, pos: Int) {
                    for (listener in listeners) listener.onSongLoaded(item, pos)
                }

                override fun onCompleted(result: List<Song>) {
                    if (buildState[0] == false) buildState[0] = true
                    Log.d(Library.javaClass.simpleName, "Completed building songs")
                    songs = result.toMutableList()
                    for (listener in listeners) listener.onSongsCompleted(result)
                    checkIsFinished()
                }
            })
            LOADER_SONGS = songsLoader
        }
        if (libraryConfig.contains(MusicType.ALBUMS)) {
            val albumsLoader = AlbumsLoader(activity)
            albumsLoader.init()
            if (buildState[1] == null) buildState[1] = false
            albumsLoader.addListener(object : MediaLoader.TaskListener<Album> {
                override fun onOneLoaded(item: Album, pos: Int) {
                    for (listener in listeners) listener.onAlbumLoaded(item, pos)
                }

                override fun onCompleted(result: List<Album>) {
                    if (buildState[1] == false) buildState[1] = true
                    Log.d(Library.javaClass.simpleName, "Completed building albums")
                    albums = result.toMutableList()
                    for (listener in listeners) listener.onAlbumsCompleted(result)
                    checkIsFinished()
                }
            })
            LOADER_ALBUMS = albumsLoader
        }
        if (libraryConfig.contains(MusicType.ARTISTS)) {
            val artistsLoader = ArtistsLoader(activity)
            artistsLoader.init()
            if (buildState[2] == null) buildState[2] = false
            artistsLoader.addListener(object : MediaLoader.TaskListener<Artist> {
                override fun onOneLoaded(item: Artist, pos: Int) {
                    for (listener in listeners) listener.onArtistLoaded(item, pos)
                }

                override fun onCompleted(result: List<Artist>) {
                    if (buildState[2] == false) buildState[2] = true
                    Log.d(Library.javaClass.simpleName, "Completed building artists")
                    artists = result.toMutableList()
                    for (listener in listeners) listener.onArtistsCompleted(result)
                    checkIsFinished()
                }
            })
            LOADER_ARTISTS = artistsLoader
        }
        if (libraryConfig.contains(MusicType.PLAYLISTS)) {
            val playlistsLoader = PlaylistsLoader(activity)
            playlistsLoader.init()
            if (buildState[3] == null) buildState[3] = false
            playlistsLoader.addListener(object : MediaLoader.TaskListener<Playlist> {
                override fun onOneLoaded(item: Playlist, pos: Int) {
                    for (listener in listeners) listener.onPlaylistLoaded(item, pos)
                }

                override fun onCompleted(result: List<Playlist>) {
                    if (buildState[3] == false) buildState[3] = true
                    Log.d(Library.javaClass.simpleName, "Completed building playlists")
                    playlists = result.toMutableList()
                    for (listener in listeners) listener.onPlaylistsCompleted(result)
                    checkIsFinished()
                }
            })
            LOADER_PLAYLISTS = playlistsLoader
        }
        if (libraryConfig.contains(MusicType.GENRES)) {
            val genresLoader = GenresLoader(activity)
            genresLoader.init()
            if (buildState[4] == null) buildState[4] = false
            genresLoader.addListener(object : MediaLoader.TaskListener<Genre> {
                override fun onOneLoaded(item: Genre, pos: Int) {
                    for (listener in listeners) listener.onGenreLoaded(item, pos)
                }

                override fun onCompleted(result: List<Genre>) {
                    if (buildState[4] == false) buildState[4] = true
                    Log.d(Library.javaClass.simpleName, "Completed building genres")
                    genres = result.toMutableList()
                    for (listener in listeners) listener.onGenresCompleted(result)
                    checkIsFinished()
                }
            })
            LOADER_GENRES = genresLoader
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

    //////////
    // Song //
    //////////

    fun registerSongListener(songListener: MediaLoader.TaskListener<Song>) = LOADER_SONGS?.addListener(songListener)

    fun unregisterSongListener(songListener: MediaLoader.TaskListener<Song>) = LOADER_SONGS?.removeListener(songListener)

    ///////////
    // Album //
    ///////////

    fun registerAlbumListener(albumListener: MediaLoader.TaskListener<Album>) = LOADER_ALBUMS?.addListener(albumListener)

    fun unregisterAlbumListener(albumListener: MediaLoader.TaskListener<Album>) = LOADER_ALBUMS?.removeListener(albumListener)

    //////////////
    // Playlist //
    //////////////

    fun registerPlaylistListener(playlistListener: MediaLoader.TaskListener<Playlist>) = LOADER_PLAYLISTS?.addListener(playlistListener)

    fun unregisterPlaylistListener(playlistListener: MediaLoader.TaskListener<Playlist>) = LOADER_PLAYLISTS?.removeListener(playlistListener)

    ////////////
    // Artist //
    ////////////

    fun registerArtistListener(artistListener: MediaLoader.TaskListener<Artist>) = LOADER_ARTISTS?.addListener(artistListener)

    fun unregisterArtistListener(artistListener: MediaLoader.TaskListener<Artist>) = LOADER_ARTISTS?.removeListener(artistListener)

    ////////////
    // Genres //
    ////////////

    fun registerGenresListener(genreListener: MediaLoader.TaskListener<Genre>) = LOADER_GENRES?.addListener(genreListener)

    fun unregisterGenresListener(genreListener: MediaLoader.TaskListener<Genre>) = LOADER_GENRES?.removeListener(genreListener)

}
