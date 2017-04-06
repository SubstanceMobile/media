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

package mobile.substance.media.sample.fragments

import android.os.Bundle
import android.support.v7.widget.Toolbar
import butterknife.bindView
import mobile.substance.media.sample.R
import mobile.substance.media.sample.helpers.NavigationHelper

class HomeFragment : NavigationDrawerFragment() {
    private val toolbar: Toolbar by bindView<Toolbar>(R.id.fragment_home_toolbar)

    override fun init(savedInstanceState: Bundle?) {
        NavigationHelper.setupNavigation(drawerLayout!!, toolbar)
    }

    override val layoutResId: Int = R.layout.fragment_home
}