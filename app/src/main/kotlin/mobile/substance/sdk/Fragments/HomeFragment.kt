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