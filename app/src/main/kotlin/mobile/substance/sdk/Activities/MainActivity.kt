package mobile.substance.sdk.Activities

import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.Toolbar
import mobile.substance.sdk.Fragments.HomeFragment
import mobile.substance.sdk.Fragments.MusicFragment
import mobile.substance.sdk.R

class MainActivity : NavigationDrawerActivity() {
    var drawerLayout: DrawerLayout? = null
    var navigationView: NavigationView? = null
    var toolbar: Toolbar? = null

    override fun getLayoutResId(): Int {
        return R.layout.activity_main
    }

    override fun init() {
        toolbar!!.setNavigationIcon(R.drawable.ic_menu_white_24dp)
        toolbar!!.setNavigationOnClickListener { drawerLayout!!.openDrawer(GravityCompat.START) }
        navigationView!!.setNavigationItemSelectedListener { it ->
            drawerLayout!!.closeDrawer(GravityCompat.START)
            handleNavigationClick(it)
        }
        super.init()
    }


    override fun initViews() {
        toolbar = findViewById(R.id.activity_main_toolbar) as Toolbar
        navigationView = findViewById(R.id.activity_main_navigationview) as NavigationView
        drawerLayout = findViewById(R.id.activity_main_drawerlayout) as DrawerLayout
    }

    override fun getFragmentTitle(): String? {
        when (getFragment()) {
            is HomeFragment -> {
                return "Home"
            }
            is MusicFragment -> {
                return "Music"
            }
        }
        return null
    }

    override fun updateTitle(title: String) {
        toolbar!!.title = title
    }

}