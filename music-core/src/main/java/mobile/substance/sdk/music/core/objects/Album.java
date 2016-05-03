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
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adrian on 7/5/2015.
 */
public class Album extends MediaObject {
    public static final int FRAME_COLOR = 0, TITLE_COLOR = 1, SUBTITLE_COLOR = 2;
    public String albumArtistName;
    public boolean animated;

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //This manages the songs of the album
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public String albumArtPath;

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //This handles the Album Art
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean defaultArt = false;
    public boolean colorAnimated = false;
    public int[] mainColors;
    public int[] accentColors = new int[]{
            Color.BLACK, Color.WHITE, Color.GRAY
    };
    public boolean colorsLoaded = false;

    @Override
    protected Uri getBaseUri() {
        return MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
    }

    public List<Song> getSongs() {
        return new ArrayList<>();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Colors
    ///////////////////////////////////////////////////////////////////////////

    public String getAlbumArtPath() {
        return albumArtPath;
    }

    public void setAlbumArtPath(String albumArtPath) {
        this.albumArtPath = "file://" + albumArtPath;
        if (albumArtPath != null) {
            defaultArt = false;
            colorAnimated = false;
        } else {
            defaultArt = true;
            colorAnimated = true;
        }
    }

    public void requestArt(final ArtRequest request) { // <- Not sure about this one
        Glide.with(getContext()).load(getAlbumArtPath())
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
        Glide.with(imageView.getContext()).load(getAlbumArtPath())
                .placeholder(placeholder)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .crossFade()
                .centerCrop()
                .into(imageView);
    }

    public int getBackgroundColor() {
        return mainColors[FRAME_COLOR];
    }

    public int getTitleTextColor() {
        return mainColors[TITLE_COLOR];
    }

    public int getSubtitleTextColor() {
        return mainColors[SUBTITLE_COLOR];
    }

    public int getAccentColor() {
        return accentColors[FRAME_COLOR];
    }

    public int getAccentIconColor() {
        return accentColors[TITLE_COLOR];
    }

    public int getAccentSecondaryIconColor() {
        return accentColors[SUBTITLE_COLOR];
    }

    public String getAlbumArtistName() {
        return albumArtistName;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //This handles the album artist
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void setAlbumArtistName(String albumArtistName) {
        this.albumArtistName = albumArtistName;
    }

    @Override
    protected boolean isContextRequired() {
        return true;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Context behavior
    ///////////////////////////////////////////////////////////////////////////

    public interface ArtRequest {
        void respond(Bitmap albumArt);
    }

}

