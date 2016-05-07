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

import org.jaudiotagger.tag.images.Artwork;

/**
 * Created by Julian Os on 03.05.2016.
 */
public class TagSong {
    private String title, artist, album, genre, year, comment, label, diskNo, path, lyrics;
    private Artwork artwork;


    public String getTitle() {
        return title;
    }

    public TagSong setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getArtist() {
        return artist;
    }

    public TagSong setArtist(String artist) {
        this.artist = artist;
        return this;
    }

    public String getAlbum() {
        return album;
    }

    public TagSong setAlbum(String album) {
        this.album = album;
        return this;
    }

    public String getGenre() {
        return genre;
    }

    public TagSong setGenre(String genre) {
        this.genre = genre;
        return this;
    }

    public String getYear() {
        return year;
    }

    public TagSong setYear(String year) {
        this.year = year;
        return this;
    }

    public String getComment() {
        return comment;
    }

    public TagSong setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public String getLabel() {
        return label;
    }

    public TagSong setLabel(String label) {
        this.label = label;
        return this;
    }

    public String getDiskNo() {
        return diskNo;
    }

    public TagSong setDiskNo(String diskNo) {
        this.diskNo = diskNo;
        return this;
    }

    public Artwork getArtwork() {
        return artwork;
    }

    public TagSong setArtwork(Artwork artwork) {
        this.artwork = artwork;
        return this;
    }

    public String getPath() {
        return path;
    }

    public TagSong setPath(String path) {
        this.path = path;
        return this;
    }

    public String getLyrics() {
        return lyrics;
    }

    public TagSong setLyrics(String lyrics) {
        this.lyrics = lyrics;
        return this;
    }
}
