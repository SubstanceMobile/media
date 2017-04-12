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

import android.graphics.Bitmap
import android.net.Uri
import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import android.support.v4.media.MediaMetadataCompat
import android.widget.ImageView
import mobile.substance.media.core.MediaFile
import mobile.substance.media.core.MediaObject
import mobile.substance.media.core.mediaApiError
import mobile.substance.media.options.AudioCoreOptions
import mobile.substance.media.options.CoreOptions
import mobile.substance.media.utils.AudioCoreUtil

abstract class Song : MediaObject(), MediaFile, ArtworkHolder {
    open var title: String? = null
    open var artistName: String? = null
    open var albumTitle: String? = null
    open var duration: Long? = null
    open var year: Long? = null
    open var trackNumber: Int? = null
    open var lyrics: String? = null
    open var artworkUri: Uri = Uri.EMPTY
    val formattedDuration: String
        get() = AudioCoreUtil.stringForTime(duration ?: 0L)

    companion object {
        fun from(uri: Uri) = NoMetadataSong(uri)
    }

    @WorkerThread
    abstract fun getArtist(): Artist?

    @WorkerThread
    abstract fun getAlbum(): Album?

    @WorkerThread
    abstract fun getGenre(): Genre?

    override fun MediaMetadataCompat.Builder.withMetadata(): MediaMetadataCompat.Builder {
        putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
        putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artistName)
        putString(MediaMetadataCompat.METADATA_KEY_ALBUM, albumTitle)
        putString(MediaMetadataCompat.METADATA_KEY_ART_URI, artworkUri.toString())
        putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration ?: 0L)
        putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, trackNumber?.toLong() ?: 0L)
        putLong(MediaMetadataCompat.METADATA_KEY_YEAR, year?.toLong() ?: 0L)
        return this
    }

    @UiThread
    override fun requestArtworkLoad(target: ImageView) = CoreOptions.imageLoadAdapter?.onRequestLoad(artworkUri, AudioCoreOptions.defaultSongArtworkRes, target) ?: mediaApiError(102)

    @WorkerThread
    override fun requestArtworkBitmap(): Bitmap = CoreOptions.imageLoadAdapter?.onRequestBitmap(artworkUri, AudioCoreOptions.defaultSongArtworkRes) ?: mediaApiError(102)

}