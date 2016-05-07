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

public class SongEditor {
    private Context context;
    private String title, artist, album, genre, diskNo, year, comment, label, lyrics;
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

    public SongEditor setLyrics(String lyrics) {
        this.lyrics = lyrics;
        return this;
    }

    public SongEditor setArtwork(File file) {
        try {
            this.artwork = ArtworkFactory.createArtworkFromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public SongEditor setArtwork(Context context, Uri uri) {
        try {
            this.artwork = ArtworkFactory.createArtworkFromFile(new File(TagHelper.getFileUri(context, uri).getPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public boolean commit() {
        return writeTags();
    }

    public boolean commitAndUpdateMediaStore(MediaStoreCallbacks mediaStoreCallbacks) {
        if (writeTags()) {
            MediaStoreHelper.updateMedia(new String[]{song.getPath()}, context, mediaStoreCallbacks);
            return true;
        } else return false;
    }


    private boolean writeTags() {
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

        if (applyField(tag, FieldKey.TITLE, title == null ? song.getTitle() : title)
                && applyField(tag, FieldKey.ARTIST, artist == null ? song.getArtist() : artist)
                && applyField(tag, FieldKey.ALBUM, album == null ? song.getAlbum() : album)
                && applyField(tag, FieldKey.DISC_NO, diskNo == null ? song.getDiskNo() : diskNo)
                && applyField(tag, FieldKey.YEAR, year == null ? song.getYear() : year)
                && applyField(tag, FieldKey.COMMENT, comment == null ? song.getComment() : comment)
                && applyField(tag, FieldKey.RECORD_LABEL, label == null ? song.getLabel() : label)
                && applyField(tag, FieldKey.GENRE, genre == null ? song.getGenre() : genre)
                && applyField(tag, FieldKey.LYRICS, lyrics == null ? song.getLyrics() : lyrics)) {
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
