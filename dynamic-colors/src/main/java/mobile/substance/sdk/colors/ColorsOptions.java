package mobile.substance.sdk.colors;

import android.graphics.Color;

/**
 * Created by Adrian on 04/05/2016.
 * This class will act as a way to configure this library
 */
public class ColorsOptions {
    static volatile ColorPackage DEFAULT_COLORS = new ColorPackage(Color.WHITE, Color.BLACK);

    /**
     * Set the {@link ColorPackage} used as a default
     */
    public static void setDefaultColors(ColorPackage defaultColors) {
        DEFAULT_COLORS = defaultColors;
    }

}
