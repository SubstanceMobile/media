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

import mobile.substance.sdk.colors.ColorConstants.ICON_COLOR_ACTIVE_DARK_BG
import mobile.substance.sdk.colors.ColorConstants.ICON_COLOR_ACTIVE_LIGHT_BG
import mobile.substance.sdk.colors.ColorConstants.ICON_COLOR_INACTIVE_DARK_BG
import mobile.substance.sdk.colors.ColorConstants.ICON_COLOR_INACTIVE_LIGHT_BG
import mobile.substance.sdk.colors.ColorConstants.TEXT_COLOR_DARK_BG
import mobile.substance.sdk.colors.ColorConstants.TEXT_COLOR_DISABLED_DARK_BG
import mobile.substance.sdk.colors.ColorConstants.TEXT_COLOR_DISABLED_LIGHT_BG
import mobile.substance.sdk.colors.ColorConstants.TEXT_COLOR_LIGHT_BG
import mobile.substance.sdk.colors.ColorConstants.TEXT_COLOR_SECONDARY_DARK_BG
import mobile.substance.sdk.colors.ColorConstants.TEXT_COLOR_SECONDARY_LIGHT_BG

/**
 * Created by Julian Os on 04.05.2016.
 */
class ColorPackage(
        ///////////////////////////////////////////////////////////////////////////
        // Primary Colors
        ///////////////////////////////////////////////////////////////////////////
        val primaryColor: Int, val primaryDarkColor: Int, val textColor: Int, val secondaryTextColor: Int, val disbledTextColor: Int,
        ///////////////////////////////////////////////////////////////////////////
        // Accent Colors
        ///////////////////////////////////////////////////////////////////////////
        val accentColor: Int, val accentTextColor: Int, val accentSecondaryTextColor: Int, val accentDisabledTextColor: Int,
        ///////////////////////////////////////////////////////////////////////////
        // Icons
        ///////////////////////////////////////////////////////////////////////////
        val iconActiveColor: Int, val iconInactiveColor: Int, val accentIconActiveColor: Int, val accentIconInactiveColor: Int)
{

    constructor(primaryColor: Int, accentColor: Int) : this(primaryColor, DynamicColorsUtil.generatePrimaryDark(primaryColor), accentColor)

    constructor(primaryColor: Int, primaryDarkColor: Int, accentColor: Int) : this(primaryColor, primaryDarkColor,
            if (DynamicColorsUtil.isColorLight(primaryColor)) TEXT_COLOR_LIGHT_BG else TEXT_COLOR_DARK_BG,
            if (DynamicColorsUtil.isColorLight(primaryColor)) TEXT_COLOR_SECONDARY_LIGHT_BG else TEXT_COLOR_SECONDARY_DARK_BG,
            if (DynamicColorsUtil.isColorLight(primaryColor)) TEXT_COLOR_DISABLED_LIGHT_BG else TEXT_COLOR_DISABLED_DARK_BG,
            accentColor,
            if (DynamicColorsUtil.isColorLight(accentColor)) TEXT_COLOR_LIGHT_BG else TEXT_COLOR_DARK_BG,
            if (DynamicColorsUtil.isColorLight(accentColor)) TEXT_COLOR_SECONDARY_LIGHT_BG else TEXT_COLOR_SECONDARY_DARK_BG,
            if (DynamicColorsUtil.isColorLight(accentColor)) TEXT_COLOR_DISABLED_LIGHT_BG else TEXT_COLOR_DISABLED_DARK_BG,
            if (DynamicColorsUtil.isColorLight(primaryColor)) ICON_COLOR_ACTIVE_LIGHT_BG else ICON_COLOR_ACTIVE_DARK_BG,
            if (DynamicColorsUtil.isColorLight(primaryColor)) ICON_COLOR_INACTIVE_LIGHT_BG else ICON_COLOR_INACTIVE_DARK_BG,
            if (DynamicColorsUtil.isColorLight(accentColor)) ICON_COLOR_ACTIVE_LIGHT_BG else ICON_COLOR_ACTIVE_DARK_BG,
            if (DynamicColorsUtil.isColorLight(accentColor)) ICON_COLOR_INACTIVE_LIGHT_BG else ICON_COLOR_INACTIVE_DARK_BG)
}
