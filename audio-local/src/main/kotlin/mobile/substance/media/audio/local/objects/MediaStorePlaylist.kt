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

import android.net.Uri
import android.provider.MediaStore
import mobile.substance.media.audio.local.DateAdded
import mobile.substance.media.core.audio.Playlist
import mobile.substance.media.core.audio.Song
import mobile.substance.media.core.mediaApiError
import mobile.substance.media.local.core.MediaStoreAttributes
import mobile.substance.media.options.AudioLocalOptions

open class MediaStorePlaylist : Playlist(), MediaStoreAttributes, DateAdded {

    override var dateAdded: Long = 0

    override val baseUri: Uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI

    override var id: Long = -1

    override fun getSongs() = AudioLocalOptions.localAudioHolder?.findSongsForPlaylist(this) ?: mediaApiError(211)

}