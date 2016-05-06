package mobile.substance.sdk.Activities

import android.support.v4.app.Fragment
import android.support.v4.widget.DrawerLayout
import android.util.Log
import android.view.MenuItem
import mobile.substance.sdk.Fragments.HomeFragment
import mobile.substance.sdk.Fragments.MusicFragment
import mobile.substance.sdk.R

open class NavigationDrawerActivity : BaseActivity() {
    private var fragment: Fragment? = null

    private fun handleLaunch() {
        fragment = HomeFragment()
        supportFragmentManager.beginTransaction().add(R.id.activity_main_fragment_placeholder, fragment, "SubstanceSDk").commit()
    }

    override fun init() {
        handleLaunch()
    }

    private fun commitFragment() {
        Log.d("LOG", "fragment is HomeFragment => " + (fragment is HomeFragment).toString())
        supportFragmentManager.beginTransaction().replace(R.id.activity_main_fragment_placeholder, fragment, "SubstanceSDK").commit()
        Log.d("LOG", supportFragmentManager.fragments.size.toString())
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