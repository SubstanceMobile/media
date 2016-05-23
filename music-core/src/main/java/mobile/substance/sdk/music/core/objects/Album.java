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
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import mobile.substance.sdk.music.core.MusicCoreOptions;

public class Album extends MediaObject {

    @Override
    protected Uri getBaseUri() {
        return MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Title
    ///////////////////////////////////////////////////////////////////////////

    public String getAlbumName() {
        return data.getString(MediaMetadataCompat.METADATA_KEY_TITLE);
    }

    public void setAlbumName(String albumName) {
        putString(MediaMetadataCompat.METADATA_KEY_TITLE, albumName);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Album Art
    ///////////////////////////////////////////////////////////////////////////

    private String albumArtworkPath;
    private Uri albumArtworkUri;

    public void setAlbumArtworkPath(String artworkPath) {
        this.albumArtworkPath = artworkPath;
        this.albumArtworkUri = Uri.parse("file://" + artworkPath);
        if (artworkPath != null) {
            setAnimated(false);
        } else {
            setAnimated(true);
        }
    }

    public String getAlbumArtworkPath() {
        return albumArtworkPath == null ? "" : albumArtworkPath;
    }

    public Uri getAlbumArtworkUri() {
        return albumArtworkUri;
    }

    public interface ArtRequest {
        void respond(Bitmap albumArt);
    }

    public void requestArt(final ArtRequest request) {
        Glide.with(getContext()).load(getAlbumArtworkPath())
                .asBitmap()
                .placeholder(MusicCoreOptions.getDefaultArt())
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .animate(android.R.anim.fade_in)
                .centerCrop()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        request.respond(resource);
                    }
                });
        Log.d("requestArt(ArtRequest)", getAlbumArtworkPath());
    }

    public void requestArt(ImageView imageView) {
        Glide.with(getContext()).load(getAlbumArtworkPath())
                .placeholder(MusicCoreOptions.getDefaultArt())
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .crossFade()
                .centerCrop()
                .into(imageView);
        Log.d("requestArt(ImageView)", getAlbumArtworkPath());
    }

    ///////////////////////////////////////////////////////////////////////////
    // Artist
    ///////////////////////////////////////////////////////////////////////////

    public String getAlbumArtistName() {
        return data.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST);
    }

    public void setAlbumArtistName(String albumArtistName) {
        putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, albumArtistName);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Genre
    ///////////////////////////////////////////////////////////////////////////

    public String getAlbumGenreName() {
        return data.getString(MediaMetadataCompat.METADATA_KEY_GENRE);
    }

    public void setAlbumGenreName(String albumGenreName) {
        putString(MediaMetadataCompat.METADATA_KEY_GENRE, albumGenreName);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Year
    ///////////////////////////////////////////////////////////////////////////

    public long getAlbumYear() {
        return data.getLong(MediaMetadataCompat.METADATA_KEY_YEAR);
    }

    public void setAlbumYear(long albumYear) {
        putLong(MediaMetadataCompat.METADATA_KEY_YEAR, albumYear);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Number Of Songs
    ///////////////////////////////////////////////////////////////////////////

    public long getAlbumNumberOfSongs() {
        return data.getLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS);
    }

    public void setAlbumNumberOfSongs(long albumNumberOfSongs) {
        putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, albumNumberOfSongs);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Context behavior
    ///////////////////////////////////////////////////////////////////////////

    @Override
    protected boolean isContextRequired() {
        return true;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Color Holding
    ///////////////////////////////////////////////////////////////////////////

    public void setColors(Object colors) {
        putData("album_color", colors);
    }

    public Object getColors() {
        return getData("album_color");
    }

    ///////////////////////////////////////////////////////////////////////////
    // Builder
    ///////////////////////////////////////////////////////////////////////////

    public static class Builder {
        private Album album;

        public Builder() {
            this(new Album());
        }

        public Builder(Album copy) {
            this.album = copy;
        }

        public Builder setName(String name) {
            this.album.setAlbumName(name);
            return this;
        }

        public Builder setArtistName(String artistName) {
            this.album.setAlbumArtistName(artistName);
            return this;
        }

        public Builder setGenreName(String genreName) {
            this.album.setAlbumGenreName(genreName);
            return this;
        }

        public Builder setYear(long year) {
            this.album.setAlbumYear(year);
            return this;
        }

        public Builder setAlbumId(long id) {
            this.album.setID(id);
            return this;
        }

        public Builder setNumberOfSongs(int numberOfSongs) {
            this.album.setAlbumNumberOfSongs(numberOfSongs);
            return this;
        }

        public Builder setArtworkPath(String path) {
            this.album.setAlbumArtworkPath(path);
            return this;
        }

        public Album build() {
            return album;
        }
    }
}

