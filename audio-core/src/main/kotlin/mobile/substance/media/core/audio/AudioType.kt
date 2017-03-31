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

import android.support.annotation.IntDef

@IntDef(AUDIO_TYPE_SONGS, AUDIO_TYPE_ALBUMS, AUDIO_TYPE_ARTISTS, AUDIO_TYPE_PLAYLISTS, AUDIO_TYPE_GENRES)
annotation class AudioType
const val AUDIO_TYPE_SONGS = 1L
const val AUDIO_TYPE_ALBUMS = 2L
const val AUDIO_TYPE_ARTISTS = 3L
const val AUDIO_TYPE_PLAYLISTS = 4L
const val AUDIO_TYPE_GENRES = 5L

