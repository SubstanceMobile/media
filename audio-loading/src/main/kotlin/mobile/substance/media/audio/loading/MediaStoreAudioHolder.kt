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

package mobile.substance.media.audio.loading

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.util.Log
import mobile.substance.media.audio.loading.objects.*
import mobile.substance.media.core.MediaCore
import mobile.substance.media.loading.core.MediaLoader
import mobile.substance.media.audio.loading.tasks.*
import mobile.substance.media.core.audio.*
import java.util.*

@SuppressWarnings("unused")
object MediaStoreAudioHolder : AudioHolder(), LoaderManager.LoaderCallbacks<List<*>> {
    private val config = MediaStoreAudioHolderConfiguration()

    override fun getType(): Long {
        return MediaCore.MEDIA_TYPE_AUDIO
    }

    ///////////////////////////////////////////////////////////////////////////
    // Main Getters
    ///////////////////////////////////////////////////////////////////////////

    private var songs: MutableList<MediaStoreSong> = mutableListOf()
    override fun getSongs(): List<MediaStoreSong> = songs

    private var albums: MutableList<MediaStoreAlbum> = mutableListOf()
    override fun getAlbums(): List<MediaStoreAlbum> = albums

    private var playlists: MutableList<MediaStorePlaylist> = mutableListOf()
    override fun getPlaylists(): List<MediaStorePlaylist> = playlists

    private var artists: MutableList<MediaStoreArtist> = mutableListOf()
    override fun getArtists(): List<MediaStoreArtist> = artists

    private var genres: MutableList<MediaStoreGenre> = mutableListOf()
    override fun getGenres(): List<MediaStoreGenre> = genres

    ///////////////////////////////////////////////////////////////////////////
    // Setup and building
    ///////////////////////////////////////////////////////////////////////////

    private var songsLoader: MediaLoader<MediaStoreSong>? = null
    private var albumsLoader: MediaLoader<MediaStoreAlbum>? = null
    private var artistsLoader: MediaLoader<MediaStoreArtist>? = null
    private var playlistsLoader: MediaLoader<MediaStorePlaylist>? = null
    private var genresLoader: MediaLoader<MediaStoreGenre>? = null

    private val buildFinishedListeners = ArrayList<() -> Any>()
    private val buildState = kotlin.arrayOfNulls<Boolean>(5)

    private fun checkIsFinished() {
        if (isBuilt()) {
            buildFinishedListeners.forEach { it.invoke() }
            buildFinishedListeners.clear()
        }
    }

    fun isBuilt(): Boolean = isActivityStarted() && buildState.all { it ?: true }

