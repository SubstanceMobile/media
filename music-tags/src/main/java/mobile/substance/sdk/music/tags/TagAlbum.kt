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
class TagAlbum {
    private var songs: List<TagSong>? = null
    private var title: String? = null
    private var artist: String? = null
    private var genre: String? = null
    private var year: String? = null
    private var label: String? = null
    private var comment: String? = null
    private var artwork: Artwork? = null

    fun getTitle(): String? {
        return title
    }

    fun setTitle(title: String): TagAlbum {
        this.title = title
        return this
    }

    fun getArtist(): String? {
        return artist
    }

    fun setArtist(artist: String): TagAlbum {
        this.artist = artist
        return this
    }

    fun getGenre(): String? {
        return genre
    }

    fun setGenre(genre: String): TagAlbum {
        this.genre = genre
        return this
    }

    fun getYear(): String? {
        return year
    }

    fun setYear(year: String): TagAlbum {
        this.year = year
        return this
    }

    fun getLabel(): String? {
        return label
    }

    fun setLabel(label: String): TagAlbum {
        this.label = label
        return this
    }

    fun getComment(): String? {
        return comment
    }

    fun setComment(comment: String): TagAlbum {
        this.comment = comment
        return this
    }

    fun getArtwork(): Artwork? {
        return artwork
    }

    fun setArtwork(artwork: Artwork): TagAlbum {
        this.artwork = artwork
        return this
    }

    fun getSongs(): List<TagSong>? {
        return songs
    }

    fun setSongs(songs: List<TagSong>): TagAlbum {
        this.songs = songs
        return this
    }
}
