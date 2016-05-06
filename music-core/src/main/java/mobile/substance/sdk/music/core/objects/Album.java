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
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

/**
 * Created by Adrian on 7/5/2015.
 */
public class Album extends MediaObject {
    private String albumName, albumGenreName, albumYear, albumArtistName, albumArtworkPath;
    private int albumDefaultArtworkResId, albumNumberOfSongs;

    @Override
    protected Uri getBaseUri() {
        return MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
    }

    public int getAlbumDefaultArtworkResId() {
        return albumDefaultArtworkResId;
    }

    public void setAlbumDefaultArtworkResId(int albumDefaultArtworkResId) {
        this.albumDefaultArtworkResId = albumDefaultArtworkResId;
    }

    public String getAlbumArtworkPath() {
        return albumArtworkPath;
    }

    public void setAlbumArtworkPath(String albumArtworkPath) {

        this.albumArtworkPath = "file://" + albumArtworkPath;
        if (albumArtworkPath != null) {
            setAnimated(false);
        } else {
            setAnimated(true);
        }
    }

    public void requestArt(final ArtRequest request) { // <- Not sure about this one
        Glide.with(getContext()).load(getAlbumArtworkPath())
                .asBitmap()
                //.placeholder(!Options.isLightTheme() ? R.drawable.art_dark : R.drawable.art_light)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .animate(android.R.anim.fade_in)
                .centerCrop()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        request.respond(resource);
                    }
                });
    }

    public void requestArt(ImageView imageView, Drawable placeholder) {
        Glide.with(imageView.getContext()).load(getAlbumArtworkPath())
                .placeholder(placeholder)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .crossFade()
                .centerCrop()
                .into(imageView);
        Log.d("requestArt()", getAlbumArtworkPath());
    }

    public String getAlbumArtistName() {
        return albumArtistName;
    }

    public void setAlbumArtistName(String albumArtistName) {
        this.albumArtistName = albumArtistName;
        putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, albumArtistName);
    }

    @Override
    protected boolean isContextRequired() {
        return true;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
        putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, albumName);
    }

    public String getAlbumGenreName() {
        return albumGenreName;
    }

    public void setAlbumGenreName(String albumGenreName) {
        this.albumGenreName = albumGenreName;
    }

    public String getAlbumYear() {
        return albumYear;
    }

    public void setAlbumYear(String albumYear) {
        this.albumYear = albumYear;
    }

    public int getAlbumNumberOfSongs() {
        return albumNumberOfSongs;
    }

    public void setAlbumNumberOfSongs(int albumNumberOfSongs) {
        this.albumNumberOfSongs = albumNumberOfSongs;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Context behavior
    ///////////////////////////////////////////////////////////////////////////

    public interface ArtRequest {
        void respond(Bitmap albumArt);
    }

    public static class Builder {
        private Album album;

        public Builder() {
            this.album = new Album();
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

        public Builder setYear(String year) {
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

    public static class WithColors<ColorContainer> extends Album {
        private ColorContainer container;

        public WithColors(ColorContainer container) {
            this.container = container;
        }

        public ColorContainer getContainer() {
            return container;
        }

        public void setContainer(ColorContainer container) {
            this.container = container;
        }
    }

}