    override fun onStartActivity(activity: Activity) {
        if (config.contains(AUDIO_TYPE_SONGS)) {
            val songsLoader = getActivity()?.supportLoaderManager?.initLoader(10, Bundle.EMPTY, this) as SongsLoader?
            if (buildState[0] == null) buildState[0] = false
            songsLoader?.addListener(object : MediaLoader.TaskListener<MediaStoreSong> {
                override fun onOneLoaded(item: MediaStoreSong, pos: Int) {
                    for (listener in listeners) listener.onSongLoaded(item, pos)
                }

                override fun onCompleted(result: List<MediaStoreSong>) {
                    if (buildState[0] == false) buildState[0] = true
                    Log.d(MediaStoreAudioHolder.javaClass.simpleName, "Completed building songs")
                    songs = result.toMutableList()
                    for (listener in listeners) listener.onSongsCompleted(result)
                    checkIsFinished()
                }
            })
            this.songsLoader = songsLoader
        }
        if (config.contains(AUDIO_TYPE_ALBUMS)) {
            val albumsLoader = getActivity()?.supportLoaderManager?.initLoader(11, Bundle.EMPTY, this) as AlbumsLoader?
            if (buildState[1] == null) buildState[1] = false
            albumsLoader?.addListener(object : MediaLoader.TaskListener<MediaStoreAlbum> {
                override fun onOneLoaded(item: MediaStoreAlbum, pos: Int) {
                    for (listener in listeners) listener.onAlbumLoaded(item, pos)
                }

                override fun onCompleted(result: List<MediaStoreAlbum>) {
                    if (buildState[1] == false) buildState[1] = true
                    Log.d(MediaStoreAudioHolder.javaClass.simpleName, "Completed building albums")
                    albums = result.toMutableList()
                    for (listener in listeners) listener.onAlbumsCompleted(result)
                    checkIsFinished()
                }
            })
            this.albumsLoader = albumsLoader
        }
        if (config.contains(AUDIO_TYPE_ARTISTS)) {
            val artistsLoader = getActivity()?.supportLoaderManager?.initLoader(12, Bundle.EMPTY, this) as ArtistsLoader?
            if (buildState[2] == null) buildState[2] = false
            artistsLoader?.addListener(object : MediaLoader.TaskListener<MediaStoreArtist> {
                override fun onOneLoaded(item: MediaStoreArtist, pos: Int) {
                    for (listener in listeners) listener.onArtistLoaded(item, pos)
                }

                override fun onCompleted(result: List<MediaStoreArtist>) {
                    if (buildState[2] == false) buildState[2] = true
                    Log.d(MediaStoreAudioHolder.javaClass.simpleName, "Completed building artists")
                    artists = result.toMutableList()
                    for (listener in listeners) listener.onArtistsCompleted(result)
                    checkIsFinished()
                }
            })
            this.artistsLoader = artistsLoader
        }
        if (config.contains(AUDIO_TYPE_PLAYLISTS)) {
            val playlistsLoader = getActivity()?.supportLoaderManager?.initLoader(13, Bundle.EMPTY, this) as PlaylistsLoader?
            if (buildState[3] == null) buildState[3] = false
            playlistsLoader?.addListener(object : MediaLoader.TaskListener<MediaStorePlaylist> {
                override fun onOneLoaded(item: MediaStorePlaylist, pos: Int) {
                    for (listener in listeners) listener.onPlaylistLoaded(item, pos)
                }

                override fun onCompleted(result: List<MediaStorePlaylist>) {
                    if (buildState[3] == false) buildState[3] = true
                    Log.d(MediaStoreAudioHolder.javaClass.simpleName, "Completed building playlists")
                    playlists = result.toMutableList()
                    for (listener in listeners) listener.onPlaylistsCompleted(result)
                    checkIsFinished()
                }
            })
            this.playlistsLoader = playlistsLoader
        }
        if (config.contains(AUDIO_TYPE_GENRES)) {
            val genresLoader = getActivity()?.supportLoaderManager?.initLoader(14, Bundle.EMPTY, this) as GenresLoader?
            if (buildState[4] == null) buildState[4] = false
            genresLoader?.addListener(object : MediaLoader.TaskListener<MediaStoreGenre> {
                override fun onOneLoaded(item: MediaStoreGenre, pos: Int) {
                    for (listener in listeners) listener.onGenreLoaded(item, pos)
                }

                override fun onCompleted(result: List<MediaStoreGenre>) {
                    if (buildState[4] == false) buildState[4] = true
                    Log.d(MediaStoreAudioHolder.javaClass.simpleName, "Completed building genres")
                    genres = result.toMutableList()
                    for (listener in listeners) listener.onGenresCompleted(result)
                    checkIsFinished()
                }
            })
            this.genresLoader = genresLoader
        }

        if (isActive()) build()
    }

    override fun onStopActivity(activity: Activity) {
        // Do nothing
    }

    fun configure() = config

    fun build() {
        songsLoader?.forceLoad()
        albumsLoader?.forceLoad()
        artistsLoader?.forceLoad()
        playlistsLoader?.forceLoad()
        genresLoader?.forceLoad()
        Log.d(MediaStoreAudioHolder.javaClass.simpleName, "MediaStoreAudioHolder is building...")
    }


    ///////////////////////////////////////////////////////////////////////////
    // Listeners
    ///////////////////////////////////////////////////////////////////////////

    /////////
    // All //
    /////////

    private val listeners = ArrayList<MediaStoreAudioHolderListener>()

    fun registerListener(listener: MediaStoreAudioHolderListener) {
        listeners.add(listener)
    }

