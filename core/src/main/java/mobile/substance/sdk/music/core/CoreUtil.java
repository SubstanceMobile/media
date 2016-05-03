package mobile.substance.sdk.music.core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.TypedValue;

public class CoreUtil {

    private CoreUtil() {
        //No "new" statements
    }

    /**
     * Convenience method that simplifies calling intents and such
     *
     * @param cxt The context to start the activity from
     * @param url The url so start
     */
    public static void startUrl(Context cxt, String url) {
        cxt.startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url)));
    }

    /**
     * Formats strings to match time. Is either hh:mm:ss or mm:ss
     *
     * @param time The raw time in ms
     * @return The formatted string value
     */
    @SuppressLint("DefaultLocale")
    public static String stringForTime(long time) {
        int totalSeconds = (int) time / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        return hours > 0 ? String.format("%d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Android Version Utils
    ///////////////////////////////////////////////////////////////////////////

    public static boolean isLollipop() {
        return Build.VERSION.SDK_INT >= 21;
    }

    public static boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= 23;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Unit conversions
    ///////////////////////////////////////////////////////////////////////////

    public static float dpToPx(Context context, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}
