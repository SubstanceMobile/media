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

package mobile.substance.media.audio.tags

import android.content.Context
import android.os.AsyncTask
import mobile.substance.media.audio.local.objects.MediaStoreAlbum
import mobile.substance.media.audio.local.objects.MediaStoreSong
import mobile.substance.media.utils.CoreUtil.toFilePath
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.Tag
import org.jaudiotagger.tag.images.ArtworkFactory
import java.io.File
import java.util.*

class TagHelper {

    open class TagObject

    interface ReadCallback<T> {

        fun onReadFinished(result: T)

    }

    internal class AsyncRead<T : mobile.substance.media.core.MediaObject, R : TagObject>(private val context: Context, private val callback: ReadCallback<R?>) : AsyncTask<T?, Void, R?>() {

        override fun doInBackground(vararg params: T?): R? {
            params.first() ?: return null
            if (params.first() is MediaStoreAlbum) read(context, params.first() as MediaStoreAlbum)
            if (params.first() is MediaStoreSong) read(context, params.first() as MediaStoreSong)
            return null
        }

        override fun onPostExecute(result: R?) {
            callback.onReadFinished(result)
        }
    }

    companion object {

        fun read(context: Context, song: MediaStoreSong): TagSong? {
            val tag: Tag?
            val filePath = song.uri.toFilePath(context)
            try {
                tag = AudioFileIO.read(File(filePath)).tag
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }

            return TagSong.Builder()
                    .setTitle(tag.getFirst(FieldKey.TITLE))
                    .setArtist(tag.getFirst(FieldKey.ARTIST))
                    .setAlbum(tag.getFirst(FieldKey.ALBUM))
                    .setGenre(tag.getFirst(FieldKey.GENRE))
                    .setYear(tag.getFirst(FieldKey.YEAR))
                    .setComment(tag.getFirst(FieldKey.COMMENT))
                    .setLabel(tag.getFirst(FieldKey.RECORD_LABEL))
                    .setDiskNo(tag.getFirst(FieldKey.DISC_NO))
                    .setPath(filePath.orEmpty())
                    .setLyrics(tag.getFirst(FieldKey.LYRICS))
                    .build()
        }

        fun read(context: Context, album: MediaStoreAlbum): TagAlbum? {
            val songs = ArrayList<TagSong>()
            album.getSongs()?.forEach {
                songs.add(read(context, it as MediaStoreSong)!!)
            }

            val tagAlbum = TagAlbum.Builder()
                    .setTitle(album.title.orEmpty())
                    .setArtist(album.artistName.orEmpty())
                    .setGenre(album.genre.orEmpty())
                    .setYear(album.year.toString())
                    .setSongs(songs)


            if (album.artworkUri != null && album.artworkUri.toString().orEmpty().isNotEmpty()) {
                tagAlbum.setArtwork(ArtworkFactory.createArtworkFromFile(File(album.artworkUri!!.toFilePath(context))))
            }

            return tagAlbum.build()
        }

        fun readAsync(context: Context, song: MediaStoreSong, callback: ReadCallback<TagSong?>) {
            AsyncRead<MediaStoreSong, TagSong>(context, callback).execute(song)
        }

        fun readAsync(context: Context, album: MediaStoreAlbum, callback: ReadCallback<TagAlbum?>) {
            AsyncRead<MediaStoreAlbum, TagAlbum>(context, callback).execute(album)
        }
    }
}
