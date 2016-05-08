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

package mobile.substance.sdk.colors;

import android.graphics.Color;

/**
 * This class will act as a way to configure this library
 */
public class DynamicColorsOptions {
    static volatile ColorPackage DEFAULT_COLORS = new ColorPackage(Color.WHITE, Color.BLACK);
    static volatile DynamicColorsCallback DEFAULT_CALLBACK = new DynamicColorsCallback() {
        @Override
        public void onColorsReady(ColorPackage colors) {
            //Do nothing.
        }
    };

    public static void setDefaultColors(ColorPackage defaultColors) {
        DEFAULT_COLORS = defaultColors;
    }

    public static ColorPackage getDefaultColors() {
        return DEFAULT_COLORS;
    }

    public static DynamicColorsCallback getDefaultCallback() {
        return DEFAULT_CALLBACK;
    }

    public static void setDefaultCallback(DynamicColorsCallback defaultCallback) {
        DEFAULT_CALLBACK = defaultCallback;
    }
}
