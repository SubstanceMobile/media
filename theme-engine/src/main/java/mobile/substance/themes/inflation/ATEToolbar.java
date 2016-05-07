/*
 * Copyright 2016 Substance Mobile
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mobile.substance.themes.inflation;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;

import mobile.substance.themes.ATE;
import mobile.substance.themes.ATEActivity;
import mobile.substance.themes.tagprocessors.ATEDefaultTags;


/**
 * @author Aidan Follestad (afollestad)
 */
class ATEToolbar extends Toolbar implements PostInflationApplier, ViewInterface {

    public ATEToolbar(Context context) {
        super(context);
        init(context, null);
    }

    public ATEToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, null);
    }

    public ATEToolbar(Context context, @Nullable AttributeSet attrs, @Nullable ATEActivity keyContext) {
        super(context, attrs);
        init(context, keyContext);
    }

    private String mKey;

    private void init(Context context, @Nullable ATEActivity keyContext) {
        ATEDefaultTags.process(this);
        if (keyContext == null && context instanceof ATEActivity)
            keyContext = (ATEActivity) context;
        if (mKey == null && keyContext != null)
            mKey = keyContext.getATEKey();
    }

    @Override
    public void postApply() {
        ATE.themeView(getContext(), this, mKey);
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