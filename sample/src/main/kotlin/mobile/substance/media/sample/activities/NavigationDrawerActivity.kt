/*
 * Copyright 2017 Substance Mobile
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

package mobile.substance.media.sample.activities

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.DrawerLayout
import android.view.MenuItem
import mobile.substance.media.sample.R
import mobile.substance.media.sample.fragments.HomeFragment
import mobile.substance.media.sample.fragments.MusicFragment

abstract class NavigationDrawerActivity : BaseActivity() {

    override abstract val layoutResId: Int

    private var fragment: Fragment? = null

    private fun handleLaunch() {
        fragment = HomeFragment()
        commitFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun init(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) handleLaunch() else fragment = supportFragmentManager.getFragment(savedInstanceState, "FRAGMENT")
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        if (fragment != null) {
            super.onSaveInstanceState(outState)
            supportFragmentManager.putFragment(outState, "FRAGMENT", fragment)
        }
    }

    private fun commitFragment() {
         supportFragmentManager
                 .beginTransaction()
                 .replace(R.id.activity_main_fragment_placeholder, fragment)
                 .commit()
    }

    fun handleNavigationClick(item: MenuItem): Boolean {
        item.isChecked = true
        when (item.itemId) {
            R.id.drawer_home -> {
                if (fragment is HomeFragment) return false
                fragment = HomeFragment()
                commitFragment()
                return true
            }
            R.id.drawer_music -> {
                if (fragment is MusicFragment) return false
                fragment = MusicFragment()
                commitFragment()
                return true
            }
        }
        return false
    }

    abstract val drawer: DrawerLayout?

}