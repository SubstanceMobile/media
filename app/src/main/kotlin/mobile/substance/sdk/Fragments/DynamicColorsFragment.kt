package mobile.substance.sdk.fragments

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.support.design.widget.FloatingActionButton
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import mobile.substance.sdk.R
import mobile.substance.sdk.colors.ColorPackage
import mobile.substance.sdk.colors.DynamicColors
import mobile.substance.sdk.colors.DynamicColorsCallback
import mobile.substance.sdk.colors.DynamicColorsUtil
import mobile.substance.sdk.helpers.NavigationHelper
import mobile.substance.sdk.theming.util.TintHelper

/**
 * Created by Julian Os on 10.05.2016.
 */
class DynamicColorsFragment : NavigationDrawerFragment(), DynamicColorsCallback {

    override fun onColorsReady(it: ColorPackage?) {
        it!!
        fab!!.backgroundTintList = ColorStateList.valueOf(it.accentColor)
        fab!!.setImageDrawable(TintHelper.createTintedDrawable(resources.getDrawable(R.drawable.ic_color_lens_black_24dp), it.accentIconActiveColor))
        text1!!.setBackgroundColor(it.primaryDarkColor)
        text2!!.setBackgroundColor(it.primaryColor)
        text3!!.setBackgroundColor(it.accentColor)
        text1!!.text = DynamicColorsUtil.hexStringForInt(it.primaryDarkColor)
        text2!!.text = DynamicColorsUtil.hexStringForInt(it.primaryColor)
        text3!!.text = DynamicColorsUtil.hexStringForInt(it.accentColor)
        Toast.makeText(activity, DynamicColorsUtil.hexStringForInt(it.textColor), Toast.LENGTH_LONG).show()
        text1!!.setTextColor(Color.parseColor(DynamicColorsUtil.hexStringForInt(it.textColor)))
        text2!!.setTextColor(it.textColor)
        text3!!.setTextColor(it.accentTextColor)
        swipeRefresh!!.isRefreshing = false
    }

    private var fab: FloatingActionButton? = null
    private var text1: TextView? = null
    private var text2: TextView? = null
    private var text3: TextView? = null
    private var image: ImageView? = null
    private var toolbar: Toolbar? = null
    private var swipeRefresh: SwipeRefreshLayout? = null


    override fun getLayoutResId(): Int {
        return R.layout.fragment_dynamic_colors
    }

    override fun init() {
        NavigationHelper.setupNavigation(getDrawerLayout(), toolbar!!)
        toolbar!!.inflateMenu(R.menu.menu_dynamic_colors)
        toolbar!!.setOnMenuItemClickListener {
            if(it.itemId == R.id.menu_item_configure) {
                it.isChecked = !it.isChecked
                activity.getSharedPreferences("preferences", Context.MODE_PRIVATE)
                        .edit()
                        .putBoolean("smartpicking", it.isChecked)
                        .commit()
                true
            } else false
        }
        Glide.with(activity).load(R.drawable.dynamic_colors).crossFade().centerCrop().into(image)

        swipeRefresh!!.isRefreshing = true
        swipeRefresh!!.setOnRefreshListener {
            refresh()
        }
        refresh()
    }

    private fun refresh() {
        val dynamicColors = DynamicColors.from(resources, R.drawable.dynamic_colors)

        if(activity.getSharedPreferences("preferences", Context.MODE_PRIVATE).getBoolean("smartpicking", false)) {
            dynamicColors.generate(this, true)
        } else dynamicColors.generateSimple(this)
    }

    override fun initViews(root: View) {
        fab = root.findViewById(R.id.fragment_dynamic_colors_fab) as FloatingActionButton
        text1 = root.findViewById(R.id.fragment_dynamic_colors_one) as TextView
        text2 = root.findViewById(R.id.fragment_dynamic_colors_two) as TextView
        text3 = root.findViewById(R.id.fragment_dynamic_colors_three) as TextView
        image = root.findViewById(R.id.fragment_dynamic_colors_header) as ImageView
        toolbar = root.findViewById(R.id.fragment_dynamic_colors_toolbar) as Toolbar
        swipeRefresh = root.findViewById(R.id.fragment_dynamic_colors_swiperefresh) as SwipeRefreshLayout
    }

}