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

package mobile.substance.sdk.activities

import android.support.v4.app.Fragment
import android.support.v4.widget.DrawerLayout
import android.util.Log
import android.view.MenuItem
import mobile.substance.sdk.R
import mobile.substance.sdk.fragments.DynamicColorsFragment
import mobile.substance.sdk.fragments.HomeFragment
import mobile.substance.sdk.fragments.MusicFragment

open class NavigationDrawerActivity : BaseActivity() {
    private var fragment: Fragment? = null

    private fun handleLaunch() {
        fragment = HomeFragment()
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.activity_main_fragment_placeholder, fragment, "SubstanceSDK")
                .addToBackStack("SubstanceSDK")
                .commitAllowingStateLoss()
    }

    override fun init() {
        handleLaunch()
    }

    private fun commitFragment() {
         supportFragmentManager
                 .beginTransaction()
                 .replace(R.id.activity_main_fragment_placeholder, fragment, "SubstanceSDK")
                 .addToBackStack("SubstanceSDK")
                 .commitAllowingStateLoss()
    }

    fun handleNavigationClick(item: MenuItem): Boolean {
        Log.d("handleNavigationClick()", "handleNavigationClick()")
        item.isChecked = true
        when (item.itemId) {
            R.id.drawer_home -> {
                fragment = HomeFragment()
                commitFragment()
                return true
            }
            R.id.drawer_music -> {
                fragment = MusicFragment()
                commitFragment()
                return true
            }
            R.id.drawer_dynamic_colors -> {
                fragment = DynamicColorsFragment()
                commitFragment()
                return true
            }
        }
        return false
    }

    fun getFragment(): Fragment {
        return fragment!!
    }

    open fun getDrawer(): DrawerLayout? {
        return null
    }

}