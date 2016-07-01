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

package mobile.substance.sdk.music.tags

import org.jaudiotagger.tag.images.Artwork

/**
 * Created by Julian Os on 03.05.2016.
 */
class TagSong {
    internal var title: String? = null
    internal var artist: String? = null
    internal var album: String? = null
    internal var genre: String? = null
    internal var year: String? = null
    internal var comment: String? = null
    internal var label: String? = null
    internal var diskNo: String? = null
    internal var path: String? = null
    internal var lyrics: String? = null
    internal var artwork: Artwork? = null


    fun getTitle(): String? {
        return title
    }

    fun setTitle(title: String): TagSong {
        this.title = title
        return this
    }

    fun getArtist(): String? {
        return artist
    }

    fun setArtist(artist: String): TagSong {
        this.artist = artist
        return this
    }

    fun getAlbum(): String? {
        return album
    }

    fun setAlbum(album: String): TagSong {
        this.album = album
        return this
    }

    fun getGenre(): String? {
        return genre
    }

    fun setGenre(genre: String): TagSong {
        this.genre = genre
        return this
    }

    fun getYear(): String? {
        return year
    }

    fun setYear(year: String): TagSong {
        this.year = year
        return this
    }

    fun getComment(): String? {
        return comment
    }

    fun setComment(comment: String): TagSong {
        this.comment = comment
        return this
    }

    fun getLabel(): String? {
        return label
    }

    fun setLabel(label: String): TagSong {
        this.label = label
        return this
    }

    fun getDiskNo(): String? {
        return diskNo
    }

    fun setDiskNo(diskNo: String): TagSong {
        this.diskNo = diskNo
        return this
    }

    fun getArtwork(): Artwork? {
        return artwork
    }

    fun setArtwork(artwork: Artwork): TagSong {
        this.artwork = artwork
        return this
    }

    fun getPath(): String? {
        return path
    }

    fun setPath(path: String): TagSong {
        this.path = path
        return this
    }

    fun getLyrics(): String? {
        return lyrics
    }

    fun setLyrics(lyrics: String): TagSong {
        this.lyrics = lyrics
        return this
    }
}
