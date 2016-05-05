package mobile.substance.sdk.music.loading.tasks;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import mobile.substance.sdk.music.core.objects.Genre;
import mobile.substance.sdk.music.loading.Library;

/**
 * Created by Julian Os on 05.05.2016.
 */
public class GenresTask extends Loader<Genre> {

    public GenresTask(Context context, Object... params) {
        super(context, params);
    }

    @Nullable
    @Override
    protected Genre buildObject(@NonNull Cursor cursor) {
        String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Genres.NAME));
        long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Genres._ID));

        Genre genre = new Genre.Builder()
                .setGenreName(name)
                .setGenreId(id)
                .build();

        return genre;
    }

    @Override
    protected Uri getUri() {
        return MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI;
    }

    @Nullable
    @Override
    protected ContentObserver getObserver() {
        return new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                update(Library.getGenres());
            }
        };
    }
}
