package mobile.substance.themes.inflation;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;

import mobile.substance.themes.ATE;
import mobile.substance.themes.ATEActivity;

/**
 * @author Aidan Follestad (afollestad)
 */
class ATEViewUtil {

    public static String init(@Nullable ATEActivity keyContext, View view, Context context) {
        if (keyContext == null && context instanceof ATEActivity)
            keyContext = (ATEActivity) context;
        String key = null;
        if (keyContext != null)
            key = keyContext.getATEKey();
        // Process views just once (during inflation)
        if (view.isLayoutRequested())
            ATE.themeView(context, view, key);
        return key;
    }

    private ATEViewUtil() {
    }
}