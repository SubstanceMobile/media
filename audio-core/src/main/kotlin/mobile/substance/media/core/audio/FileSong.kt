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

package mobile.substance.media.core.audio

import android.media.MediaMetadataRetriever
import android.net.Uri
import android.support.annotation.WorkerThread

@WorkerThread
class FileSong(override val uri: Uri) : Song() {

    init {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(getContext(), uri)
        title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: uri.toString().substring(uri.toString().lastIndexOf("/") + 1)
        artistName = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: "<unknown>"
        albumTitle = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM) ?: "<unknown>"
        duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION).toLong()
        retriever.release()
    }

    override fun getArtist(): Artist? = null

    override fun getAlbum(): Album? = null

    override fun getGenre(): Genre? = null

}