package mobile.substance.themes.inflation;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ListView;

import mobile.substance.themes.ATEActivity;
import mobile.substance.themes.tagprocessors.ATEDefaultTags;


/**
 * @author Aidan Follestad (afollestad)
 */
class ATEListView extends ListView implements ViewInterface {

    public ATEListView(Context context) {
        super(context);
        init(context, null);
    }

    public ATEListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, null);
    }

    public ATEListView(Context context, AttributeSet attrs, @Nullable ATEActivity keyContext) {
        super(context, attrs);
        init(context, keyContext);
    }

    private void init(Context context, @Nullable ATEActivity keyContext) {
        ATEDefaultTags.process(this);
        ATEViewUtil.init(keyContext, this, context);
    }

    @Override
    public boolean setsStatusBarColor() {
        return false;
    }

    @Override
    public boolean setsToolbarColor() {
        return false;
    }
}
