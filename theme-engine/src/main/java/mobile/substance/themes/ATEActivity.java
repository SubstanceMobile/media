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

package mobile.substance.themes;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

/**
 * @author Aidan Follestad (afollestad)
 */
public class ATEActivity extends AppCompatActivity {

    private long updateTime = -1;

    @Nullable
    public String getATEKey() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ATE.preApply(this, getATEKey());
        super.onCreate(savedInstanceState);
        updateTime = System.currentTimeMillis();
    }

    @Override
    protected void onStart() {
        super.onStart();
        ATE.postApply(this, getATEKey());
    }

    @Override
    protected void onResume() {
        super.onResume();
        ATE.invalidateActivity(this, updateTime, getATEKey());
        updateVars();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing())
            ATE.cleanup();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ATE.themeOverflow(this, getATEKey());
        return super.onCreateOptionsMenu(menu);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Colors
    ///////////////////////////////////////////////////////////////////////////
    int primary, accent;

    void updateVars() {
        primary = Config.primaryColor(this, getATEKey());
        accent = Config.accentColor(this, getATEKey());
    }

    int getPrimaryColor() {
        return primary;
    }

    int getAccentColor() {
        return accent;
    }


}