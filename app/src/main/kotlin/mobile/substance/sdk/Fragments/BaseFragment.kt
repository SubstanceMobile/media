package mobile.substance.sdk.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by Julian Os on 03.05.2016.
 */
open class BaseFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater!!.inflate(getLayoutResId(), container, false)
        initViews(view)
        init()
        return view
    }

    open fun initViews(root: View) {
    }

    open fun init() {
    }

    open fun getLayoutResId(): Int {
        return 0
    }
}