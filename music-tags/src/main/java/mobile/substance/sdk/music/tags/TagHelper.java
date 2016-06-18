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

package mobile.substance.sdk.music.tags;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.io.File;

import mobile.substance.sdk.music.core.objects.Album;
import mobile.substance.sdk.music.core.objects.Playlist;
import mobile.substance.sdk.music.core.objects.Song;
import mobile.substance.sdk.music.core.utils.CoreUtil;

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

    //public static TagAlbum read(Context context, Album album) {
    //    List<TagSong> songs = new ArrayList<>();
    //    for (Song song : album.getSongs()) {
    //        songs.add(read(context, song));
    //    }
//
    //      Artwork artwork = null;
    //    try {
    //      artwork = ArtworkFactory.createArtworkFromFile(new File(album.getAlbumArtworkPath()));
    //  } catch (IOException e) {
    //      e.printStackTrace();
    //  }
//
    //      return new TagAlbum()
    //            .setTitle(album.getTitle())
    //          .setArtist(album.getAlbumArtistName())
    //          //.setGenre(album.getGenre())
    //        //.setYear(album.getYear())
    //      .setArtwork(artwork)
    //    .setSongs(songs);
    //}

    public static void readAsync(Context context, Song song, SongReadCallback callback) {
        new AsyncTagSong(context, callback).execute(song);
    }

    public static void readAsync(Context context, Album album, AlbumReadCallback callback) {
        new AsyncTagAlbum(context, callback).execute(album);
    }

    public static Uri getFileUri(Context context, Uri uri) {
        return Uri.parse(CoreUtil.INSTANCE.getFilePath(context, uri));
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
                        p.setPlaylistName(name);
                        p.setId(Integer.valueOf(mInsert.getLastPathSegment()));
                        //p.setFavorites(TYPE);
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
            return null; //
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
