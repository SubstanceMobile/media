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

package mobile.substance.sdk.Fragments

import android.support.v7.widget.Toolbar
import android.view.View
import mobile.substance.sdk.Helpers.NavigationHelper
import mobile.substance.sdk.R

/**
 * Created by Julian Os on 03.05.2016.
 */
class HomeFragment : NavigationDrawerFragment() {
    private var toolbar: Toolbar? = null

    override fun init() {
        NavigationHelper.setupNavigation(getDrawerLayout(), toolbar!!)
    }

    override fun initViews(root: View) {
        toolbar = root.findViewById(R.id.fragment_home_toolbar) as Toolbar
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_home
    }
}