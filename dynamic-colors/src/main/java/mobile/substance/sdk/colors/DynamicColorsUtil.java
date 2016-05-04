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


}
