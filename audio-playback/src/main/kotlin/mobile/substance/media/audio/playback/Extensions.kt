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

package mobile.substance.media.audio.playback

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import mobile.substance.media.audio.playback.players.Playback
import mobile.substance.media.extensions.toFilePath
import mobile.substance.media.utils.AudioCoreUtil

fun MediaPlayer.prepareWithDataSource(context: Context, dataSource: Uri) {
    try {
        val url = dataSource.toString()
        Log.d("Checking url validity", url)
        if (!AudioCoreUtil.isHttpUrl(url)) setDataSource(context, dataSource) else setDataSource(url)
    } catch (e: Exception) {
        Log.e(Playback.TAG, "Unable to play " + dataSource.toFilePath(context), e)
    } finally {
        prepareAsync()
    }
}

fun MediaPlayer.destroy() {
    try {
        stop()
        reset()
        release()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun MediaSessionCompat.destroy() {
    isActive = false
    release()
}
