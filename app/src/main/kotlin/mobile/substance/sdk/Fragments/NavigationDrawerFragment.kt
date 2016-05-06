package mobile.substance.sdk.Fragments

import android.support.v4.widget.DrawerLayout
import mobile.substance.sdk.Activities.NavigationDrawerActivity

/**
 * Created by Julian on 06/05/16.
 */
open class NavigationDrawerFragment : BaseFragment() {

    fun getDrawerLayout(): DrawerLayout {
        return (activity as NavigationDrawerActivity).getDrawer()!!
    }

}