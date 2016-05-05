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

package mobile.substance.sdk.music.core.objects;

import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.media.MediaMetadataCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adrian on 7/5/2015.
 */
public class Artist extends MediaObject {
    private String artistName, artistBio, artistImagePath;
    private long artistId;
    private List<Album> artistAlbums = new ArrayList<>();
    private List<Song> artistSongs = new ArrayList<>();

    public Artist() {

    }

    @Override
    protected Uri getBaseUri() {
        return MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //This manages the strings
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
        putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, artistName);
    }

    public String getArtistBio() {
        return artistBio;
    }

    public void setArtistBio(String artistBio) {
        this.artistBio = artistBio;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //This manages the image
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public String getArtistImagePath() {
        return artistImagePath;
    }

    public void setArtistImagePath(String artistImagePath) {
        this.artistImagePath = artistImagePath;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //This manages all of the lists
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public List<Album> getArtistAlbums() {
        return artistAlbums;
    }

    public void setArtistAlbums(List<Album> artistAlbums) {
        this.artistAlbums = artistAlbums;
    }

    public List<Song> getArtistSongs() {
        return artistSongs;
    }

    public void setArtistSongs(List<Song> artistSongs) {
        this.artistSongs = artistSongs;
    }

    public long getArtistId() {
        return artistId;
    }

    public void setArtistId(long artistId) {
        this.artistId = artistId;
    }

    public static class Builder {
        private Artist artist;

        public Builder() {
            this.artist = new Artist();
        }

        public Builder setName(String name) {
            this.artist.setArtistName(name);
            return this;
        }

        public Builder setBio(String bio) {
            this.artist.setArtistBio(bio);
            return this;
        }

        public Builder setId(long id) {
            this.artist.setArtistId(id);
            return this;
        }

        public Builder setSongs(List<Song> songs) {
            this.artist.setArtistSongs(songs);
            return this;
        }

        public Builder setAlbums(List<Album> albums) {
            this.artist.setArtistAlbums(albums);
            return this;
        }

        public Artist build() {
            return artist;
        }

    }

}
