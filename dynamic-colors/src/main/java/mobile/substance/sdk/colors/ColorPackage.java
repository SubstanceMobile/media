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

import static mobile.substance.sdk.colors.DynamicColorsConstants.ICON_COLOR_ACTIVE_DARK_BG;
import static mobile.substance.sdk.colors.DynamicColorsConstants.ICON_COLOR_ACTIVE_LIGHT_BG;
import static mobile.substance.sdk.colors.DynamicColorsConstants.ICON_COLOR_INACTIVE_DARK_BG;
import static mobile.substance.sdk.colors.DynamicColorsConstants.ICON_COLOR_INACTIVE_LIGHT_BG;
import static mobile.substance.sdk.colors.DynamicColorsConstants.TEXT_COLOR_DARK_BG;
import static mobile.substance.sdk.colors.DynamicColorsConstants.TEXT_COLOR_DISABLED_DARK_BG;
import static mobile.substance.sdk.colors.DynamicColorsConstants.TEXT_COLOR_DISABLED_LIGHT_BG;
import static mobile.substance.sdk.colors.DynamicColorsConstants.TEXT_COLOR_LIGHT_BG;
import static mobile.substance.sdk.colors.DynamicColorsConstants.TEXT_COLOR_SECONDARY_DARK_BG;
import static mobile.substance.sdk.colors.DynamicColorsConstants.TEXT_COLOR_SECONDARY_LIGHT_BG;

/**
 * Created by Julian Os on 04.05.2016.
 */
public class ColorPackage {
    private int primaryColor, primaryDarkColor, textColor, secondaryTextColor, disbledTextColor,
        accentColor, accentTextColor, accentSecondaryTextColor, accentDisabledTextColor,
        iconActiveColor, iconInactiveColor, accentIconActiveColor, accentIconInactiveColor;

    public ColorPackage(int primaryColor, int primaryDarkColor, int textColor, int secondaryTextColor, int disabledTextColor,
                        int accentColor, int accentTextColor, int accentSecondaryTextColor, int accentDisabledTextColor,
                        int iconActiveColor, int iconInactiveColor, int accentIconActiveColor, int accentIconInactiveColor) {
        //Main Colors
        this.primaryColor = primaryColor;
        this.primaryDarkColor = primaryDarkColor;
        this.textColor = textColor;
        this.secondaryTextColor = secondaryTextColor;
        this.disbledTextColor = disabledTextColor;

        //Accent Colors
        this.accentColor = accentColor;
        this.accentTextColor = accentTextColor;
        this.accentSecondaryTextColor = accentSecondaryTextColor;
        this.accentDisabledTextColor = accentDisabledTextColor;

        //Icon colors
        this.iconActiveColor = iconActiveColor;
        this.iconInactiveColor = iconInactiveColor;
        this.accentIconActiveColor = accentIconActiveColor;
        this.accentIconInactiveColor = accentIconInactiveColor;
    }

    public ColorPackage(int primaryColor, int accentColor){
        this(primaryColor, DynamicColorsUtil.generatePrimaryDark(primaryColor), accentColor);
    }

    public ColorPackage(int primaryColor, int primaryDarkColor, int accentColor) {
        this(primaryColor, primaryDarkColor,
                DynamicColorsUtil.isColorLight(primaryColor) ? TEXT_COLOR_LIGHT_BG : TEXT_COLOR_DARK_BG,
                DynamicColorsUtil.isColorLight(primaryColor) ? TEXT_COLOR_SECONDARY_LIGHT_BG : TEXT_COLOR_SECONDARY_DARK_BG,
                DynamicColorsUtil.isColorLight(primaryColor) ? TEXT_COLOR_DISABLED_LIGHT_BG : TEXT_COLOR_DISABLED_DARK_BG,
                accentColor,
                DynamicColorsUtil.isColorLight(accentColor) ? TEXT_COLOR_LIGHT_BG : TEXT_COLOR_DARK_BG,
                DynamicColorsUtil.isColorLight(accentColor) ? TEXT_COLOR_SECONDARY_LIGHT_BG : TEXT_COLOR_SECONDARY_DARK_BG,
                DynamicColorsUtil.isColorLight(accentColor) ? TEXT_COLOR_DISABLED_LIGHT_BG : TEXT_COLOR_DISABLED_DARK_BG,
                DynamicColorsUtil.isColorLight(primaryColor) ? ICON_COLOR_ACTIVE_LIGHT_BG : ICON_COLOR_ACTIVE_DARK_BG,
                DynamicColorsUtil.isColorLight(primaryColor) ? ICON_COLOR_INACTIVE_LIGHT_BG : ICON_COLOR_INACTIVE_DARK_BG,
                DynamicColorsUtil.isColorLight(accentColor) ? ICON_COLOR_ACTIVE_LIGHT_BG : ICON_COLOR_ACTIVE_DARK_BG,
                DynamicColorsUtil.isColorLight(accentColor) ? ICON_COLOR_INACTIVE_LIGHT_BG : ICON_COLOR_INACTIVE_DARK_BG);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Primary Colors
    ///////////////////////////////////////////////////////////////////////////

    public int getPrimaryColor() {
        return primaryColor;
    }

    public int getPrimaryDarkColor() {
        return primaryDarkColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public int getSecondaryTextColor() {
        return secondaryTextColor;
    }

    public int getDisbledTextColor() {
        return disbledTextColor;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Accent Colors
    ///////////////////////////////////////////////////////////////////////////

    public int getAccentColor() {
        return accentColor;
    }

    public int getAccentSecondaryTextColor() {
        return accentSecondaryTextColor;
    }

    public int getAccentTextColor() {
        return accentTextColor;
    }

    public int getAccentDisabledTextColor() {
        return accentDisabledTextColor;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Icons
    ///////////////////////////////////////////////////////////////////////////

    public int getIconActiveColor() {
        return iconActiveColor;
    }

    public int getIconInactiveColor() {
        return iconInactiveColor;
    }

    public int getAccentIconActiveColor() {
        return accentIconActiveColor;
    }

    public int getAccentIconInactiveColor() {
        return accentIconInactiveColor;
    }
}
