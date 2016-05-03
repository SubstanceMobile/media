package mobile.substance.sdk.Fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import mobile.substance.sdk.R

/**
 * Created by Julian Os on 03.05.2016.
 */
class HomeFragment : BaseFragment() {

    override fun init() {

    }

    override fun initViews(root: View) {

    }

    override fun getRootView(inflater: LayoutInflater?, container: ViewGroup?): View? {
        return inflater!!.inflate(R.layout.fragment_home, container, false)
    }
}