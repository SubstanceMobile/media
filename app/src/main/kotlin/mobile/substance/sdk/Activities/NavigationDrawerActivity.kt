package mobile.substance.sdk.Activities

import android.support.v4.app.Fragment
import android.util.Log
import android.view.MenuItem
import mobile.substance.sdk.Fragments.HomeFragment
import mobile.substance.sdk.Fragments.MusicFragment
import mobile.substance.sdk.R

open class NavigationDrawerActivity : BaseActivity() {
    private var fragment: Fragment? = null

    private fun handleLaunch() {
        fragment = HomeFragment()
        applyFragmentChanges()
    }

    override fun init() {
        handleLaunch()
    }

    private fun applyFragmentChanges() {
        supportFragmentManager.beginTransaction().replace(R.id.activity_main_fragment_placeholder, fragment).commit()
        updateTitle(getFragmentTitle()!!)
    }

    fun handleNavigationClick(item: MenuItem): Boolean {
        Log.d("handleNavigationClick()", "handleNavigationClick()")
        item.isChecked = true
        when (item.itemId) {
            R.id.drawer_home -> {
                fragment = HomeFragment()
                applyFragmentChanges()
                return true
            }
            R.id.drawer_music -> {
                fragment = MusicFragment()
                applyFragmentChanges()
                return true
            }
        }
        return false
    }

    fun getFragment(): Fragment {
        return fragment!!
    }

    open fun updateTitle(title: String) {
    }

    open fun getFragmentTitle(): String? {
        return null
    }

}