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

package mobile.substance.sdk

import android.app.Application
import mobile.substance.sdk.music.core.MusicCoreOptions

/**
 * Created by Julian Os on 09.05.2016.
 */

class SDKApp : Application() {


    override fun onCreate() {
        super.onCreate()

        Thread() {
            run {
                MusicCoreOptions.setDefaultArt(R.drawable.default_artwork_gem)
                MusicCoreOptions.setStatusbarIconResId(R.drawable.ic_audiotrack_white_24dp)
            }
        }.start()
    }

}