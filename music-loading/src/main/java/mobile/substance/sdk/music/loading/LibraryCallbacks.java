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
public interface LibraryCallbacks {

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
