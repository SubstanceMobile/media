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

package mobile.substance.sdk.colors

import android.graphics.Color

/**
 * Created by Julian Os on 04.05.2016.
 */
object DynamicColorsUtil {

    fun generatePrimaryDark(color: Int): Int {
        return Color.rgb(Math.round(Color.red(color) * 0.80f), Math.round(Color.green(color) * 0.80f), Math.round(Color.blue(color) * 0.80f))
    }

    fun isColorLight(color: Int): Boolean {
        return 1.0 - (0.299 * Color.red(color).toDouble() + 0.587 * Color.green(color).toDouble() + 0.114 * Color.blue(color).toDouble()) / 255.0 < 0.4
    }

    fun makeDisabledColor(color: Int, isLight: Boolean): Int {
        val alpha = Math.round(Color.alpha(color) * if (isLight) 0.5f else 0.38f)
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        return Color.argb(alpha, red, green, blue)
    }

    fun hexStringForInt(color: Int): String {
        return java.lang.String.format("#%06X", 0xFFFFFF and color)
    }

}
