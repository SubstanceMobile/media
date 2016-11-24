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

import android.net.Uri
import android.provider.MediaStore
import android.support.v4.media.MediaMetadataCompat
import mobile.substance.sdk.utils.MusicCoreUtil

/**
 * Wrapper around a MediaMetadataCompat optimised for Song metadata
 */
class Song : MediaObject() {

    ///////////////////////////////////////////////////////////////////////////
    // Overrides & other methods
    ///////////////////////////////////////////////////////////////////////////

    override val baseUri: Uri?
        get() = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

    override val uri: Uri
        get() = if (hasExplicitPath) explicitUri ?: Uri.EMPTY else super.uri


    override val isContextRequired: Boolean
        get() = true

    override fun toMetadataCompat(source: MediaMetadataCompat): MediaMetadataCompat {
        return MediaMetadataCompat.Builder(source)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, songTitle)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, songArtistName)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, songAlbumName)
                .putString(MediaMetadataCompat.METADATA_KEY_ART_URI, explicitArtworkUri.toString()) // This is just the explicit artwork path (if one exists), you still need to merge the album's artwork path in case that one is necessary
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, songDuration ?: 0)
                .putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, songTrackNumber ?: 0)
                .putLong(MediaMetadataCompat.METADATA_KEY_YEAR, songYear ?: 0)
                .build()
    }

    ///////////////////////////////////////////////////////////////////////////
    // Title
    ///////////////////////////////////////////////////////////////////////////

    var songTitle: String? = null

    ///////////////////////////////////////////////////////////////////////////
    // Artist
    ///////////////////////////////////////////////////////////////////////////

    var songArtistId: Long? = null

    var songArtistName: String? = null

    ///////////////////////////////////////////////////////////////////////////
    // Album
    ///////////////////////////////////////////////////////////////////////////

    var songAlbumId: Long? = null

    var songAlbumName: String? = null

    ///////////////////////////////////////////////////////////////////////////
    // Explicit artwork
    ///////////////////////////////////////////////////////////////////////////

    var explicitArtworkUri: Uri? = null

    val hasExplicitArtwork: Boolean
        get() = explicitArtworkUri != null

    ///////////////////////////////////////////////////////////////////////////
    // Duration
    ///////////////////////////////////////////////////////////////////////////

    var songDuration: Long? = null

    val songDurationString: String
        get() {
            val songDuration = songDuration
            if (songDuration != null) return MusicCoreUtil.stringForTime(songDuration) else return "--:--"
        }

    ///////////////////////////////////////////////////////////////////////////
    // Track number
    ///////////////////////////////////////////////////////////////////////////

    var songTrackNumber: Long? = null

    val songTrackNumberString: String
        get() {
            val trackNumber = songTrackNumber
            if (trackNumber != null) return trackNumber.toString() else return "-"
        }

    ///////////////////////////////////////////////////////////////////////////
    // Year
    ///////////////////////////////////////////////////////////////////////////

    var songYear: Long? = null

    ///////////////////////////////////////////////////////////////////////////
    // Explicit path
    ///////////////////////////////////////////////////////////////////////////

    var explicitUri: Uri? = null

    val hasExplicitPath: Boolean
        get() = explicitUri != null

    ///////////////////////////////////////////////////////////////////////////
    // Date of addition
    ///////////////////////////////////////////////////////////////////////////

    var dateAdded: Long? = null

    ///////////////////////////////////////////////////////////////////////////
    // Builder
    ///////////////////////////////////////////////////////////////////////////

    class Builder @JvmOverloads constructor(private val song: Song = Song()) {

        fun setId(id: Long): Builder {
            this.song.id = id
            return this
        }

        fun setTitle(title: String): Builder {
            this.song.songTitle = title
            return this
        }

        fun setArtistName(artistName: String): Builder {
            this.song.songArtistName = artistName
            return this
        }

        fun setAlbumName(albumName: String): Builder {
            this.song.songAlbumName = albumName
            return this
        }

        fun setAlbumId(albumId: Long): Builder {
            this.song.songAlbumId = albumId
            return this
        }

        fun setArtistId(artistId: Long): Builder {
            this.song.songArtistId = artistId
            return this
        }

        fun setYear(year: Long): Builder {
            this.song.songYear = year
            return this
        }

        fun setTrackNumber(trackNumber: Long): Builder {
            this.song.songTrackNumber = trackNumber
            return this
        }

        fun setDuration(duration: Long): Builder {
            this.song.songDuration = duration
            return this
        }

        fun setDateAdded(dateAdded: Long): Builder {
            this.song.dateAdded = dateAdded
            return this
        }

        fun build(): Song {
            return song
        }

    }

}
