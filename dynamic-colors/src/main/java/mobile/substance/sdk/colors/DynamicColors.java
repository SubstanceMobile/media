package mobile.substance.sdk.colors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.graphics.Palette;

import java.io.File;
import java.util.concurrent.Executor;

public class DynamicColors {
    private static final int COLOR_DEFAULT = Color.BLACK;
    private Object from;

    private DynamicColors(Object from) {
        this.from = from;
    }

    public static DynamicColors from(Bitmap image) {
        return new DynamicColors(image);
    }

    public static DynamicColors from(Uri image) {
        return new DynamicColors(image.getPath());
    }

    public static DynamicColors from(String path) {
        return new DynamicColors(path);
    }

    public static DynamicColors from(File image) {
        return new DynamicColors(image.getPath());
    }

    public void generate(Context context, DynamicColorsCallback callback, boolean useSmartPicking) {
        if (useSmartPicking) {
            new DynamicColorsGenerator.SmartPicking(context, callback).execute(from);
        } else new DynamicColorsGenerator(context, callback).execute(from);

    }

    public void generateOnExecutor(Executor exec, Context context, DynamicColorsCallback callback, boolean useSmartPicking) {
        if (useSmartPicking) {
            new DynamicColorsGenerator.SmartPicking(context, callback).executeOnExecutor(exec, from);
        } else new DynamicColorsGenerator(context, callback).executeOnExecutor(exec, from);
    }

    private static class DynamicColorsGenerator extends AsyncTask<Object, Void, ColorPackage> {
        private Context context;
        private DynamicColorsCallback callback;

        DynamicColorsGenerator(Context context, DynamicColorsCallback callback) {
            this.context = context;
            this.callback = callback;
        }

        @Override
        protected ColorPackage doInBackground(Object... params) {
            Bitmap bitmap = null;
            if (params[0] instanceof Bitmap) bitmap = (Bitmap) params[0];
            if (params[0] instanceof String) bitmap = BitmapFactory.decodeFile((String) params[0]);

            if (bitmap == null) return null;

            Palette palette = Palette.from(bitmap).generate();

            int primary = palette.getDarkVibrantColor(COLOR_DEFAULT);
            int accent = palette.getVibrantColor(COLOR_DEFAULT);

            palette = null;

            int primaryDark = DynamicColorsUtil.generatePrimaryDark(primary);

            boolean isPrimaryLight = DynamicColorsUtil.isColorLight(primary);
            boolean isAccentLight = DynamicColorsUtil.isColorLight(accent);

            int textColorPrimary = isPrimaryLight ? DynamicColorsConstants.TEXT_COLOR_PRIMARY_LIGHT_BG : DynamicColorsConstants.TEXT_COLOR_PRIMARY_DARK_BG;
            int textColorSecondary = isPrimaryLight ? DynamicColorsConstants.TEXT_COLOR_SECONDARY_LIGHT_BG : DynamicColorsConstants.TEXT_COLOR_SECONDARY_DARK_BG;
            int iconColorActive = isPrimaryLight ? DynamicColorsConstants.ICON_COLOR_ACTIVE_LIGHT_BG : DynamicColorsConstants.ICON_COLOR_ACTIVE_DARK_BG;
            int iconColorInactive = isPrimaryLight ? DynamicColorsConstants.ICON_COLOR_INACTIVE_LIGHT_BG : DynamicColorsConstants.ICON_COLOR_INACTIVE_DARK_BG;
            int iconColorAccent = isAccentLight ? DynamicColorsConstants.ICON_COLOR_ACTIVE_LIGHT_BG : DynamicColorsConstants.ICON_COLOR_ACTIVE_DARK_BG;

            return new ColorPackage(primary, primaryDark, accent, textColorPrimary, textColorSecondary, iconColorActive, iconColorInactive, iconColorAccent);
        }

        @Override
        protected void onPostExecute(ColorPackage colorPackage) {
            callback.onColorsReady(colorPackage);
        }

        private static class SmartPicking extends DynamicColorsGenerator {

            SmartPicking(Context context, DynamicColorsCallback callback) {
                super(context, callback);
            }

            @Override
            protected ColorPackage doInBackground(Object... params) {
                return null;
            }
        }

    }
}
