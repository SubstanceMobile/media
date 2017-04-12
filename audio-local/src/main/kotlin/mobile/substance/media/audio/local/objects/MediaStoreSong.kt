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

package mobile.substance.media.audio.local.objects

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import android.support.annotation.WorkerThread
import android.widget.ImageView
import co.metalab.asyncawait.async
import mobile.substance.media.audio.local.DateAdded
import mobile.substance.media.core.audio.Song
import mobile.substance.media.core.mediaApiError
import mobile.substance.media.local.core.MediaStoreAttributes
import mobile.substance.media.options.AudioCoreOptions
import mobile.substance.media.options.AudioLocalOptions
import mobile.substance.media.options.CoreOptions

open class MediaStoreSong : Song(), MediaStoreAttributes, DateAdded {
    var albumId: Long = -1
    var artistId: Long = -1

    override var artworkUri: Uri
        get() = getAlbum()?.artworkUri ?: Uri.EMPTY
        set(value) {}

    override var dateAdded: Long = -1

    override var id: Long = -1

    override val baseUri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

    override val uri: Uri
        get() = contentUri

    override fun getArtist() = AudioLocalOptions.localAudioHolder?.findArtistById(artistId)

    override fun getAlbum() = AudioLocalOptions.localAudioHolder?.findAlbumById(albumId)

    override fun getGenre() = AudioLocalOptions.localAudioHolder?.findGenreForSong(this)

    override fun requestArtworkLoad(target: ImageView) {
        if (AudioLocalOptions.useEmbeddedArtwork) async {
            val embeddedPicture = await {
                MediaMetadataRetriever().apply {
                    setDataSource(getContext(), uri)
                }.embeddedPicture
            }
            CoreOptions.imageLoadAdapter?.onRequestLoad(embeddedPicture, AudioCoreOptions.defaultSongArtworkRes, target) ?: mediaApiError(102)
        } else return super.requestArtworkLoad(target)
    }

    @WorkerThread
    override fun requestArtworkBitmap(): Bitmap {
        if (AudioLocalOptions.useEmbeddedArtwork) {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(getContext(), uri)
            val embeddedPicture = retriever.embeddedPicture
            retriever.release()
            return CoreOptions.imageLoadAdapter?.onRequestBitmap(embeddedPicture, AudioCoreOptions.defaultSongArtworkRes) ?: mediaApiError(102)
        } else return super.requestArtworkBitmap()
    }

}