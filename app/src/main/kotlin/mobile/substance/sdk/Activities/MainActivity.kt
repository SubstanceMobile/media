package mobile.substance.sdk.Activities

import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import mobile.substance.sdk.R
import mobile.substance.sdk.music.loading.Library
import mobile.substance.sdk.music.loading.LibraryConfig
import mobile.substance.sdk.music.loading.LibraryData

class MainActivity : NavigationDrawerActivity() {
    var drawerLayout: DrawerLayout? = null
    var navigationView: NavigationView? = null

    override fun getLayoutResId(): Int {
        return R.layout.activity_main
    }

    override fun init() {
        navigationView!!.setNavigationItemSelectedListener { it ->
            drawerLayout!!.closeDrawer(GravityCompat.START)
            handleNavigationClick(it)
        }
        Library(this, LibraryConfig()
                .put(LibraryData.SONGS)
                .put(LibraryData.ALBUMS)
                .put(LibraryData.ARTISTS)
                .put(LibraryData.PLAYLISTS)
                .put(LibraryData.GENRES))
        Library.build()
        super.init()
    }


    override fun initViews() {
        navigationView = findViewById(R.id.activity_main_navigationview) as NavigationView
        drawerLayout = findViewById(R.id.activity_main_drawerlayout) as DrawerLayout
    }

    override fun getDrawer(): DrawerLayout? {
        return drawerLayout
    }

}