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
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat.*
import mobile.substance.sdk.music.core.CoreUtil

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
        get() {
            if(hasExplicitPath) return Uri.parse(explicitPath) else return super.uri
        }

    override val isContextRequired: Boolean
        get() = true

    fun toMediaItem(): MediaBrowserCompat.MediaItem {
        return MediaBrowserCompat.MediaItem(metadata!!.description, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE)
    }

    ///////////////////////////////////////////////////////////////////////////
    // Artist
    ///////////////////////////////////////////////////////////////////////////

    var songArtistId: Long? = null

    var songArtistName: String?
        get() = metadata?.getString(METADATA_KEY_ARTIST)
        set(value) {
            if(value != null) putString(METADATA_KEY_ARTIST, value)
        }

    ///////////////////////////////////////////////////////////////////////////
    // Title
    ///////////////////////////////////////////////////////////////////////////

    var songTitle: String?
        get() = metadata?.getString(METADATA_KEY_TITLE)
        set(value) {
            if(value != null) putString(METADATA_KEY_TITLE, value)
        }

    ///////////////////////////////////////////////////////////////////////////
    // Album
    ///////////////////////////////////////////////////////////////////////////

    var songAlbumID: Long? = null

    var songAlbumName: String?
        get() = metadata?.getString(METADATA_KEY_ALBUM)
        set(value) {
            if(value != null) putString(METADATA_KEY_ALBUM, value)
        }

    ///////////////////////////////////////////////////////////////////////////
    // Explicit artwork
    ///////////////////////////////////////////////////////////////////////////

    val hasExplicitArtwork: Boolean = explicitArtworkPath != null

    var explicitArtworkPath: String?
        get() = metadata?.getString(METADATA_KEY_ART_URI)
        set(value) {
            if(value != null) putString(METADATA_KEY_ART_URI, value)
        }

    ///////////////////////////////////////////////////////////////////////////
    // Duration
    ///////////////////////////////////////////////////////////////////////////

    var songDuration: Long?
        get() = metadata?.getLong(METADATA_KEY_DURATION)
        set(value) {
            if(value != null) putLong(METADATA_KEY_DURATION, value)
        }

    val songDurationString: String
        get() {
            val songDuration = songDuration
            if(songDuration != null) return CoreUtil.stringForTime(songDuration) else return "--:--"
        }

    ///////////////////////////////////////////////////////////////////////////
    // Track number
    ///////////////////////////////////////////////////////////////////////////

    var songTrackNumber: Long?
        get() = metadata?.getLong(METADATA_KEY_TRACK_NUMBER)
        set(value) {
            if(value != null) putLong(METADATA_KEY_TRACK_NUMBER, value)
        }


    val songTrackNumberString: String
        get() {
            val trackNumber = songTrackNumber
            if(trackNumber != null) return trackNumber.toString() else return "-"
        }

    ///////////////////////////////////////////////////////////////////////////
    // Year
    ///////////////////////////////////////////////////////////////////////////

    var songYear: String?
        get() {
            val year = metadata?.getLong(METADATA_KEY_YEAR)
            if(year != null) return year.toString() else return "-"
        }
        set(value) {
            if(value != null) putLong(METADATA_KEY_YEAR, value.toLong())
        }

    ///////////////////////////////////////////////////////////////////////////
    // Explicit path
    ///////////////////////////////////////////////////////////////////////////

    var explicitPath: String? = null

    val hasExplicitPath: Boolean = explicitPath != null

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
            this.song.songAlbumID = albumId
            return this
        }

        fun setArtistId(artistId: Long): Builder {
            this.song.songArtistId = artistId
            return this
        }

        fun setYear(year: String): Builder {
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

        fun build(): Song {
            return song
        }

    }

}
