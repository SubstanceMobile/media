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

package mobile.substance.media.audio.local

import android.app.Activity
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import mobile.substance.media.audio.local.objects.*
import mobile.substance.media.local.core.MediaLoader
import mobile.substance.media.audio.local.tasks.*
import mobile.substance.media.core.audio.*
import mobile.substance.media.local.core.LocalMediaHolder
import mobile.substance.media.options.AudioLocalOptions.config
import java.util.*

abstract class LocalAudioHolder<Song : MediaStoreSong,
        Album : MediaStoreAlbum,
        Artist : MediaStoreArtist,
        Playlist : MediaStorePlaylist,
        Genre : MediaStoreGenre> : LocalMediaHolder(), Audio {

    companion object {
        val TAG = LocalAudioHolder::class.java.simpleName
        const val LOADER_ID_SONGS = 11
        const val LOADER_ID_ALBUMS = 12
        const val LOADER_ID_ARTISTS = 13
        const val LOADER_ID_PLAYLISTS = 14
        const val LOADER_ID_GENRES = 15
    }

    open val songsApplicator: MediaLoader.Applicator<Song>? = null
    open val albumsApplicator: MediaLoader.Applicator<Album>? = null
    open val artistsApplicator: MediaLoader.Applicator<Artist>? = null
    open val playlistsApplicator: MediaLoader.Applicator<Playlist>? = null
    open val genresApplicator: MediaLoader.Applicator<Genre>? = null

    ///////////////////////////////////////////////////////////////////////////
    // Main Getters
    ///////////////////////////////////////////////////////////////////////////

    private var songs: MutableList<Song> = mutableListOf()
    override fun getSongs(): List<Song> = songs

    private var albums: MutableList<Album> = mutableListOf()
    override fun getAlbums(): List<Album> = albums

    private var playlists: MutableList<Playlist> = mutableListOf()
    override fun getPlaylists(): List<Playlist> = playlists

    private var artists: MutableList<Artist> = mutableListOf()
    override fun getArtists(): List<Artist> = artists

    private var genres: MutableList<Genre> = mutableListOf()
    override fun getGenres(): List<Genre> = genres

    override fun initLoaders() {
        if (config.contains(AUDIO_TYPE_SONGS)) addLoader(LOADER_ID_SONGS, object : MediaLoader.Listener<Song> {
            override fun onLoaded(item: Song) {
                for (listener in listeners) listener.onSongLoaded(item)
            }

            override fun onLoaded(output: List<Song>) {
                Log.d(TAG, "Completed building songs, ${output.size} to be exact")
                songs = output.toMutableList()
                for (listener in listeners) listener.onSongsCompleted(output)
            }
        }, songsApplicator)
        if (config.contains(AUDIO_TYPE_ALBUMS)) addLoader(LOADER_ID_ALBUMS, object : MediaLoader.Listener<Album> {
            override fun onLoaded(item: Album) {
                for (listener in listeners) listener.onAlbumLoaded(item)
            }

            override fun onLoaded(output: List<Album>) {
                Log.d(TAG, "Completed building albums")
                albums = output.toMutableList()
                for (listener in listeners) listener.onAlbumsCompleted(output)
            }
        }, albumsApplicator)
        if (config.contains(AUDIO_TYPE_ARTISTS)) addLoader(LOADER_ID_ARTISTS, object : MediaLoader.Listener<Artist> {
            override fun onLoaded(item: Artist) {
                for (listener in listeners) listener.onArtistLoaded(item)
            }

            override fun onLoaded(output: List<Artist>) {
                Log.d(TAG, "Completed building artists")
                artists = output.toMutableList()
                for (listener in listeners) listener.onArtistsCompleted(output)
            }
        }, artistsApplicator)
        if (config.contains(AUDIO_TYPE_PLAYLISTS)) addLoader(LOADER_ID_PLAYLISTS, object : MediaLoader.Listener<Playlist> {
            override fun onLoaded(item: Playlist) {
                for (listener in listeners) listener.onPlaylistLoaded(item)
            }

            override fun onLoaded(output: List<Playlist>) {
                Log.d(TAG, "Completed building playlists")
                playlists = output.toMutableList()
                for (listener in listeners) listener.onPlaylistsCompleted(output)
            }
        }, playlistsApplicator)
        if (config.contains(AUDIO_TYPE_GENRES)) addLoader(LOADER_ID_GENRES, object : MediaLoader.Listener<Genre> {
            override fun onLoaded(item: Genre) {
                for (listener in listeners) listener.onGenreLoaded(item)
            }

            override fun onLoaded(output: List<Genre>) {
                Log.d(TAG, "Completed building genres")
                genres = output.toMutableList()
                for (listener in listeners) listener.onGenresCompleted(output)
            }
        }, genresApplicator)
    }

    override fun onStopActivity(activity: Activity) {
        config.clear()
    }

    ///////////////////////////////////////////////////////////////////////////
    // Listeners
    ///////////////////////////////////////////////////////////////////////////

    private val listeners = ArrayList<MediaStoreAudioHolderListener>()

    fun registerListener(listener: MediaStoreAudioHolderListener) {
        listeners.add(listener)
    }

    fun unregisterListener(listener: MediaStoreAudioHolderListener) {
        listeners.remove(listener)
    }

    override fun createLoader(id: Int): MediaLoader<*>? {
        when (id) {
            LOADER_ID_SONGS -> return SongsLoader<Song>(getActivity()!!)
            LOADER_ID_ALBUMS -> return AlbumsLoader<Album>(getActivity()!!)
            LOADER_ID_ARTISTS -> return ArtistsLoader<Artist>(getActivity()!!)
            LOADER_ID_PLAYLISTS -> return PlaylistsLoader<Playlist>(getActivity()!!)
            LOADER_ID_GENRES -> return GenresLoader<Genre>(getActivity()!!)
            else -> return null
        }
    }

    override fun onInvalidateHolder() {
        songs.clear()
        albums.clear()
        artists.clear()
        playlists.clear()
        genres.clear()
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

    fun findSongsForPlaylist(playlist: MediaStorePlaylist): List<Song> {
        val songs = ArrayList<Song>()
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

    fun findSongsForGenre(genre: MediaStoreGenre): List<Song> {
        val songs = ArrayList<Song>()
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

    fun findAlbumsForGenre(genre: MediaStoreGenre): List<Album> {
        val albums = ArrayList<Album>()
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

    fun findGenreForSong(song: MediaStoreSong): Genre? = getGenres().firstOrNull {
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

    fun findGenreForAlbum(album: MediaStoreAlbum): Genre? = getGenres().firstOrNull {
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
