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

package mobile.substance.sdk.music.loading;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import mobile.substance.sdk.music.core.objects.Album;
import mobile.substance.sdk.music.core.objects.Artist;
import mobile.substance.sdk.music.core.objects.Genre;
import mobile.substance.sdk.music.core.objects.Playlist;
import mobile.substance.sdk.music.core.objects.SearchResult;
import mobile.substance.sdk.music.core.objects.Song;
import mobile.substance.sdk.music.loading.tasks.AlbumsTask;
import mobile.substance.sdk.music.loading.tasks.ArtistsTask;
import mobile.substance.sdk.music.loading.tasks.GenresTask;
import mobile.substance.sdk.music.loading.tasks.Loader;
import mobile.substance.sdk.music.loading.tasks.PlaylistsTask;
import mobile.substance.sdk.music.loading.tasks.SongsTask;

@SuppressWarnings("unused")
public class Library {
    public static volatile Context context;
    private static volatile List<Song> mSongs = new ArrayList<>();
    private static volatile List<Album> mAlbums = new ArrayList<>();
    private static volatile List<Playlist> mPlaylists = new ArrayList<>();
    private static volatile List<Artist> mArtists = new ArrayList<>();
    private static volatile List<Genre> mGenres = new ArrayList<>();

    // Task indices:
    // 0 = Songs
    // 1 = Albums
    // 2 = Playlists
    // 3 = Artists
    // 4 = Genres
    private static volatile Loader[] mTasks = new Loader[5];

    private static volatile List<LibraryListener> listeners = new ArrayList<>();

