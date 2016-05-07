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
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

import mobile.substance.themes.ATE;
import mobile.substance.themes.ATEActivity;
import mobile.substance.themes.tagprocessors.ATEDefaultTags;


/**
 * @author Aidan Follestad (afollestad)
 */
class ATEEditText extends AppCompatEditText implements ViewInterface, PostInflationApplier {

    public ATEEditText(Context context) {
        super(context);
        init(context, null);
    }

    public ATEEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, null);
    }

    public ATEEditText(Context context, AttributeSet attrs, @Nullable ATEActivity keyContext, boolean waitForInflate) {
        super(context, attrs);
        mWaitForInflate = waitForInflate;
        init(context, keyContext);
    }

    private boolean mWaitForInflate;
    private ATEActivity mKeyContext;

    private void init(Context context, @Nullable ATEActivity keyContext) {
        ATEDefaultTags.process(this);
        if (mWaitForInflate) {
            mKeyContext = keyContext;
            ATE.addPostInflationView(this);
            return;
        }
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

    @Override
    public void postApply() {
        if(!mWaitForInflate) return;
        mWaitForInflate = false;
        ATEViewUtil.init(mKeyContext, this, getContext());
        mKeyContext = null;
    }
}
