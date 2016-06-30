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
import com.google.android.gms.cast.framework.CastOptions
import com.google.android.gms.cast.framework.OptionsProvider
import com.google.android.gms.cast.framework.SessionProvider
import mobile.substance.sdk.music.playback.MusicPlaybackOptions
import mobile.substance.sdk.music.playback.MusicPlaybackUtil
import java.util.*

class CastOptionsProvider : OptionsProvider {

    companion object {
        const val MEDIA_NAMESPACE = "urn:x-cast:com.google.cast.media"
    }

    override fun getAdditionalSessionProviders(p0: Context?): MutableList<SessionProvider>? {
        return null
    }

    override fun getCastOptions(p0: Context?): CastOptions? {
        val namespaces = ArrayList<String>()
        namespaces.add(MEDIA_NAMESPACE)

        return CastOptions.Builder()
                .setReceiverApplicationId(MusicPlaybackOptions.castApplicationId)
                .setEnableReconnectionService(true)
                .setResumeSavedSession(true)
                .setSupportedNamespaces(namespaces)
                .build()
    }

}