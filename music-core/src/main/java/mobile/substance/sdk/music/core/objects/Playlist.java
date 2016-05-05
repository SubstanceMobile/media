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

/**
 * Created by Adrian on 7/5/2015.
 */
public class Playlist extends MediaObject {
    private String playlistName;
    private int playlistType;

    public Playlist() {
    }

    @Override
    protected Uri getBaseUri() {
        return MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Manages the title of the playlist
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
        putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, playlistName);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Order
    ///////////////////////////////////////////////////////////////////////////

    public int getPlaylistType() {
        return playlistType;
    }

    public void setPlaylistType(int playlistType) {
        this.playlistType = playlistType;
    }

    public static class Builder {
        private Playlist playlist;

        public Builder() {
            this.playlist = new Playlist();
        }

        public Builder setName(String name) {
            this.playlist.setPlaylistName(name);
            return this;
        }

        public Builder setId(long id) {
            this.playlist.setID(id);
            return this;
        }

        public Builder setType(int type) {
            this.playlist.setPlaylistType(type);
            return this;
        }

        public Playlist build() {
            return playlist;
        }

    }

}
