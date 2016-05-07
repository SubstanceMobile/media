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

package mobile.substance.sdk.music.core;

/**
 * Created by Adrian on 07/05/2016.
 */
public class MusicOptions {

    private static volatile int DEFAULT_ART = 0;

    public static void setDefaultArt(int defaultArt) {
        DEFAULT_ART = defaultArt;
    }

    public static int getDefaultArt() {
        return DEFAULT_ART;
    }
}
