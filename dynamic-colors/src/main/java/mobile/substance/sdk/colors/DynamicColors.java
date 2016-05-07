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

    public void generateOnExecutioner(Executor executor, DynamicColorsCallback callback, boolean useSmartTextPicking) {
        exec(executor, callback, true, useSmartTextPicking);
    }

    public void generateSimple(DynamicColorsCallback callback) {
        exec(AsyncTask.THREAD_POOL_EXECUTOR, callback, false);
    }

    public void generateSimpleOnExecutioner(Executor executor, DynamicColorsCallback callback) {
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

            Boolean useSmartColor = false;
            if (params[1] instanceof Boolean) useSmartColor = (Boolean) params[1];

            Palette palette = Palette.from(bitmap).generate();
            if (useSmartColor) {
                Boolean useSmartTextColors = false;
                if (params[1] instanceof Boolean) useSmartTextColors = (Boolean) params[1];

                //Gets main swatches
                ArrayList<Palette.Swatch> sortedSwatches = new ArrayList<>(palette.getSwatches());
                Collections.sort(sortedSwatches, new Comparator<Palette.Swatch>() {
                    @Override
                    public int compare(Palette.Swatch a, Palette.Swatch b) {
                        return ((Integer) a.getPopulation()).compareTo(b.getPopulation());
                    }
                });

                //Applies swatches to album
                try {
                    Palette.Swatch[] swatches = new Palette.Swatch[]{sortedSwatches.get(sortedSwatches.size() - 1), sortedSwatches.get(0)};

                    int primary = swatches[0].getRgb();
                    boolean isPrimaryLight = DynamicColorsUtil.isColorLight(primary);
                    int title = useSmartTextColors ? swatches[0].getBodyTextColor() :
                            (isPrimaryLight ? DynamicColorsConstants.TEXT_COLOR_DARK : DynamicColorsConstants.TEXT_COLOR_LIGHT),
                            subtitle = useSmartTextColors ? swatches[0].getTitleTextColor() :
                                    (isPrimaryLight ? DynamicColorsConstants.TEXT_COLOR_SECONDARY_LIGHT : DynamicColorsConstants.TEXT_COLOR_SECONDARY_DARK);

                    int accent = swatches[1].getRgb();
                    boolean isAccentLight = DynamicColorsUtil.isColorLight(accent);
                    int accentIcon = useSmartTextColors ? swatches[1].getBodyTextColor() :
                            (isAccentLight ? DynamicColorsConstants.ICON_COLOR_ACTIVE_LIGHT_BG : DynamicColorsConstants.ICON_COLOR_ACTIVE_DARK_BG),
                            accentSubIcon = useSmartTextColors ? swatches[1].getTitleTextColor() :
                                    (isAccentLight ? DynamicColorsConstants.ICON_COLOR_INACTIVE_LIGHT_BG : DynamicColorsConstants.ICON_COLOR_INACTIVE_DARK_BG);

                    return new ColorPackage(primary, DynamicColorsUtil.generatePrimaryDark(primary), title, subtitle,
                            accent, accentIcon, accentSubIcon);
                } catch (Exception e) {
                    return ColorsOptions.DEFAULT_COLORS;
                }
            } else {
                try {
                    int primary = palette.getDarkVibrantColor(Color.BLACK),
                            accent = palette.getVibrantColor(Color.BLACK);
                    boolean isPrimaryLight = DynamicColorsUtil.isColorLight(primary),
                            isAccentLight = DynamicColorsUtil.isColorLight(accent);

                    int textColorPrimary = isPrimaryLight ? DynamicColorsConstants.TEXT_COLOR_DARK : DynamicColorsConstants.TEXT_COLOR_LIGHT,
                            textColorSecondary = isPrimaryLight ? DynamicColorsConstants.TEXT_COLOR_SECONDARY_LIGHT : DynamicColorsConstants.TEXT_COLOR_SECONDARY_DARK;
                    int accentIcon = isAccentLight ? DynamicColorsConstants.ICON_COLOR_ACTIVE_LIGHT_BG : DynamicColorsConstants.ICON_COLOR_ACTIVE_DARK_BG,
                            accentSubIcon = isAccentLight ? DynamicColorsConstants.ICON_COLOR_INACTIVE_LIGHT_BG : DynamicColorsConstants.ICON_COLOR_INACTIVE_DARK_BG;

                    return new ColorPackage(primary, DynamicColorsUtil.generatePrimaryDark(primary), textColorPrimary, textColorSecondary,
                            accent, accentIcon, accentSubIcon);
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
