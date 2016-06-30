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

package mobile.substance.sdk.music.playback.cast

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException

import fi.iki.elonen.NanoHTTPD
import mobile.substance.sdk.music.core.utils.CoreUtil
import mobile.substance.sdk.music.loading.Library
import mobile.substance.sdk.music.playback.MusicPlaybackUtil
import mobile.substance.sdk.music.playback.MusicQueue
import mobile.substance.sdk.music.playback.PlaybackRemote

/**
 * Created by Julian Os on 13.02.2016.
 */
class LocalServer(internal var type: Int, private val context: Context) : NanoHTTPD(MusicPlaybackUtil.getServerportForType(type)) {

    fun serve(uri: Uri): NanoHTTPD.Response? {
        try {
            val mimeType = if (type == MusicPlaybackUtil.SERVER_TYPE_AUDIO) "audio/*" else "image/*"
            return NanoHTTPD.Response(NanoHTTPD.Response.Status.OK, mimeType, FileInputStream(File(CoreUtil.getFilePath(context, uri))))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            return null
        }
    }

    companion object {
        val TAG = LocalServer::class.java.simpleName
    }
}
