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

import java.util.List;

import mobile.substance.sdk.music.core.objects.Album;
import mobile.substance.sdk.music.core.objects.Artist;
import mobile.substance.sdk.music.core.objects.Genre;
import mobile.substance.sdk.music.core.objects.Playlist;
import mobile.substance.sdk.music.core.objects.Song;

/**
 * Created by Julian Os on 05.05.2016.
 */
public interface LibraryListener {

    void onSongLoaded(Song item, int pos);

    void onSongsCompleted(List<Song> result);

    void onAlbumLoaded(Album item, int pos);

    void onAlbumsCompleted(List<Album> result);

    void onArtistLoaded(Artist item, int pos);

    void onArtistsCompleted(List<Artist> result);

    void onPlaylistLoaded(Playlist item, int pos);

    void onPlaylistsCompleted(List<Playlist> result);

    void onGenreLoaded(Genre item, int pos);

    void onGenresCompleted(List<Genre> result);

}
