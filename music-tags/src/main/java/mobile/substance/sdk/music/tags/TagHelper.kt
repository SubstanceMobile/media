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

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import android.provider.MediaStore
import mobile.substance.sdk.music.core.objects.Album
import mobile.substance.sdk.music.core.objects.Playlist
import mobile.substance.sdk.music.core.objects.Song
import mobile.substance.sdk.music.core.utils.CoreUtil
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.Tag
import java.io.File

class TagHelper {

    private fun createPlaylist(context: Context, name: String?): Playlist? {
        if (name == null || name.length <= 0) {
            return null
        } else {
            try {
                val mQuery = context.contentResolver.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, arrayOf("_id"), "name=?", arrayOf(name), null)
                if (mQuery == null || mQuery.count < 1) {
                    val mValues = ContentValues(1)
                    mValues.put(MediaStore.Audio.Playlists.NAME, name)
                    val mInsert = context.contentResolver.insert(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, mValues)
                    mQuery?.close()
                    if (mInsert != null) {
                        context.contentResolver.notifyChange(Uri.parse("content://media/audio/playlists"), null)
                        val p = Playlist()
                        p.playlistName = name
                        p.id = mInsert.lastPathSegment.toLong()
                        //p.setFavorites(TYPE);
                        return p
                    } else
                        return null
                } else
                    return null
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }

        }
    }

    interface ReadCallback<T> {

        fun onReadFinished(result: T)

    }

    internal class AsyncTagAlbum(private val context: Context, private val callback: ReadCallback<TagAlbum?>) : AsyncTask<Album, Void, TagAlbum>() {

        override fun doInBackground(vararg params: Album): TagAlbum? {
            return null //
        }

        override fun onPostExecute(tagAlbum: TagAlbum) {
            callback.onReadFinished(tagAlbum)
        }
    }

    internal class AsyncTagSong(private val context: Context, private val callback: ReadCallback<TagSong?>) : AsyncTask<Song, Void, TagSong?>() {

        override fun doInBackground(vararg params: Song): TagSong? {
            return read(context, params[0])
        }

        override fun onPostExecute(tagSong: TagSong?) {
            callback.onReadFinished(tagSong)
        }
    }

    companion object {

        fun read(context: Context, song: Song): TagSong? {
            var tag: Tag? = null
            val filePath = getFileUri(context, song.uri).path
            try {
                tag = AudioFileIO.read(File(filePath)).tag
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }

            return TagSong().setTitle(tag!!.getFirst(FieldKey.TITLE)).setArtist(tag.getFirst(FieldKey.ARTIST)).setAlbum(tag.getFirst(FieldKey.ALBUM)).setGenre(tag.getFirst(FieldKey.GENRE)).setYear(tag.getFirst(FieldKey.YEAR)).setComment(tag.getFirst(FieldKey.COMMENT)).setLabel(tag.getFirst(FieldKey.RECORD_LABEL)).setDiskNo(tag.getFirst(FieldKey.DISC_NO)).setPath(filePath).setLyrics(tag.getFirst(FieldKey.LYRICS))
        }

        //public static TagAlbum read(Context context, Album album) {
        //    List<TagSong> songs = new ArrayList<>();
        //    for (Song song : album.getSongs()) {
        //        songs.add(read(context, song));
        //    }
        //
        //      Artwork artwork = null;
        //    try {
        //      artwork = ArtworkFactory.createArtworkFromFile(new File(album.getAlbumArtworkPath()));
        //  } catch (IOException e) {
        //      e.printStackTrace();
        //  }
        //
        //      return new TagAlbum()
        //            .setTitle(album.getTitle())
        //          .setArtist(album.getAlbumArtistName())
        //          //.setGenre(album.getGenre())
        //        //.setYear(album.getYear())
        //      .setArtwork(artwork)
        //    .setSongs(songs);
        //}

        fun readAsync(context: Context, song: Song, callback: ReadCallback<TagSong?>) {
            AsyncTagSong(context, callback).execute(song)
        }

        fun readAsync(context: Context, album: Album, callback: ReadCallback<TagAlbum?>) {
            AsyncTagAlbum(context, callback).execute(album)
        }

        fun getFileUri(context: Context, uri: Uri): Uri {
            return Uri.parse(CoreUtil.getFilePath(context, uri))
        }
    }
}
