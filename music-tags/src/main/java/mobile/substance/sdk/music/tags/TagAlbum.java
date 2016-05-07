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

import java.util.List;

/**
 * Created by Julian Os on 03.05.2016.
 */
public class TagAlbum {
    private List<TagSong> songs;
    private String title, artist, genre, year, label, comment;
    private Artwork artwork;

    public String getTitle() {
        return title;
    }

    public TagAlbum setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getArtist() {
        return artist;
    }

    public TagAlbum setArtist(String artist) {
        this.artist = artist;
        return this;
    }

    public String getGenre() {
        return genre;
    }

    public TagAlbum setGenre(String genre) {
        this.genre = genre;
        return this;
    }

    public String getYear() {
        return year;
    }

    public TagAlbum setYear(String year) {
        this.year = year;
        return this;
    }

    public String getLabel() {
        return label;
    }

    public TagAlbum setLabel(String label) {
        this.label = label;
        return this;
    }

    public String getComment() {
        return comment;
    }

    public TagAlbum setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public Artwork getArtwork() {
        return artwork;
    }

    public TagAlbum setArtwork(Artwork artwork) {
        this.artwork = artwork;
        return this;
    }

    public List<TagSong> getSongs() {
        return songs;
    }

    public TagAlbum setSongs(List<TagSong> songs) {
        this.songs = songs;
        return this;
    }
}
