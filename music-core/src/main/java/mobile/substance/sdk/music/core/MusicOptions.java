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

    private static volatile int DEFAULT_ART, STATUSBAR_ICON_RESID = 0;
    private static volatile String CAST_APPLICATION_ID;

    public static int getDefaultArt() {
        return DEFAULT_ART;
    }

    public static void setDefaultArt(int defaultArt) {
        DEFAULT_ART = defaultArt;
    }

    public static int getStatusbarIconResId() {
        return STATUSBAR_ICON_RESID;
    }

    public static void setStatusbarIconResId(int resid) {
        STATUSBAR_ICON_RESID = resid;
    }

    public static void setCastApplicationId(String applicationId) {
        CAST_APPLICATION_ID = applicationId;

    }

    public static String getCastApplicationId() {
        return CAST_APPLICATION_ID;
    }
}
