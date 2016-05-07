package mobile.substance.sdk.colors;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.graphics.Palette;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.Executor;

import static mobile.substance.sdk.colors.DynamicColorsConstants.ICON_COLOR_ACTIVE_DARK_BG;
import static mobile.substance.sdk.colors.DynamicColorsConstants.ICON_COLOR_ACTIVE_LIGHT_BG;
import static mobile.substance.sdk.colors.DynamicColorsConstants.ICON_COLOR_INACTIVE_DARK_BG;
import static mobile.substance.sdk.colors.DynamicColorsConstants.ICON_COLOR_INACTIVE_LIGHT_BG;

public class DynamicColors {
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

    ///////////////////////////////////////////////////////////////////////////
    // Methods for running.
    ///////////////////////////////////////////////////////////////////////////

    public void exec(Executor exec, DynamicColorsCallback callback, boolean... properties) {
        new DynamicColorsGenerator(callback).executeOnExecutor(exec, from, properties);
    }

    public void generate(DynamicColorsCallback callback, boolean useSmartTextPicking) {
        exec(AsyncTask.THREAD_POOL_EXECUTOR, callback, true, useSmartTextPicking);
    }

    public void generateOnExecutor(Executor executor, DynamicColorsCallback callback, boolean useSmartTextPicking) {
        exec(executor, callback, true, useSmartTextPicking);
    }

    public void generateSimple(DynamicColorsCallback callback) {
        exec(AsyncTask.THREAD_POOL_EXECUTOR, callback, false);
    }

    public void generateSimpleOnExecutor(Executor executor, DynamicColorsCallback callback) {
        exec(executor, callback, false);
    }

    ///////////////////////////////////////////////////////////////////////////
    // The actual task
    ///////////////////////////////////////////////////////////////////////////

    private static class DynamicColorsGenerator extends AsyncTask<Object, Void, ColorPackage> {
        private DynamicColorsCallback callback;

        DynamicColorsGenerator(DynamicColorsCallback callback) {
            this.callback = callback;
        }

        @Override
        protected ColorPackage doInBackground(Object... params) {
            Bitmap bitmap = null;
            if (params[0] instanceof Bitmap) bitmap = (Bitmap) params[0];
            if (params[0] instanceof String) bitmap = BitmapFactory.decodeFile((String) params[0]);
            if (bitmap == null) return null;

            Palette palette = Palette.from(bitmap).generate();
            if (params[1] instanceof Boolean && (Boolean) params[1]) {
                ArrayList<Palette.Swatch> sortedSwatches = new ArrayList<>(palette.getSwatches());
                Collections.sort(sortedSwatches, new Comparator<Palette.Swatch>() {
                    @Override
                    public int compare(Palette.Swatch a, Palette.Swatch b) {
                        return ((Integer) a.getPopulation()).compareTo(b.getPopulation());
                    }
                });

                try {
                    Palette.Swatch[] swatches = new Palette.Swatch[]{sortedSwatches.get(sortedSwatches.size() - 1), sortedSwatches.get(0)};
                    int primary = swatches[0].getRgb();
                    int accent = swatches[1].getRgb();

                    if (params[2] instanceof Boolean && (Boolean) params[2]) {
                        return new ColorPackage(primary, accent);
                    } else {
                        int title = swatches[0].getTitleTextColor(),
                                accentTitle = swatches[1].getTitleTextColor(),
                                accentSubtitle = swatches[1].getBodyTextColor();
                        return new ColorPackage(primary, DynamicColorsUtil.generatePrimaryDark(primary),
                                title, swatches[0].getBodyTextColor(), DynamicColorsUtil.makeDisabledColor(title, DynamicColorsUtil.isColorLight(title)),
                                accent, accentTitle, accentSubtitle, DynamicColorsUtil.makeDisabledColor(accentTitle, DynamicColorsUtil.isColorLight(accentTitle)),
                                DynamicColorsUtil.isColorLight(primary) ? ICON_COLOR_ACTIVE_LIGHT_BG : ICON_COLOR_ACTIVE_DARK_BG,
                                DynamicColorsUtil.isColorLight(primary) ? ICON_COLOR_INACTIVE_LIGHT_BG : ICON_COLOR_INACTIVE_DARK_BG,
                                DynamicColorsUtil.isColorLight(accent) ? ICON_COLOR_ACTIVE_LIGHT_BG : ICON_COLOR_ACTIVE_DARK_BG,
                                DynamicColorsUtil.isColorLight(accent) ? ICON_COLOR_INACTIVE_LIGHT_BG : ICON_COLOR_INACTIVE_DARK_BG);
                    }
                } catch (Exception e) {
                    return ColorsOptions.DEFAULT_COLORS;
                }
            } else {
                try {
                    return new ColorPackage(palette.getDarkVibrantColor(Color.BLACK), palette.getVibrantColor(Color.BLACK));
                } catch (Exception e) {
                    return ColorsOptions.DEFAULT_COLORS;
                }
            }
        }

        @Override
        protected void onPostExecute(ColorPackage colorPackage) {
            callback.onColorsReady(colorPackage);
        }

    }
}
