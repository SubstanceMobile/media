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

package mobile.substance.media.core.music.objects

import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.media.MediaMetadataCompat
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import mobile.substance.media.options.MusicCoreOptions

class Album : mobile.substance.media.core.MediaObject() {

    ///////////////////////////////////////////////////////////////////////////
    // Overrides
    ///////////////////////////////////////////////////////////////////////////

    override val baseUri: Uri?
        get() = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI

    override val isContextRequired: Boolean
        get() = true

    override fun toMetadataCompat(source: MediaMetadataCompat): MediaMetadataCompat {
        return MediaMetadataCompat.Builder(source)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, albumName)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, albumArtworkPath)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, albumArtistName)
                .putString(MediaMetadataCompat.METADATA_KEY_GENRE, albumGenreName)
                .putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, albumNumberOfSongs ?: 0)
                .putLong(MediaMetadataCompat.METADATA_KEY_YEAR, albumYear ?: 0)
                .build()
    }

    ///////////////////////////////////////////////////////////////////////////
    // Title
    ///////////////////////////////////////////////////////////////////////////

    var albumName: String? = null

    ///////////////////////////////////////////////////////////////////////////
    // Album Artwork
    ///////////////////////////////////////////////////////////////////////////

    var albumArtworkPath: String? = null

    interface ArtRequest {
        fun respond(albumArt: Bitmap)
    }

    fun requestArt(request: ArtRequest) {
        val target = object : SimpleTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, glideAnimation: GlideAnimation<in Bitmap>) {
                request.respond(resource)
            }
        }
        if (MusicCoreOptions.glidePreferPlaceholder) {
            Glide.with(getContext())
                    .load(albumArtworkPath)
                    .asBitmap()
                    .placeholder(MusicCoreOptions.defaultArt)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .animate(android.R.anim.fade_in)
                    .centerCrop()
                    .into(target)
        } else Glide.with(getContext())
                .load(albumArtworkPath)
                .asBitmap()
                .error(MusicCoreOptions.defaultArt)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .animate(android.R.anim.fade_in)
                .centerCrop()
                .into(target)
    }

    fun requestArt(imageView: ImageView) {
        if (MusicCoreOptions.glidePreferPlaceholder) {
            Glide.with(getContext())
                    .load(albumArtworkPath)
                    .placeholder(MusicCoreOptions.defaultArt)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .crossFade()
                    .centerCrop()
                    .into(imageView)
        } else Glide.with(getContext())
                .load(albumArtworkPath)
                .error(MusicCoreOptions.defaultArt)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .crossFade()
                .centerCrop()
                .into(imageView)
    }

    ///////////////////////////////////////////////////////////////////////////
    // Artist
    ///////////////////////////////////////////////////////////////////////////

    var albumArtistName: String? = null

    ///////////////////////////////////////////////////////////////////////////
    // Genre
    ///////////////////////////////////////////////////////////////////////////

    var albumGenreName: String? = null

    ///////////////////////////////////////////////////////////////////////////
    // Year
    ///////////////////////////////////////////////////////////////////////////

    var albumYear: Long? = null

    ///////////////////////////////////////////////////////////////////////////
    // Number Of Songs
    ///////////////////////////////////////////////////////////////////////////

    var albumNumberOfSongs: Long? = null

    ///////////////////////////////////////////////////////////////////////////
    // Color Holding
    ///////////////////////////////////////////////////////////////////////////

    var colors: Any?
        get() = get("album_colors")
        set(value) {
            if (value != null) put("album_colors", value)
        }

    ///////////////////////////////////////////////////////////////////////////
    // Builder
    ///////////////////////////////////////////////////////////////////////////

    class Builder @JvmOverloads constructor(private val album: Album = Album()) {

        fun setId(id: Long): Builder {
            this.album.id = id
            return this
        }

        fun setName(name: String): Builder {
            this.album.albumName = name
            return this
        }

        fun setArtistName(artistName: String): Builder {
            this.album.albumArtistName = artistName
            return this
        }

        fun setGenreName(genreName: String): Builder {
            this.album.albumGenreName = genreName
            return this
        }

        fun setYear(year: Long): Builder {
            this.album.albumYear = year
            return this
        }

        fun setNumberOfSongs(numberOfSongs: Long): Builder {
            this.album.albumNumberOfSongs = numberOfSongs
            return this
        }

        fun setArtworkPath(path: String?): Builder {
            this.album.albumArtworkPath = path
            return this
        }

        fun build(): Album {
            return album
        }
    }
}


