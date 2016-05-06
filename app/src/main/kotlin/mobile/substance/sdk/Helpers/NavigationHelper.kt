package mobile.substance.sdk.Helpers

import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.Toolbar
import mobile.substance.sdk.R

/**
 * Created by Julian on 06/05/16.
 */
object NavigationHelper {

    fun setupNavigation(drawerLayout: DrawerLayout, toolbar: Toolbar) {
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp)
        toolbar.setNavigationOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }
    }

}