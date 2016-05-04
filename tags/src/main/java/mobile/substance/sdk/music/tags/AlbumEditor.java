package mobile.substance.sdk.music.tags;

import android.content.Context;
import android.net.Uri;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.tag.images.ArtworkFactory;

import java.io.File;
import java.io.IOException;

public class AlbumEditor {
    private Context context;
    private String title, artist, genre, year, comment, label;
    private Artwork artwork;
    private TagAlbum album;

    public AlbumEditor(Context context, TagAlbum album) {
        this.context = context;
        this.album = album;
    }

    public AlbumEditor setTitle(String title) {
        this.title = title;
        return this;
    }

    public AlbumEditor setArtist(String artist) {
        this.artist = artist;
        return this;
    }

    public AlbumEditor setGenre(String genre) {
        this.genre = genre;
        return this;
    }

    public AlbumEditor setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public AlbumEditor setYear(String year) {
        this.year = year;
        return this;
    }

    public AlbumEditor setLabel(String label) {
        this.label = label;
        return this;
    }

    public AlbumEditor setArtwork(File file) {
        try {
            this.artwork = ArtworkFactory.createArtworkFromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public AlbumEditor setArtwork(Context context, Uri uri) {
        try {
            this.artwork = ArtworkFactory.createArtworkFromFile(new File(TagHelper.getFileUri(context, uri).getPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public boolean commit() {
        return write();
    }

    public boolean commitAndUpdateMediaStore(MediaStoreCallbacks mediaStoreCallbacks) {
        if (write()) {
            String[] paths = new String[album.getSongs().size()];
            for (int i = 0; i < album.getSongs().size(); i++) {
                paths[i] = album.getSongs().get(i).getPath();
            }
            MediaStoreHelper.updateMedia(paths, context, mediaStoreCallbacks);
            return true;
        } else return false;
    }


    private boolean write() {
        for (TagSong song : album.getSongs()) {
            if (!writeTags(song)) {
                return false;
            }
        }
        return true;
    }

    private boolean writeTags(TagSong song) {
        AudioFile file = null;
        try {
            file = new AudioFileIO().readFile(new File(song.getPath()));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        Tag tag = file.getTag();

        try {
            tag.setField(artwork == null ? album.getArtwork() : artwork);
        } catch (FieldDataInvalidException e) {
            e.printStackTrace();
            return false;
        }

        if (applyField(tag, FieldKey.TITLE, title == null ? album.getTitle() : title)
                && applyField(tag, FieldKey.ARTIST, artist == null ? album.getArtist() : artist)
                && applyField(tag, FieldKey.YEAR, year == null ? album.getYear() : year)
                && applyField(tag, FieldKey.COMMENT, comment == null ? album.getComment() : comment)
                && applyField(tag, FieldKey.RECORD_LABEL, label == null ? album.getLabel() : label)
                && applyField(tag, FieldKey.GENRE, genre == null ? album.getGenre() : genre)) {
            try {
                file.commit();
                return true;
            } catch (CannotWriteException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    private boolean applyField(Tag tag, FieldKey key, String value) {
        if (value == null)
            return false;
        try {
            tag.setField(key, value);
            return true;
        } catch (FieldDataInvalidException e) {
            e.printStackTrace();
            return false;
        }
    }
}
