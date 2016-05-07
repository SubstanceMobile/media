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

package mobile.substance.themes.util;

import android.support.design.widget.TabLayout;
import android.view.View;

/**
 * @author Aidan Follestad (afollestad)
 */
public final class TabLayoutUtil {

    // External class is used after checking if TabLayout is on the class path. Avoids compile errors.
    public static boolean isTabLayout(View view) {
        return view instanceof TabLayout;
    }

    private TabLayoutUtil() {
    }
}