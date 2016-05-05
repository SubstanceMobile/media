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

package mobile.substance.sdk.music.loading.tasks;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;

import mobile.substance.sdk.music.core.objects.Song;
import mobile.substance.sdk.music.loading.Library;

/**
 * Created by Adrian on 3/25/2016.
 */
public class SongsTask extends Loader<Song> {

    public SongsTask(Context context, Object... params) {
        super(context, params);
    }

    @Override
    protected Song buildObject(@NonNull Cursor cursor) {
        String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
        String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
        long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
        long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
        long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
        long trackNumber = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.TRACK));
        String albumName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
        long artistId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID));
        String year = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.YEAR));

        Song s = new Song.Builder()
                .setTitle(title)
                .setArtistName(artist)
                .setArtistId(artistId)
                .setAlbumName(albumName)
                .setAlbumId(albumId)
                .setTrackNumber(trackNumber)
                .setYear(year == null ? "0000" : year)
                .setDuration(duration)
                .build();

        Log.i("SongsTask", "Loaded ID " + id);
        return s;
    }

    @Override
    protected Uri getUri() {
        return MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    }

    @Override
    protected String getSelection() {
        return MediaStore.Audio.Media.IS_MUSIC + "=1";
    }

    @Override
    protected String getSortOrder() {
        return MediaStore.Audio.Media.DEFAULT_SORT_ORDER;
    }

    @Override
    protected ContentObserver getObserver() {
        return new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                update(Library.getSongs());
            }
        };
    }
}
