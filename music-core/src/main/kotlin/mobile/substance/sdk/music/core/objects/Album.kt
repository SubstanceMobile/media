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

package mobile.substance.sdk.music.core.objects

import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.media.MediaMetadataCompat
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import mobile.substance.sdk.music.core.MusicOptions

class Album : MediaObject() {

    ///////////////////////////////////////////////////////////////////////////
    // Overrides
    ///////////////////////////////////////////////////////////////////////////

    override val baseUri: Uri?
        get() = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI

    override val isContextRequired: Boolean
        get() = true

    ///////////////////////////////////////////////////////////////////////////
    // Title
    ///////////////////////////////////////////////////////////////////////////

    var albumName: String?
        get() = data?.getString(MediaMetadataCompat.METADATA_KEY_TITLE)
        set(value) {
            if(value != null) putString(MediaMetadataCompat.METADATA_KEY_TITLE, value)
        }

    ///////////////////////////////////////////////////////////////////////////
    // Album Artwork
    ///////////////////////////////////////////////////////////////////////////

    var albumArtworkPath: String?
        get() {
            val path = data?.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI)
            if(path != null) return path else return ""
        }
        set(value) {
            if (value != null) {
                putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, value)
                albumArtworkUri = Uri.parse("file://" + value)
                isAnimated = false
            } else {
                isAnimated = true
            }
        }

    var albumArtworkUri: Uri? = null
        private set


    interface ArtRequest {
        fun respond(albumArt: Bitmap)
    }

    fun requestArt(request: ArtRequest) {
        Glide.with(getContext()).load(albumArtworkPath).asBitmap().placeholder(MusicOptions.defaultArt).diskCacheStrategy(DiskCacheStrategy.SOURCE).animate(android.R.anim.fade_in).centerCrop().into(object : SimpleTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, glideAnimation: GlideAnimation<in Bitmap>) {
                request.respond(resource)
            }
        })
        Log.d("requestArt(ArtRequest)", albumArtworkPath)
    }

    fun requestArt(imageView: ImageView) {
        Glide.with(getContext()).load(albumArtworkPath).placeholder(MusicOptions.defaultArt).diskCacheStrategy(DiskCacheStrategy.SOURCE).crossFade().centerCrop().into(imageView)
        Log.d("requestArt(ImageView)", albumArtworkPath)
    }

    ///////////////////////////////////////////////////////////////////////////
    // Artist
    ///////////////////////////////////////////////////////////////////////////

    var albumArtistName: String?
        get() = data?.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST)
        set(value) {
            if(value != null) putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, value)
        }

    ///////////////////////////////////////////////////////////////////////////
    // Genre
    ///////////////////////////////////////////////////////////////////////////

    var albumGenreName: String?
        get() = data?.getString(MediaMetadataCompat.METADATA_KEY_GENRE)
        set(value) {
            if(value != null) putString(MediaMetadataCompat.METADATA_KEY_GENRE, value)
        }

    ///////////////////////////////////////////////////////////////////////////
    // Year
    ///////////////////////////////////////////////////////////////////////////

    var albumYear: Long?
        get() = data?.getLong(MediaMetadataCompat.METADATA_KEY_YEAR)
        set(value) {
            if(value != null) putLong(MediaMetadataCompat.METADATA_KEY_YEAR, value)
        }

    ///////////////////////////////////////////////////////////////////////////
    // Number Of Songs
    ///////////////////////////////////////////////////////////////////////////

    var albumNumberOfSongs: Long?
        get() = data?.getLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS)
        set(value) {
            if(value != null) putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, value)
        }

    ///////////////////////////////////////////////////////////////////////////
    // Color Holding
    ///////////////////////////////////////////////////////////////////////////

    var colors: Any
        get() = getData("album_colors")!!
        set(value) = putData("album_colors", value)

    ///////////////////////////////////////////////////////////////////////////
    // Builder
    ///////////////////////////////////////////////////////////////////////////

    class Builder @JvmOverloads constructor(private val album: Album = Album()) {

        fun setAlbumId(id: Long): Builder {
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


