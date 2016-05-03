package mobile.substance.sdk.music.tags;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.io.File;

import mobile.substance.sdk.music.core.objects.Song;

/**
 * Created by Julian Os on 03.05.2016.
 */
public class TagHelper {

    public static TagSong read(Context context, Song song) {
        Tag tag = null;
        try {
            tag = AudioFileIO.read(new File(getFileUri(context, song.getUri()).getPath())).getTag();
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
                .setDiskNo(tag.getFirst(FieldKey.DISC_NO));
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
}
