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
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import android.support.annotation.WorkerThread
import android.widget.ImageView
import co.metalab.asyncawait.async
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import mobile.substance.media.audio.local.DateAdded
import mobile.substance.media.audio.local.MediaStoreAudioHolder
import mobile.substance.media.core.audio.Album
import mobile.substance.media.core.audio.Artist
import mobile.substance.media.core.audio.Genre
import mobile.substance.media.core.audio.Song
import mobile.substance.media.local.core.MediaStoreAttributes
import mobile.substance.media.options.AudioCoreOptions
import mobile.substance.media.options.AudioLocalOptions

class MediaStoreSong : Song(), MediaStoreAttributes, DateAdded {
    var albumId: Long = -1
    var artistId: Long = -1

    override var artworkUri: Uri? = null
        get() = getAlbum()?.artworkUri

    override var dateAdded: Long = -1

    override var id: Long = -1

    override val baseUri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

    override val uri: Uri
        get() = contentUri

    override fun getArtist(): Artist? = MediaStoreAudioHolder.findArtistById(artistId)

    override fun getAlbum(): Album? = MediaStoreAudioHolder.findAlbumById(albumId)

    override fun getGenre(): Genre? = MediaStoreAudioHolder.findGenreForSong(this)

    override fun loadArtwork(target: ImageView) {
        if (AudioLocalOptions.useEmbeddedArtwork) async {
            val embeddedPicture = await {
                MediaMetadataRetriever().apply {
                    setDataSource(getContext(), uri)
                }.embeddedPicture
            }
            Glide.with(getContext())
                    .load(embeddedPicture)
                    .placeholder(AudioCoreOptions.defaultArtResId)
                    .error(AudioCoreOptions.defaultArtResId)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .crossFade()
                    .centerCrop()
                    .into(target)
        } else return super.loadArtwork(target)
    }

    @WorkerThread
    override fun getArtwork(): Bitmap? {
        if (AudioLocalOptions.useEmbeddedArtwork) {
            val embeddedPicture = MediaMetadataRetriever().apply {
                setDataSource(getContext(), uri)
            }.embeddedPicture
            return BitmapFactory.decodeByteArray(embeddedPicture, 0, embeddedPicture.size)
        } else return super.getArtwork()
    }

}