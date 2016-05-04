package mobile.substance.sdk.music.tags;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.tag.images.ArtworkFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mobile.substance.sdk.music.core.objects.Album;
import mobile.substance.sdk.music.core.objects.Playlist;
import mobile.substance.sdk.music.core.objects.Song;

public class TagHelper {

    public static TagSong read(Context context, Song song) {
        Tag tag = null;
        String filePath = getFileUri(context, song.getUri()).getPath();
        try {
            tag = AudioFileIO.read(new File(filePath)).getTag();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return new TagSong()
                .setTitle(tag.getFirst(FieldKey.TITLE))
                .setArtist(tag.getFirst(FieldKey.ARTIST))
                .setAlbum(tag.getFirst(FieldKey.ALBUM))
                .setGenre(tag.getFirst(FieldKey.GENRE))
                .setYear(tag.getFirst(FieldKey.YEAR))
                .setComment(tag.getFirst(FieldKey.COMMENT))
                .setLabel(tag.getFirst(FieldKey.RECORD_LABEL))
                .setDiskNo(tag.getFirst(FieldKey.DISC_NO))
                .setPath(filePath)
                .setLyrics(tag.getFirst(FieldKey.LYRICS));
    }

    public static TagAlbum read(Context context, Album album) {
        List<TagSong> songs = new ArrayList<>();
        for (Song song : album.getSongs()) {
            songs.add(read(context, song));
        }

        Artwork artwork = null;
        try {
            artwork = ArtworkFactory.createArtworkFromFile(new File(album.getAlbumArtPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new TagAlbum()
                .setTitle(album.getTitle())
                .setArtist(album.getAlbumArtistName())
                //.setGenre(album.getGenre())
                //.setYear(album.getYear())
                .setArtwork(artwork)
                .setSongs(songs);
    }

    public static void readAsync(Context context, Song song, SongReadCallback callback) {
        new AsyncTagSong(context, callback).execute(song);
    }

    public static void readAsync(Context context, Album album, AlbumReadCallback callback) {
        new AsyncTagAlbum(context, callback).execute(album);
    }

    public static Uri getFileUri(Context context, Uri uri) {
        return Uri.parse(getPath(context, uri));
    }

    private static String getPath(final Context context, final Uri uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private Playlist createPlaylist(Context context, String name) {
        if (name == null || name.length() <= 0) {
            return null;
        } else {
            try {
                Cursor mQuery = context.getContentResolver().query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, new String[]{"_id"}, "name=?", new String[]{name}, null);
                if (mQuery == null || mQuery.getCount() < 1) {
                    ContentValues mValues = new ContentValues(1);
                    mValues.put(MediaStore.Audio.Playlists.NAME, name);
                    Uri mInsert = context.getContentResolver().insert(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, mValues);
                    if (mQuery != null) {
                        mQuery.close();
                    }
                    if (mInsert != null) {
                        context.getContentResolver().notifyChange(Uri.parse("content://media/audio/playlists"), null);
                        Playlist p = new Playlist();
                        p.setName(name);
                        p.setId(Integer.valueOf(mInsert.getLastPathSegment()));
                        p.setSongs(Collections.<Song>emptyList());
                        //p.setType(TYPE);
                        return p;
                    } else return null;
                } else return null;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    interface AlbumReadCallback {

        void onReadFinished(TagAlbum album);

    }

    interface SongReadCallback {

        void onReadFinished(TagSong song);

    }

    static class AsyncTagAlbum extends AsyncTask<Album, Void, TagAlbum> {
        private Context context;
        private AlbumReadCallback callback;

        public AsyncTagAlbum(Context context, AlbumReadCallback callback) {
            this.context = context;
            this.callback = callback;
        }

        @Override
        protected TagAlbum doInBackground(Album... params) {
            return read(context, params[0]);
        }

        @Override
        protected void onPostExecute(TagAlbum tagAlbum) {
            callback.onReadFinished(tagAlbum);
        }
    }

    static class AsyncTagSong extends AsyncTask<Song, Void, TagSong> {
        private Context context;
        private SongReadCallback callback;

        public AsyncTagSong(Context context, SongReadCallback callback) {
            this.context = context;
            this.callback = callback;
        }

        @Override
        protected TagSong doInBackground(Song... params) {
            return read(context, params[0]);
        }

        @Override
        protected void onPostExecute(TagSong tagSong) {
            callback.onReadFinished(tagSong);
        }
    }
}
