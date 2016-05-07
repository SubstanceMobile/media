package mobile.substance.themes;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;

import java.util.HashMap;

import mobile.substance.themes.tagprocessors.BackgroundTagProcessor;
import mobile.substance.themes.tagprocessors.EdgeGlowTagProcessor;
import mobile.substance.themes.tagprocessors.FontTagProcessor;
import mobile.substance.themes.tagprocessors.TabLayoutTagProcessor;
import mobile.substance.themes.tagprocessors.TagProcessor;
import mobile.substance.themes.tagprocessors.TextColorTagProcessor;
import mobile.substance.themes.tagprocessors.TextShadowColorTagProcessor;
import mobile.substance.themes.tagprocessors.TextSizeTagProcessor;
import mobile.substance.themes.tagprocessors.TintTagProcessor;
import mobile.substance.themes.util.ATEUtil;
import mobile.substance.themes.viewprocessors.DefaultProcessor;
import mobile.substance.themes.viewprocessors.NavigationViewProcessor;
import mobile.substance.themes.viewprocessors.SearchViewProcessor;
import mobile.substance.themes.viewprocessors.ToolbarProcessor;
import mobile.substance.themes.viewprocessors.ViewProcessor;

/**
 * @author Aidan Follestad (afollestad)
 */
class ATEBase {

    protected final static String DEFAULT_PROCESSOR = "[default]";

    private static HashMap<String, ViewProcessor> mViewProcessors;
    private static HashMap<String, TagProcessor> mTagProcessors;

    private static void initViewProcessors() {
        mViewProcessors = new HashMap<>(5);
        mViewProcessors.put(DEFAULT_PROCESSOR, new DefaultProcessor());

        mViewProcessors.put(SearchView.class.getName(), new SearchViewProcessor());
        mViewProcessors.put(Toolbar.class.getName(), new ToolbarProcessor());

        if (ATEUtil.isInClassPath(NavigationViewProcessor.MAIN_CLASS))
            mViewProcessors.put(NavigationViewProcessor.MAIN_CLASS, new NavigationViewProcessor());
        else Log.d("ATEBase", "NavigationView isn't in the class path. Ignoring.");
        if (ATEUtil.isInClassPath(SearchViewProcessor.MAIN_CLASS))
            mViewProcessors.put(SearchViewProcessor.MAIN_CLASS, new SearchViewProcessor());
        else Log.d("ATEBase", "SearchView isn't in the class path. Ignoring.");
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T extends View> ViewProcessor<T, ?> getViewProcessor(@Nullable Class<T> viewClass) {
        if (mViewProcessors == null)
            initViewProcessors();
        if (viewClass == null)
            return mViewProcessors.get(DEFAULT_PROCESSOR);
        ViewProcessor viewProcessor = mViewProcessors.get(viewClass.getName());
        if (viewProcessor != null)
            return viewProcessor;
        Class<?> current = viewClass;
        while (true) {
            current = current.getSuperclass();
            if (current == null || current.getName().equals(View.class.getName()))
                break;
            viewProcessor = mViewProcessors.get(current.getName());
            if (viewProcessor != null) break;
        }
        return viewProcessor;
    }

    public static <T extends View> void registerViewProcessor(@NonNull Class<T> viewCls, @NonNull ViewProcessor<T, ?> viewProcessor) {
        if (mViewProcessors == null)
            initViewProcessors();
        mViewProcessors.put(viewCls.getName(), viewProcessor);
    }

    private static void initTagProcessors() {
        mTagProcessors = new HashMap<>(14);
        mTagProcessors.put(BackgroundTagProcessor.PREFIX, new BackgroundTagProcessor());
        mTagProcessors.put(FontTagProcessor.PREFIX, new FontTagProcessor());
        mTagProcessors.put(TextColorTagProcessor.PREFIX, new TextColorTagProcessor(false, false));
        mTagProcessors.put(TextColorTagProcessor.LINK_PREFIX, new TextColorTagProcessor(true, false));
        mTagProcessors.put(TextColorTagProcessor.HINT_PREFIX, new TextColorTagProcessor(false, true));
        mTagProcessors.put(TextShadowColorTagProcessor.PREFIX, new TextShadowColorTagProcessor());
        mTagProcessors.put(TextSizeTagProcessor.PREFIX, new TextSizeTagProcessor());
        mTagProcessors.put(TintTagProcessor.PREFIX, new TintTagProcessor(false, false, false));
        mTagProcessors.put(TintTagProcessor.BACKGROUND_PREFIX, new TintTagProcessor(true, false, false));
        mTagProcessors.put(TintTagProcessor.SELECTOR_PREFIX, new TintTagProcessor(false, true, false));
        mTagProcessors.put(TintTagProcessor.SELECTOR_PREFIX_LIGHT, new TintTagProcessor(false, true, true));
        mTagProcessors.put(TabLayoutTagProcessor.TEXT_PREFIX, new TabLayoutTagProcessor(true, false));
        mTagProcessors.put(TabLayoutTagProcessor.INDICATOR_PREFIX, new TabLayoutTagProcessor(false, true));
        mTagProcessors.put(EdgeGlowTagProcessor.PREFIX, new EdgeGlowTagProcessor());
    }

    @Nullable
    public static TagProcessor getTagProcessor(@NonNull String prefix) {
        if (mTagProcessors == null)
            initTagProcessors();
        return mTagProcessors.get(prefix);
    }

    public static void registerTagProcessor(@NonNull String prefix, @NonNull TagProcessor tagProcessor) {
        if (mTagProcessors == null)
            initTagProcessors();
        mTagProcessors.put(prefix, tagProcessor);
    }

    protected static Class<?> didPreApply = null;
}