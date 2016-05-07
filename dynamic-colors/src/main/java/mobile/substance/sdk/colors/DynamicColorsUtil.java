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
import android.support.annotation.ColorInt;

/**
 * Created by Julian Os on 04.05.2016.
 */
public class DynamicColorsUtil {

    public static int generatePrimaryDark(int color) {
        return Color.rgb(Math.round(Color.red(color) * 0.80f), Math.round(Color.green(color) * 0.80f), Math.round(Color.blue(color) * 0.80f));
    }

    public static boolean isColorLight(@ColorInt int color) {
        return 1.0D - (0.299D * (double) Color.red(color) + 0.587D * (double) Color.green(color) + 0.114D * (double) Color.blue(color)) / 255.0D < 0.4D;
    }

    public static int makeDisabledColor(@ColorInt int color, boolean isLight) {
        int alpha = Math.round(Color.alpha(color) * (isLight ? 0.5f : 0.38f));
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

}
