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
import android.os.AsyncTask
import android.provider.MediaStore
import android.util.Log
import mobile.substance.sdk.music.core.libraryhooks.PlaybackLibHook
import mobile.substance.sdk.music.core.libraryhooks.TagsLibHook
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

    private fun hookIfNecessary(libraryConfig: LibraryConfig) {
        if (libraryConfig.playbackHook) {
            PlaybackLibHook.albumList = { Library.albums }
            PlaybackLibHook.songList = { Library.songs }
        }
        if (libraryConfig.tagsHook) {
            TagsLibHook.albumList = { Library.albums }
            TagsLibHook.songList = { Library.songs }
        }
    }

    fun init(context: Context, libraryConfig: LibraryConfig) {
        Library.context = context.applicationContext

        hookIfNecessary(libraryConfig)

        //Creates tasks
        if (libraryConfig.config.contains(LibraryData.SONGS)) {
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
        if (libraryConfig.config.contains(LibraryData.ALBUMS)) {
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
        if (libraryConfig.config.contains(LibraryData.PLAYLISTS)) {
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
        if (libraryConfig.config.contains(LibraryData.ARTISTS)) {
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
        if (libraryConfig.config.contains(LibraryData.GENRES)) {
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

}
