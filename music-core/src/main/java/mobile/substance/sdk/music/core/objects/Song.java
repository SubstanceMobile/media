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

import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.media.MediaBrowserCompat;

import mobile.substance.sdk.music.core.CoreUtil;

import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ALBUM;
import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ALBUM_ART;
import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_ARTIST;
import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_DURATION;
import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_TITLE;
import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER;
import static android.support.v4.media.MediaMetadataCompat.METADATA_KEY_YEAR;

/**
 * Wrapper around a MediaMetadataCompat optimised for Song metadata
 */
public class Song extends MediaObject {
    private long songAlbumId, songArtistId;
    private String additionalPath;

    @Override
    protected Uri getBaseUri() {
        return MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Title
    ///////////////////////////////////////////////////////////////////////////

    public String getSongTitle() {
        return data.getString(METADATA_KEY_TITLE);
    }

    public void setSongTitle(String songTitle) {
        putString(METADATA_KEY_TITLE, songTitle);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Album
    ///////////////////////////////////////////////////////////////////////////

    public String getSongAlbumName() {
        return data.getString(METADATA_KEY_ALBUM);
    }

    public void setSongAlbumName(String songAlbum) {
        putString(METADATA_KEY_ALBUM, songAlbum);
    }

    public long getSongAlbumID() {
        return songAlbumId;
    }

    public void setSongAlbumID(long albumID) {
        this.songAlbumId = albumID;
    }

    public void embedArtwork(Bitmap bitmap) {
        putBitmap(METADATA_KEY_ALBUM_ART, bitmap);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Duration
    ///////////////////////////////////////////////////////////////////////////

    public long getSongDuration() {
        return data.getLong(METADATA_KEY_DURATION);
    }

    public void setSongDuration(long songDuration) {
        putLong(METADATA_KEY_DURATION, songDuration);
    }

    public String getSongDurString() {
        return CoreUtil.stringForTime(getSongDuration());
    }

    ///////////////////////////////////////////////////////////////////////////
    // Track number
    ///////////////////////////////////////////////////////////////////////////

    public String getTrackNumberString() {
        long track = data.getLong(METADATA_KEY_TRACK_NUMBER);
        return track != 0 ? String.valueOf(track) : "-";
    }

    public Song setSongTrackNumber(long trackNumber) {
        putLong(METADATA_KEY_TRACK_NUMBER, trackNumber);
        return this;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Year
    ///////////////////////////////////////////////////////////////////////////

    public String getSongYear() {
        return String.valueOf(data.getLong(METADATA_KEY_YEAR));
    }

    public void setSongYear(String year) {
        putLong(METADATA_KEY_YEAR, Long.valueOf(year));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Artist
    ///////////////////////////////////////////////////////////////////////////

    public long getSongArtistId() {
        return songArtistId;
    }

    public void setSongArtistId(long songArtistId) {
        this.songArtistId = songArtistId;
    }

    public String getSongArtistName() {
        return data.getString(METADATA_KEY_ARTIST);
    }

    public void setSongArtistName(String songArtist) {
        putString(METADATA_KEY_ARTIST, songArtist);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Additional Song path
    ///////////////////////////////////////////////////////////////////////////


    @Override
    public String getFilePath() {
        return additionalPath == null ? super.getFilePath() : additionalPath;
    }

    public void setAdditionalPath(String additionalPath) {
        this.additionalPath = additionalPath;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Builder and Other Classes
    ///////////////////////////////////////////////////////////////////////////

    @Override
    protected boolean isContextRequired() {
        return true;
    }

    public MediaBrowserCompat.MediaItem toMediaItem() {
        return new MediaBrowserCompat.MediaItem(data.getDescription(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE);
    }

    public static class Builder {
        private Song song;

        public Builder() {
            this(new Song());
        }

        public Builder(Song copy) {
            this.song = copy;
        }

        public Builder setId(long id) {
            this.song.setID(id);
            return this;
        }

        public Builder setTitle(String title) {
            this.song.setSongTitle(title);
            return this;
        }

        public Builder setArtistName(String artistName) {
            this.song.setSongArtistName(artistName);
            return this;
        }

        public Builder setAlbumName(String albumName) {
            this.song.setSongAlbumName(albumName);
            return this;
        }

        public Builder setAlbumId(long albumId) {
            this.song.setSongAlbumID(albumId);
            return this;
        }

        public Builder setArtistId(long artistId) {
            this.song.setSongArtistId(artistId);
            return this;
        }

        public Builder setYear(String year) {
            this.song.setSongYear(year);
            return this;
        }

        public Builder setTrackNumber(long trackNumber) {
            this.song.setSongTrackNumber(trackNumber);
            return this;
        }

        public Builder setDuration(long duration) {
            this.song.setSongDuration(duration);
            return this;
        }

        public Song build() {
            return song;
        }

    }

}
