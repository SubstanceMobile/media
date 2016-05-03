package mobile.substance.sdk.music.tags;

import android.content.Context;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;

import java.io.File;

public class SongEditor {
    private Context context;
    private String title, artist, album, genre, diskNo, year, comment, label;
    private Artwork artwork;
    private TagSong song;

    public SongEditor(Context context, TagSong song) {
        this.context = context;
        this.song = song;
    }

    public SongEditor setTitle(String title) {
        this.title = title;
        return this;
    }

    public SongEditor setArtist(String artist) {
        this.artist = artist;
        return this;
    }

    public SongEditor setAlbum(String album) {
        this.album = album;
        return this;
    }

    public SongEditor setGenre(String genre) {
        this.genre = genre;
        return this;
    }

    public SongEditor setDiskNo(String diskNo) {
        this.diskNo = diskNo;
        return this;
    }

    public SongEditor setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public SongEditor setYear(String year) {
        this.year = year;
        return this;
    }

    public SongEditor setLabel(String label) {
        this.label = label;
        return this;
    }

    public SongEditor setArtwork(Artwork artwork) {
        this.artwork = artwork;
        return this;
    }

    public boolean commit() {
        AudioFile file = null;
        try {
            file = new AudioFileIO().readFile(new File(song.getPath()));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        Tag tag = file.getTag();

        try {
            tag.setField(artwork == null ? song.getArtwork() : artwork);
        } catch (FieldDataInvalidException e) {
            e.printStackTrace();
            return false;
        }

        return applyField(tag, FieldKey.TITLE, title == null ? song.getTitle() : title)
                && applyField(tag, FieldKey.ARTIST, artist == null ? song.getArtist() : artist)
                && applyField(tag, FieldKey.ALBUM, album == null ? song.getAlbum() : album)
                && applyField(tag, FieldKey.DISC_NO, diskNo == null ? song.getDiskNo() : diskNo)
                && applyField(tag, FieldKey.YEAR, year == null ? song.getYear() : year)
                && applyField(tag, FieldKey.COMMENT, comment == null ? song.getComment() : comment)
                && applyField(tag, FieldKey.RECORD_LABEL, label == null ? song.getLabel() : label)
                && applyField(tag, FieldKey.GENRE, genre == null ? song.getGenre() : genre);
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
