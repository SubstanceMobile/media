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
class TagSong : TagHelper.TagObject() {
    var title: String = ""
    var artist: String = ""
    var album: String = ""
    var genre: String = ""
    var year: String = ""
    var comment: String = ""
    var label: String = ""
    var diskNo: String = ""
    var path: String = ""
    var lyrics: String = ""
    var artwork: Artwork? = null

    class Builder(private val base: TagSong = TagSong()) {

        fun setTitle(title: String): Builder {
            base.title = title
            return this
        }

        fun setArtist(artist: String): Builder {
            base.artist = artist
            return this
        }

        fun setAlbum(album: String): Builder {
            base.album = album
            return this
        }

        fun setGenre(genre: String): Builder {
            base.genre = genre
            return this
        }

        fun setYear(year: String): Builder {
            base.year = year
            return this
        }

        fun setComment(comment: String): Builder {
            base.comment = comment
            return this
        }

        fun setLabel(label: String): Builder {
            base.label = label
            return this
        }

        fun setDiskNo(diskNo: String): Builder {
            base.diskNo = diskNo
            return this
        }

        fun setArtwork(artwork: Artwork?): Builder {
            base.artwork = artwork
            return this
        }

        fun setPath(path: String): Builder {
            base.path = path
            return this
        }

        fun setLyrics(lyrics: String): Builder {
            base.lyrics = lyrics
            return this
        }

        fun build(): TagSong = base

    }

}
