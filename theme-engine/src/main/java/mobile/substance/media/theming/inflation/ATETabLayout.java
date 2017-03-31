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

package mobile.substance.sdk.theming.inflation;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;

import mobile.substance.sdk.theming.ATEActivity;
import mobile.substance.sdk.theming.tagprocessors.ATEDefaultTags;

/**
 * @author Aidan Follestad (afollestad)
 */
class ATETabLayout extends TabLayout implements ViewInterface {

    public ATETabLayout(Context context) {
        super(context);
        init(context, null);
    }

    public ATETabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, null);
    }

    public ATETabLayout(Context context, AttributeSet attrs, @Nullable ATEActivity keyContext) {
        super(context, attrs);
        init(context, keyContext);
    }

    private void init(Context context, @Nullable ATEActivity keyContext) {
        ATEDefaultTags.process(this);
        try {
            ATEViewUtil.init(keyContext, this, context);
        } catch (Throwable t) {
            throw new RuntimeException(t.getMessage(), t);
        }
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