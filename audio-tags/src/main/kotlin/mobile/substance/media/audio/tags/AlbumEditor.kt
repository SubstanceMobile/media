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

package mobile.substance.media.audio.tags

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import mobile.substance.media.utils.AudioCoreUtil
import mobile.substance.media.utils.CoreUtil.toFilePath

import org.jaudiotagger.audio.AudioFile
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.audio.exceptions.CannotWriteException
import org.jaudiotagger.audio.flac.metadatablock.MetadataBlockDataPicture
import org.jaudiotagger.tag.FieldDataInvalidException
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.Tag
import org.jaudiotagger.tag.images.AndroidArtwork
import org.jaudiotagger.tag.images.Artwork
import org.jaudiotagger.tag.images.ArtworkFactory
import org.jaudiotagger.tag.reference.PictureTypes

import java.io.File
import java.io.IOException
import java.nio.ByteBuffer

class AlbumEditor(private val context: Context, private val album: TagAlbum) {
    private var title: String = ""
    private var artist: String = ""
    private var genre: String = ""
    private var year: String = ""
    private var comment: String = ""
    private var label: String = ""
    private var artwork: Artwork? = null
    private var albumThumb: File? = null

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

    fun setLinkedArtwork(url: String): AlbumEditor {
        try {
            this.artwork = ArtworkFactory.createLinkedArtworkFromURL(url)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return this
    }

    fun setArtwork(bytes: ByteArray): AlbumEditor {
        try {
            val currentTimeMillis = System.currentTimeMillis()
            Log.d(AlbumEditor::class.java.simpleName, currentTimeMillis.toString())
            albumThumb = File(Environment.getExternalStorageDirectory().path + "/Android/data/com.android.providers.media/albumthumbs/$currentTimeMillis")
            if (!(albumThumb?.exists() ?: false)) {
                albumThumb?.parentFile?.mkdirs()
                albumThumb?.createNewFile()
            }

            albumThumb?.writeBytes(bytes)

            this.artwork = AndroidArtwork.createArtworkFromFile(albumThumb)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return this
    }

    fun setArtwork(context: Context, uri: Uri): AlbumEditor {
        try {
            this.artwork = ArtworkFactory.createArtworkFromFile(File(uri.toFilePath(context)))
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
            val paths = arrayOfNulls<String>(album.songs!!.size)
            for (i in 0..album.songs!!.size - 1)
                paths[i] = album.songs!![i]!!.path
            MediaStoreHelper.updateMedia(paths, context, mediaStoreCallback, album.album, albumThumb?.path.orEmpty())
            return true
        } else return false
    }


    private fun write(): Boolean {
        for (song in album.songs!!) {
            if (!writeTags(song!!)) {
                return false
            }
        }
        return true
    }

    private fun writeTags(song: TagSong): Boolean {
        var file: AudioFile?
        try {
            file = AudioFileIO().readFile(File(song.path))
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

        val tag = file!!.tag

        try {
            tag.deleteArtworkField()
            tag.addField(if (artwork == null) album.artwork else artwork)
        } catch (e: FieldDataInvalidException) {
            e.printStackTrace()
            return false
        }

        if (applyField(tag, FieldKey.ALBUM, if (title.isEmpty()) album.title else title)
                && applyField(tag, FieldKey.ALBUM_ARTIST, if (artist.isEmpty()) album.artist else artist)
                && applyField(tag, FieldKey.YEAR, if (year.isEmpty()) album.year else year)
                && applyField(tag, FieldKey.COMMENT, if (comment.isEmpty()) album.comment else comment)
                && applyField(tag, FieldKey.RECORD_LABEL, if (label.isEmpty()) album.label else label)
                && applyField(tag, FieldKey.GENRE, if (genre.isEmpty()) album.genre else genre)) {
            try {
                file.commit()
                Log.d(AlbumEditor::class.java.simpleName, "Committed updated tags to file ${file.file.path}")
                return true
            } catch (e: CannotWriteException) {
                e.printStackTrace()
                return false
            }

        } else Log.d(AlbumEditor::class.java.simpleName, "didn't write tags!")
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
