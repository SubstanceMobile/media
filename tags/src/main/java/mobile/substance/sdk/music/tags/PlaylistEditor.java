package mobile.substance.sdk.music.tags;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import mobile.substance.sdk.music.core.objects.Playlist;
import mobile.substance.sdk.music.core.objects.Song;

/**
 * Created by Julian Os on 04.05.2016.
 */
public class PlaylistEditor {
    public static final int MAX_PLAYLIST_ITEMS = 500;
    private Context context;
    private Playlist playlist;
    private String name;
    private List<Song> songsToRemove = new ArrayList<>();
    private List<Song> songsToAdd = new ArrayList<>();
    private boolean delete = false;
    private Pair<Integer, Integer> positions;


    public PlaylistEditor(Context context, Playlist playlist) {
        this.context = context;
        this.playlist = playlist;
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public PlaylistEditor setName(String name) {
        this.name = name;
        return this;
    }

    public PlaylistEditor remove(List<Song> songs) {
        this.songsToRemove.addAll(songs);
        return this;
    }

    public PlaylistEditor remove(Song song) {
        this.songsToRemove.add(song);
        return this;
    }

    public PlaylistEditor add(Song song) {
        this.songsToAdd.add(song);
        return this;
    }

    public PlaylistEditor add(List<Song> songs) {
        this.songsToAdd.addAll(songs);
        return this;
    }

    public PlaylistEditor moveSong(int fromPos, int toPos) {
        this.positions = new Pair<>(fromPos, toPos);
        return this;
    }

    public PlaylistEditor delete() {
        this.delete = true;
        return this;
    }

    public Playlist commit() {
        List<TagResult> results = new ArrayList<>();

        if (songsToAdd.size() > 0) results.add(addToPlaylist(songsToAdd));
        if (songsToRemove.size() > 0) results.add(removeFromPlaylist(songsToRemove));
        if (name != null) results.add(renamePlaylist(name));
        if (delete) results.add(deletePlaylist());
        if (positions != null) results.add(movePlaylistItem(positions.first, positions.second));

        Log.d(PlaylistEditor.class.getSimpleName(), String.valueOf(results));

        return playlist;
    }

    private TagResult addToPlaylist(List<Song> songs) {
        String[] s = new String[]{"max(play_order)"};
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlist.getId());
        try {
            Cursor query = context.getContentResolver().query(uri, s, null, null, null);
            if (query != null) {
                try {
                    if (query.moveToFirst()) {
                        int count = query.getInt(query.getColumnIndex(MediaStore.Audio.Playlists.Members._COUNT)) + 1;
                        query.close();
                        context.getContentResolver().bulkInsert(uri, newPlaylistMemberValues(songs, count));
                        playlist.addAll(songs);
                        return TagResult.SUCCESS;
                    } else return TagResult.ERR_ADD_FAILED;
                } catch (Throwable th) {
                    th.printStackTrace();
                    return TagResult.ERR_ADD_FAILED;
                }
            } else return TagResult.ERR_ADD_FAILED;
        } catch (Throwable th2) {
            th2.printStackTrace();
            return TagResult.ERR_ADD_FAILED;
        }
    }

    private TagResult deletePlaylist() {
        try {
            context.getContentResolver().delete(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, "_id IN (" + playlist.getId() + ")", null);
            playlist = null;
            return TagResult.SUCCESS;
        } catch (Exception ignored) {
            return TagResult.ERR_DELETE_FAILED;
        }
    }

    private ContentValues[] newPlaylistMemberValues(@NonNull List<Song> songs, int count) {
        ContentValues[] cv = new ContentValues[songs.size()];
        for (int i = 0; i < songs.size(); i++) {
            cv[i] = new ContentValues();
            cv[i].put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, count + i + 1);
            cv[i].put(MediaStore.Audio.Playlists.Members.AUDIO_ID, songs.get(i).getID());
        }
        return cv;
    }

    private TagResult movePlaylistItem(int fromPos, int toPos) {
        try {
            MediaStore.Audio.Playlists.Members.moveItem(context.getContentResolver(), playlist.getId(), fromPos, toPos);
            return TagResult.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return TagResult.ERR_MOVE_FAILED;
        }
    }

    private TagResult removeFromPlaylist(List<Song> songs) {
        try {
            for (Song song : songs) {
                context.getContentResolver().delete(MediaStore.Audio.Playlists.Members.getContentUri("external", playlist.getId()), "audio_id =?", new String[]{String.valueOf(song.getID())});
                playlist.removeSong(song);
            }
            return TagResult.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return TagResult.ERR_REMOVE_FAILED;
        }
    }


    private TagResult renamePlaylist(String newName) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Audio.Playlists.NAME, newName);
        try {
            context.getContentResolver().update(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, contentValues, "_id=?", new String[]{String.valueOf(playlist.getId())});
            context.getContentResolver().notifyChange(Uri.parse("content://media"), null);
            playlist.setName(newName);
            return TagResult.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return TagResult.ERR_NAME_UNAVAILABLE;
        }
    }


}
