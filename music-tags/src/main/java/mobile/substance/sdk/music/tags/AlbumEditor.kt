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

import android.content.Context
import android.net.Uri

import org.jaudiotagger.audio.AudioFile
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.audio.exceptions.CannotWriteException
import org.jaudiotagger.tag.FieldDataInvalidException
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.Tag
import org.jaudiotagger.tag.images.Artwork
import org.jaudiotagger.tag.images.ArtworkFactory

import java.io.File
import java.io.IOException

class AlbumEditor(private val context: Context, private val album: TagAlbum) {
    private var title: String? = null
    private var artist: String? = null
    private var genre: String? = null
    private var year: String? = null
    private var comment: String? = null
    private var label: String? = null
    private var artwork: Artwork? = null

    fun setTitle(title: String): AlbumEditor {
        this.title = title
        return this
    }

    fun setArtist(artist: String): AlbumEditor {
        this.artist = artist
        return this
    }

    fun setGenre(genre: String): AlbumEditor {
        this.genre = genre
        return this
    }

    fun setComment(comment: String): AlbumEditor {
        this.comment = comment
        return this
    }

    fun setYear(year: String): AlbumEditor {
        this.year = year
        return this
    }

    fun setLabel(label: String): AlbumEditor {
        this.label = label
        return this
    }

    fun setArtwork(file: File): AlbumEditor {
        try {
            this.artwork = ArtworkFactory.createArtworkFromFile(file)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return this
    }

    fun setArtwork(context: Context, uri: Uri): AlbumEditor {
        try {
            this.artwork = ArtworkFactory.createArtworkFromFile(File(TagHelper.getFileUri(context, uri).path))
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return this
    }

    fun commit(): Boolean {
        return write()
    }

    fun commitAndUpdateMediaStore(mediaStoreCallback: MediaStoreCallback): Boolean {
        if (write()) {
            val paths = arrayOfNulls<String>(album.getSongs()!!.size)
            for (i in 0..album.getSongs()!!.size - 1) {
                paths[i] = album.getSongs()!![i].path
            }
            MediaStoreHelper.updateMedia(paths, context, mediaStoreCallback)
            return true
        } else
            return false
    }


    private fun write(): Boolean {
        for (song in album.getSongs()!!) {
            if (!writeTags(song)) {
                return false
            }
        }
        return true
    }

    private fun writeTags(song: TagSong): Boolean {
        var file: AudioFile? = null
        try {
            file = AudioFileIO().readFile(File(song.path))
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

        val tag = file!!.tag

        try {
            tag.setField(if (artwork == null) album.getArtwork() else artwork)
        } catch (e: FieldDataInvalidException) {
            e.printStackTrace()
            return false
        }

        if (applyField(tag, FieldKey.TITLE, if (title == null) album.getTitle() else title)
                && applyField(tag, FieldKey.ARTIST, if (artist == null) album.getArtist() else artist)
                && applyField(tag, FieldKey.YEAR, if (year == null) album.getYear() else year)
                && applyField(tag, FieldKey.COMMENT, if (comment == null) album.getComment() else comment)
                && applyField(tag, FieldKey.RECORD_LABEL, if (label == null) album.getLabel() else label)
                && applyField(tag, FieldKey.GENRE, if (genre == null) album.getGenre() else genre)) {
            try {
                file.commit()
                return true
            } catch (e: CannotWriteException) {
                e.printStackTrace()
                return false
            }

        }
        return false
    }

    private fun applyField(tag: Tag, key: FieldKey, value: String?): Boolean {
        if (value == null)
            return false
        try {
            tag.setField(key, value)
            return true
        } catch (e: FieldDataInvalidException) {
            e.printStackTrace()
            return false
        }

    }
}
