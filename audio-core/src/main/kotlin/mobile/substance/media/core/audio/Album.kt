/*
 * Copyright 2017 Substance Mobile
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

package mobile.substance.media.core.audio

import android.net.Uri
import android.support.v4.media.MediaMetadataCompat
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import mobile.substance.media.core.MediaObject
import mobile.substance.media.options.AudioCoreOptions

abstract class Album : AudioObject() {
    open var title: String? = null
    open var artistName: String? = null
    open var artworkUri: Uri? = null
    open var genre: String? = null
    open var numberOfSongs: Int? = null
    open var year: String? = null

    abstract fun getSongs(): List<Song>?

    abstract fun getArtist(): Artist?

    abstract fun getGenre(): Genre?

    final override fun MediaMetadataCompat.Builder.withMetadata(): MediaMetadataCompat.Builder {
        putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, title)
        putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, artworkUri.toString())
        putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artistName)
        putString(MediaMetadataCompat.METADATA_KEY_GENRE, genre)
        putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, numberOfSongs?.toLong() ?: 0L)
        putLong(MediaMetadataCompat.METADATA_KEY_YEAR, year?.toLong() ?: 0L)
        return this
    }

    override fun loadArtwork(target: ImageView) {
        if (AudioCoreOptions.glidePreferPlaceholder) {
            Glide.with(getContext())
                    .load(artworkUri)
                    .placeholder(AudioCoreOptions.defaultArtResId)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .crossFade()
                    .centerCrop()
                    .into(target)
        } else Glide.with(getContext())
                .load(artworkUri)
                .error(AudioCoreOptions.defaultArtResId)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .crossFade()
                .centerCrop()
                .into(target)
    }

}