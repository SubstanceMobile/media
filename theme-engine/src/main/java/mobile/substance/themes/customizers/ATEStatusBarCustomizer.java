package mobile.substance.themes.customizers;

import android.support.annotation.ColorInt;

import mobile.substance.themes.Config;

/**
 * @author Aidan Follestad (afollestad)
 */
public interface ATEStatusBarCustomizer {

    @ColorInt
    int getStatusBarColor();

    @Config.LightStatusBarMode
    int getLightStatusBarMode();
}
