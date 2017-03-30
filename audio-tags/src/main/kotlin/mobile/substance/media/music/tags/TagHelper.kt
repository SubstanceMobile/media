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
import android.os.AsyncTask
import mobile.substance.media.core.music.MusicDataHolder
import mobile.substance.media.core.music.objects.Album
import mobile.substance.media.core.MediaObject
import mobile.substance.media.core.music.objects.Song
import mobile.substance.media.utils.MusicCoreUtil
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
            if (params.first() is Album) read(context, params.first() as Album)
            if (params.first() is Song) read(context, params.first() as Song)
            return null
        }

        override fun onPostExecute(result: R?) {
            callback.onReadFinished(result)
        }
    }

    companion object {

        fun read(context: Context, song: Song): TagSong? {
            val tag: Tag?
            val filePath = MusicCoreUtil.getFilePath(context, song.uri)
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

        fun read(context: Context, album: Album): TagAlbum? {
            val songs = ArrayList<TagSong>()
            MusicDataHolder.findSongsForAlbum(album).forEach {
                songs.add(read(context, it)!!)
            }

            val tagAlbum = TagAlbum.Builder()
                    .setTitle(album.albumName.orEmpty())
                    .setArtist(album.albumArtistName.orEmpty())
                    .setGenre(album.albumGenreName.orEmpty())
                    .setYear(album.albumYear.toString())
                    .setSongs(songs)


            if (album.albumArtworkPath != null && album.albumArtworkPath.orEmpty().length > 0) {
                tagAlbum.setArtwork(ArtworkFactory.createArtworkFromFile(File(MusicCoreUtil.getFilePath(context, Uri.parse("file://" + album.albumArtworkPath)))))
            }

            return tagAlbum.build()
        }

        fun readAsync(context: Context, song: Song, callback: ReadCallback<TagSong?>) {
            AsyncRead<Song, TagSong>(context, callback).execute(song)
        }

        fun readAsync(context: Context, album: Album, callback: ReadCallback<TagAlbum?>) {
            AsyncRead<Album, TagAlbum>(context, callback).execute(album)
        }
    }
}
