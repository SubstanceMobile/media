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

package mobile.substance.media.music.tags

import android.content.Context
import android.net.Uri
import mobile.substance.media.utils.MusicCoreUtil

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

class SongEditor(private val context: Context, private val song: TagSong) {
    private var title: String? = null
    private var artist: String? = null
    private var album: String? = null
    private var genre: String? = null
    private var diskNo: String? = null
    private var year: String? = null
    private var comment: String? = null
    private var label: String? = null
    private var lyrics: String? = null
    private var artwork: Artwork? = null

    fun setTitle(title: String): SongEditor {
        this.title = title
        return this
    }

    fun setArtist(artist: String): SongEditor {
        this.artist = artist
        return this
    }

    fun setAlbum(album: String): SongEditor {
        this.album = album
        return this
    }

    fun setGenre(genre: String): SongEditor {
        this.genre = genre
        return this
    }

    fun setDiskNo(diskNo: String): SongEditor {
        this.diskNo = diskNo
        return this
    }

    fun setComment(comment: String): SongEditor {
        this.comment = comment
        return this
    }

    fun setYear(year: String): SongEditor {
        this.year = year
        return this
    }

    fun setLabel(label: String): SongEditor {
        this.label = label
        return this
    }

    fun setLyrics(lyrics: String): SongEditor {
        this.lyrics = lyrics
        return this
    }

    fun setArtwork(file: File): SongEditor {
        try {
            this.artwork = ArtworkFactory.createArtworkFromFile(file)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return this
    }

    fun setArtwork(context: Context, uri: Uri): SongEditor {
        try {
            this.artwork = ArtworkFactory.createArtworkFromFile(File(MusicCoreUtil.getFilePath(context, uri)))
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return this
    }

    fun commit(): Boolean {
        return writeTags()
    }

    fun commitAndUpdateMediaStore(mediaStoreCallback: MediaStoreCallback): Boolean {
        if (writeTags()) {
            MediaStoreHelper.updateMedia(arrayOf(song.path!!), context, mediaStoreCallback)
            return true
        } else
            return false
    }


    private fun writeTags(): Boolean {
        var file: AudioFile? = null
        try {
            file = AudioFileIO().readFile(File(song.path))
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

        val tag = file!!.tag

        try {
            tag.setField(if (artwork == null) song.artwork else artwork)
        } catch (e: FieldDataInvalidException) {
            e.printStackTrace()
            return false
        }

        if (applyField(tag, FieldKey.TITLE, if (title == null) song.title else title)
                && applyField(tag, FieldKey.ARTIST, if (artist == null) song.artist else artist)
                && applyField(tag, FieldKey.ALBUM, if (album == null) song.album else album)
                && applyField(tag, FieldKey.DISC_NO, if (diskNo == null) song.diskNo else diskNo)
                && applyField(tag, FieldKey.YEAR, if (year == null) song.year else year)
                && applyField(tag, FieldKey.COMMENT, if (comment == null) song.comment else comment)
                && applyField(tag, FieldKey.RECORD_LABEL, if (label == null) song.label else label)
                && applyField(tag, FieldKey.GENRE, if (genre == null) song.genre else genre)
                && applyField(tag, FieldKey.LYRICS, if (lyrics == null) song.lyrics else lyrics)) {
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