    fun unregisterListener(listener: MediaStoreAudioHolderListener) {
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

    fun registerSongListener(songListener: MediaLoader.TaskListener<Song>) = songsLoader?.addListener(songListener)

    fun unregisterSongListener(songListener: MediaLoader.TaskListener<Song>) = songsLoader?.removeListener(songListener)

    ///////////
    // Album //
    ///////////

    fun registerAlbumListener(albumListener: MediaLoader.TaskListener<Album>) = albumsLoader?.addListener(albumListener)

    fun unregisterAlbumListener(albumListener: MediaLoader.TaskListener<Album>) = albumsLoader?.removeListener(albumListener)

    //////////////
    // Playlist //
    //////////////

    fun registerPlaylistListener(playlistListener: MediaLoader.TaskListener<Playlist>) = playlistsLoader?.addListener(playlistListener)

    fun unregisterPlaylistListener(playlistListener: MediaLoader.TaskListener<Playlist>) = playlistsLoader?.removeListener(playlistListener)

    ////////////
    // Artist //
    ////////////

    fun registerArtistListener(artistListener: MediaLoader.TaskListener<Artist>) = artistsLoader?.addListener(artistListener)

    fun unregisterArtistListener(artistListener: MediaLoader.TaskListener<Artist>) = artistsLoader?.removeListener(artistListener)

    ////////////
    // Genres //
    ////////////

    fun registerGenresListener(genreListener: MediaLoader.TaskListener<Genre>) = genresLoader?.addListener(genreListener)

    fun unregisterGenresListener(genreListener: MediaLoader.TaskListener<Genre>) = genresLoader?.removeListener(genreListener)


    ///////////////////////////////////////////////////////////////////////////
    // LoaderManager.LoaderCallbacks
    ///////////////////////////////////////////////////////////////////////////

    override fun onLoaderReset(loader: Loader<List<*>>?) {

    }

    override fun onLoadFinished(loader: Loader<List<*>>?, data: List<*>?) {
        println("onLoadFinished() ${loader!!.id}")
        val result = data ?: emptyList<mobile.substance.media.core.MediaObject>()
        (loader as MediaLoader).verifyListener.onCompleted(result as List<Nothing>)
        (loader as MediaLoader).registerObserver()
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<*>>? {
        when (id) {
            10 -> return SongsLoader(getActivity()!!) as Loader<List<*>>
            11 -> return AlbumsLoader(getActivity()!!) as Loader<List<*>>
            12 -> return ArtistsLoader(getActivity()!!) as Loader<List<*>>
            13 -> return PlaylistsLoader(getActivity()!!) as Loader<List<*>>
            14 -> return GenresLoader(getActivity()!!) as Loader<List<*>>
            else -> return null
        }
    }

    override fun onInvalidateHolder() {
        songs.clear()
        songsLoader?.abandon()

        albums.clear()
        albumsLoader?.abandon()

        artists.clear()
        artistsLoader?.abandon()

        playlists.clear()
        playlistsLoader?.abandon()

        genres.clear()
        genresLoader?.abandon()
    }



    fun findSongById(id: Long) = getSongs().firstOrNull { it.id == id }

    fun findSongByUri(uri: Uri) = getSongs().firstOrNull { it.uri === uri }

    fun findAlbumById(id: Long) = getAlbums().firstOrNull { it.id == id }

    fun findArtistById(id: Long) = getArtists().firstOrNull { it.id == id }

    fun findPlaylistById(id: Long) = getPlaylists().firstOrNull { it.id == id }

    fun findGenreById(id: Long) = getGenres().firstOrNull { it.id == id }

    ///////////////////////////////////////////////////////////////////////////
    // findXforY()
    // NOTE: These have default implementations. For convenience
    ///////////////////////////////////////////////////////////////////////////

    fun findSongsForArtist(artist: MediaStoreArtist) = getSongs().filter { it.artistId == artist.id }

    fun findAlbumsForArtist(artist: MediaStoreArtist) = getAlbums().filter { it.artistName == artist.name }

    fun findSongsForAlbum(album: MediaStoreAlbum) = getSongs().filter { it.albumId == album.id }

    fun findArtistForAlbum(album: MediaStoreAlbum) = getArtists().firstOrNull { it.name == album.artistName }

    fun findSongsForPlaylist(playlist: MediaStorePlaylist): List<MediaStoreSong> {
        val songs = ArrayList<MediaStoreSong>()
        try {
            val playlistSongsCursor = getActivity()?.contentResolver?.query(MediaStore.Audio.Playlists.Members.getContentUri("external", playlist.id),
                    null, null, null,
                    MediaStore.Audio.Playlists.Members.PLAY_ORDER)
            val idColumn = playlistSongsCursor!!.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID)
            playlistSongsCursor.moveToFirst()
            do {
                val song = findSongById(playlistSongsCursor.getLong(idColumn))
                if (song != null) songs.add(song)
            } while (playlistSongsCursor.moveToNext())
            playlistSongsCursor.close()
            return songs
        } catch (e: IndexOutOfBoundsException) {
            return emptyList()
        }
    }

    fun findSongsForGenre(genre: MediaStoreGenre): List<MediaStoreSong> {
        val songs = ArrayList<MediaStoreSong>()
        try {
            val genreSongsCursor = getActivity()?.contentResolver?.query(MediaStore.Audio.Genres.Members.getContentUri("external", genre.id),
                    null, null, null, null)
            val idColumn = genreSongsCursor!!.getColumnIndex(MediaStore.Audio.Genres.Members.AUDIO_ID)
            genreSongsCursor.moveToFirst()
            do {
                val song = findSongById(genreSongsCursor.getLong(idColumn))
                if (song != null) songs.add(song)
            } while (genreSongsCursor.moveToNext())
            genreSongsCursor.close()
            return songs
        } catch (e: IndexOutOfBoundsException) {
            return emptyList()
        }
    }

    fun findAlbumsForGenre(genre: MediaStoreGenre): List<MediaStoreAlbum> {
        val albums = ArrayList<MediaStoreAlbum>()
        try {
            val genreSongsCursor = getActivity()?.contentResolver?.query(MediaStore.Audio.Genres.Members.getContentUri("external", genre.id),
                    null, null, null, null)
            val idColumn = genreSongsCursor!!.getColumnIndex(MediaStore.Audio.Genres.Members.ALBUM_ID)
            genreSongsCursor.moveToFirst()
            do {
                val album = findAlbumById(genreSongsCursor.getLong(idColumn))
                if (album != null && !albums.contains(album)) albums.add(album)
            } while (genreSongsCursor.moveToNext())
            genreSongsCursor.close()
            return albums
        } catch (e: IndexOutOfBoundsException) {
            return emptyList()
        }
    }

    fun findGenreForSong(song: MediaStoreSong): MediaStoreGenre? = getGenres().firstOrNull {
        var containsSong = false
        val genreSongsCursor = getActivity()?.contentResolver?.query(MediaStore.Audio.Genres.Members.getContentUri("external", it.id),
                null, null, null, null)
        val idColumn = genreSongsCursor!!.getColumnIndex(MediaStore.Audio.Genres.Members.AUDIO_ID)
        genreSongsCursor.moveToFirst()
        do {
            if (genreSongsCursor.getLong(idColumn) == song.id) {
                containsSong = true
                break
            }
        } while (genreSongsCursor.moveToNext())
        genreSongsCursor.close()
        containsSong
    }

    fun findGenreForAlbum(album: MediaStoreAlbum): MediaStoreGenre? = getGenres().firstOrNull {
        var containsAlbum = false
        val genreSongsCursor = getActivity()?.contentResolver?.query(MediaStore.Audio.Genres.Members.getContentUri("external", it.id),
                null, null, null, null)
        val idColumn = genreSongsCursor!!.getColumnIndex(MediaStore.Audio.Genres.Members.ALBUM_ID)
        genreSongsCursor.moveToFirst()
        do {
            if (genreSongsCursor.getLong(idColumn) == album.id) {
                containsAlbum = true
                break
            }
        } while (genreSongsCursor.moveToNext())
        genreSongsCursor.close()
        containsAlbum
    }

}