    public static void init(Context context, LibraryConfig config) {
        Library.context = context.getApplicationContext();

        //Creates tasks
        if (config.get().contains(LibraryData.SONGS)) {
            SongsTask mSongsTask = new SongsTask(context);
            mSongsTask.addListener(new Loader.TaskListener<Song>() {
                @Override
                public void onOneLoaded(Song item, int pos) {
                    for (LibraryListener listener : listeners)
                        if (listener != null) listener.onSongLoaded(item, pos);
                    // updateLinks();
                }

                @Override
                public void onCompleted(List<Song> result) {
                    mSongs = result;
                    for (LibraryListener listener : listeners)
                        if (listener != null) listener.onSongsCompleted(result);
                }
            });
            mTasks[0] = mSongsTask;
        }
        if (config.get().contains(LibraryData.ALBUMS)) {
            AlbumsTask mAlbumsTask = new AlbumsTask(context);
            mAlbumsTask.addListener(new Loader.TaskListener<Album>() {
                @Override
                public void onOneLoaded(Album item, int pos) {
                    for (LibraryListener listener : listeners)
                        if (listener != null) listener.onAlbumLoaded(item, pos);
                    // updateLinks();
                }

                @Override
                public void onCompleted(List<Album> result) {
                    mAlbums = result;
                    for (LibraryListener listener : listeners)
                        if (listener != null) listener.onAlbumsCompleted(result);
                }
            });
            mTasks[1] = mAlbumsTask;
        }
        if (config.get().contains(LibraryData.PLAYLISTS)) {
            PlaylistsTask mPlaylistsTask = new PlaylistsTask(context);
            mPlaylistsTask.addListener(new Loader.TaskListener<Playlist>() {
                @Override
                public void onOneLoaded(Playlist item, int pos) {
                    for (LibraryListener listener : listeners)
                        if (listener != null) listener.onPlaylistLoaded(item, pos);
                    // updateLinks();
                }

                @Override
                public void onCompleted(List<Playlist> result) {
                    mPlaylists = result;
                    for (LibraryListener listener : listeners)
                        if (listener != null) listener.onPlaylistsCompleted(result);
                }
            });
            mTasks[2] = mPlaylistsTask;
        }
        if (config.get().contains(LibraryData.ARTISTS)) {
            ArtistsTask mArtistsTask = new ArtistsTask(context);
            mArtistsTask.addListener(new Loader.TaskListener<Artist>() {
                @Override
                public void onOneLoaded(Artist item, int pos) {
                    for (LibraryListener listener : listeners)
                        if (listener != null) listener.onArtistLoaded(item, pos);
                    // updateLinks();
                }

                @Override
                public void onCompleted(List<Artist> result) {
                    mArtists = result;
                    for (LibraryListener listener : listeners)
                        if (listener != null) listener.onArtistsCompleted(result);
                }
            });
            mTasks[3] = mArtistsTask;
        }
        if (config.get().contains(LibraryData.GENRES)) {
            GenresTask mGenresTask = new GenresTask(context);
            mGenresTask.addListener(new Loader.TaskListener<Genre>() {
                @Override
                public void onOneLoaded(Genre item, int pos) {
                    for (LibraryListener listener : listeners)
                        if (listener != null) listener.onGenreLoaded(item, pos);
                    // updateLinks();
                }

                @Override
                public void onCompleted(List<Genre> result) {
                    mGenres = result;
                    for (LibraryListener listener : listeners)
                        if (listener != null) listener.onGenresCompleted(result);
                }
            });
            mTasks[4] = mGenresTask;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Builds the media library
    ///////////////////////////////////////////////////////////////////////////

    public static void build() {
        for (Loader loader : mTasks) {
            if (loader != null) loader.run();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Update Listener from MediaStore
    ///////////////////////////////////////////////////////////////////////////

    /////////////
    // Library //
    /////////////

    public static void registerListener(LibraryListener listener) {
        listeners.add(listener);
    }

    public static void unregisterListener(LibraryListener listener) {
        listeners.remove(listener);
    }

    /////////////////
    // Media Store //
    /////////////////

    public static void registerMediaStoreListeners() {
        for (Loader loader : mTasks) {
            loader.registerMediaStoreListener();
        }
    }

    public static void unregisterMediaStoreListeners() {
        for (Loader loader : mTasks) {
            loader.unregisterMediaStoreListener();
        }
    }

    //////////
    // Song //
    //////////

    public static void registerSongListener(Loader.TaskListener<Song> songListener) {
        if (mTasks[0] != null)
            mTasks[0].addListener(songListener);
    }

    public static void unregisterSongListener(Loader.TaskListener<Song> songListener) {
        if (mTasks[0] != null)
            mTasks[0].removeListener(songListener);
    }

    ///////////
    // Album //
    ///////////

    public static void registerAlbumListener(Loader.TaskListener<Album> albumListener) {
        if (mTasks[1] != null)
            mTasks[1].addListener(albumListener);
    }

    public static void unregisterAlbumListener(Loader.TaskListener<Album> albumListener) {
        if (mTasks[1] != null)
            mTasks[1].removeListener(albumListener);
    }

    //////////////
    // Playlist //
    //////////////

    public static void registerPlaylistListener(Loader.TaskListener<Playlist> playlistListener) {
        if (mTasks[2] != null)
            mTasks[2].addListener(playlistListener);
    }

    public static void unregisterPlaylistListener(Loader.TaskListener<Playlist> playlistListener) {
        if (mTasks[2] != null)
            mTasks[2].removeListener(playlistListener);
    }

    ////////////
    // Artist //
    ////////////

    public static void registerArtistListener(Loader.TaskListener<Artist> artistListener) {
        if (mTasks[3] != null)
            mTasks[3].addListener(artistListener);
    }

    public static void unregisterArtistListener(Loader.TaskListener<Artist> artistListener) {
        if (mTasks[3] != null)
            mTasks[3].removeListener(artistListener);
    }

    ////////////
    // Genres //
    ////////////

    public static void registerGenresListener(Loader.TaskListener<Genre> genreListener) {
        if (mTasks[4] != null)
            mTasks[4].addListener(genreListener);
    }

    public static void unregisterGenresListener(Loader.TaskListener<Genre> genreListener) {
        if (mTasks[4] != null)
            mTasks[4].removeListener(genreListener);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Getters
    ///////////////////////////////////////////////////////////////////////////

    public static List<Song> getSongs() {
        return mSongs;
    }

    public static List<Album> getAlbums() {
        return mAlbums;
    }

    public static List<Playlist> getPlaylists() {
        return mPlaylists;
    }

    public static List<Artist> getArtists() {
        return mArtists;
    }

    public static List<Genre> getGenres() {
        return mGenres;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Methods for finding a media object by ID
    ///////////////////////////////////////////////////////////////////////////

    @Nullable
    public static Song findSongById(long id) {
        for (Song song : getSongs()) if (song.getID() == id) return song;
        return null;
    }

    @Nullable
    public static Song findSongByUri(Uri uri) {
        for (Song song : getSongs()) if (song.getUri() == uri) return song;
        return null;
    }

    @Nullable
    public static Album findAlbumById(long id) {
        for (Album album : getAlbums()) if (album.getID() == id) return album;
        return null;
    }

    @Nullable
    public static Playlist findPlaylistById(long id) {
        for (Playlist playlist : getPlaylists())
            if (playlist.getID() == id) return playlist;
        return null;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Find X for Y
    ///////////////////////////////////////////////////////////////////////////

    public interface QueryResult<T> {

        void onQueryResult(T result);

    }

    public static class QueryTask<Result> extends AsyncTask<Callable<Result>, Void, Result> {
        private QueryResult<Result> callback;

        public QueryTask(QueryResult<Result> callback) {
            this.callback = callback;
        }

        @SafeVarargs
        @Override
        protected final Result doInBackground(Callable<Result>... params) {
            try {
                return params[0].call();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Result result) {
            callback.onQueryResult(result);
        }
    }

    public static List<Song> findSongsForArtist(Artist artist) {
        List<Song> songs = new ArrayList<>();
        for (Song song : getSongs()) {
            if (song.getSongArtistId() == artist.getID())
                songs.add(song);
        }
        return songs;
    }

    @SuppressWarnings("unchecked")
    public static void findSongsForArtistAsync(final Artist artist, QueryResult<List<Song>> callback) {
        new QueryTask<>(callback).execute(new Callable<List<Song>>() {
            @Override
            public List<Song> call() throws Exception {
                return findSongsForArtist(artist);
            }
        });
    }

    public static List<Album> findAlbumsForArtist(Artist artist) {
        List<Album> albums = new ArrayList<>();
        for (Album album : getAlbums()) {
            if (album.getAlbumArtistName().equals(artist.getArtistName()))
                albums.add(album);
        }
        return albums;
    }

    @SuppressWarnings("unchecked")
    public static void findAlbumsForArtistAsync(final Artist artist, QueryResult<List<Album>> callback) {
        new QueryTask<>(callback).execute(new Callable<List<Album>>() {
            @Override
            public List<Album> call() throws Exception {
                return findAlbumsForArtist(artist);
            }
        });
    }

    public static List<Song> findSongsForAlbum(Album album) {
        List<Song> songs = new ArrayList<>();
        for (Song song : getSongs()) {
            if (song.getSongAlbumID() == album.getID())
                songs.add(song);
        }
        return songs;
    }

    @SuppressWarnings("unchecked")
    public static void findSongsForAlbumAsync(final Album album, QueryResult<List<Song>> callback) {
        new QueryTask<>(callback).execute(new Callable<List<Song>>() {
            @Override
            public List<Song> call() throws Exception {
                return findSongsForAlbum(album);
            }
        });
    }

    @Nullable
    public static Artist findArtistForAlbum(Album album) {
        for (Artist artist : getArtists()) {
            if (artist.getArtistName().equals(album.getAlbumArtistName()))
                return artist;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static void findArtistForAlbumAsync(final Album album, QueryResult<Artist> callback) {
        new QueryTask<>(callback).execute(new Callable<Artist>() {
            @Override
            public Artist call() throws Exception {
                return findArtistForAlbum(album);
            }
        });
    }

    public static List<Song> findSongsForPlaylist(Context context, Playlist playlist) throws NullPointerException {
        List<Song> songs = new ArrayList<>();
        try {
            Cursor playlistSongsCursor = context.getContentResolver().query(MediaStore.Audio.Playlists.Members.getContentUri("external", playlist.getID()),
                    null, null, null,
                    MediaStore.Audio.Playlists.Members.PLAY_ORDER);
            int idColumn = playlistSongsCursor.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID);
            playlistSongsCursor.moveToFirst();
            do {
                Song s = findSongById(playlistSongsCursor.getLong(idColumn));
                if (s != null) songs.add(s);
            } while (playlistSongsCursor.moveToNext());
            playlistSongsCursor.close();
            return songs;
        } catch (IndexOutOfBoundsException e) {
            return Collections.emptyList();
        }
    }

    @SuppressWarnings("unchecked")
    public static void findSongsForPlaylistAsync(final Context context, final Playlist playlist, QueryResult<List<Song>> callback) {
        new QueryTask<>(callback).execute(new Callable<List<Song>>() {
            @Override
            public List<Song> call() throws Exception {
                return findSongsForPlaylist(context, playlist);
            }
        });
    }

    public static List<Song> findSongsForGenre(Context context, Genre genre) throws NullPointerException {
        List<Song> songs = new ArrayList<>();
        try {
            Cursor genreSongsCursor = context.getContentResolver().query(MediaStore.Audio.Genres.Members.getContentUri("external", genre.getID()),
                    null, null, null, null);
            int idColumn = genreSongsCursor.getColumnIndex(MediaStore.Audio.Genres.Members.AUDIO_ID);
            genreSongsCursor.moveToFirst();
            do {
                Song s = findSongById(genreSongsCursor.getLong(idColumn));
                if (s != null) {
                    songs.add(s);
                    Log.d("GENRE: " + genre.getGenreName(), "Found Song: " + s.getSongTitle());
                }
            } while (genreSongsCursor.moveToNext());
            genreSongsCursor.close();
            return songs;
        } catch (IndexOutOfBoundsException e) {
            return Collections.emptyList();
        }
    }

    @SuppressWarnings("unchecked")
    public static void findSongsForGenreAsync(final Context context, final Genre genre, QueryResult<List<Song>> callback) {
        new QueryTask<>(callback).execute(new Callable<List<Song>>() {
            @Override
            public List<Song> call() throws Exception {
                return findSongsForGenre(context, genre);
            }
        });
    }

    ///////////////////////////////////////////////////////////////////////////
    // Search
    ///////////////////////////////////////////////////////////////////////////

    public static SearchResult filterAlbums(String query) {
        List<Album> results = new ArrayList<>();
        for (Album a : getAlbums()) {
            if (a.getAlbumName().toLowerCase().contains(query.toLowerCase())) {
                if (!results.contains(a)) results.add(a);
            }

            if (a.getAlbumArtistName().toLowerCase().contains(query.toLowerCase())) {
                if (!results.contains(a)) results.add(a);
            }
        }
        return new SearchResult(context.getString(R.string.albums), results);
    }

    public static SearchResult filterSongs(String query) {
        List<Song> results = new ArrayList<>();
        for (Song s : getSongs()) {
            if (s.getSongTitle().toLowerCase().contains(query.toLowerCase())) {
                if (!results.contains(s)) results.add(s);
            }

            if (s.getSongArtistName().toLowerCase().contains(query.toLowerCase())) {
                if (!results.contains(s)) results.add(s);
            }
        }
        return new SearchResult(context.getString(R.string.songs), results);
    }

    public static SearchResult filterPlaylists(String query) {
        List<Playlist> results = new ArrayList<>();
        for (Playlist p : getPlaylists()) {
            if (p.getPlaylistName().toLowerCase().contains(query.toLowerCase())) {
                if (!results.contains(p)) results.add(p);
            }
        }
        return new SearchResult(context.getString(R.string.playlists), results);
    }

    public static List<SearchResult> search(String query) {
        List<SearchResult> output = new ArrayList<>();
        filterAlbums(query).addIfNotEmpty(output);
        filterSongs(query).addIfNotEmpty(output);
        filterPlaylists(query).addIfNotEmpty(output);
        return Collections.unmodifiableList(output);
    }


}
