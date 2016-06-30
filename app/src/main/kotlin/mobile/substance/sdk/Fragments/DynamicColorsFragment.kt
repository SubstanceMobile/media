/*
 * Copyright 2016 Substance Mobile
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mobile.substance.sdk.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.support.design.widget.FloatingActionButton
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.Toolbar
import android.widget.ImageView
import android.widget.TextView
import butterknife.bindView
import butterknife.bindViews
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

    companion object {
        const val REQUEST_CODE_PICK_PHOTO = 1
    }

    override fun onColorsReady(it: ColorPackage) {
        fab.backgroundTintList = ColorStateList.valueOf(it.accentColor)
        fab.setImageDrawable(TintHelper.createTintedDrawable(resources.getDrawable(R.drawable.ic_add_a_photo_black_24dp), it.accentIconActiveColor))
        fab.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start()
        texts[0].setBackgroundColor(it.primaryDarkColor)
        texts[1].setBackgroundColor(it.primaryColor)
        texts[2].setBackgroundColor(it.accentColor)
        texts[0].text = DynamicColorsUtil.hexStringForInt(it.primaryDarkColor)
        texts[1].text = DynamicColorsUtil.hexStringForInt(it.primaryColor)
        texts[2].text = DynamicColorsUtil.hexStringForInt(it.accentColor)
        texts[0].setTextColor(it.textColor)
        texts[1].setTextColor(it.textColor)
        texts[2].setTextColor(it.accentTextColor)
        swipeRefresh!!.isRefreshing = false
    }

    private val fab: FloatingActionButton by bindView<FloatingActionButton>(R.id.fragment_dynamic_colors_fab)
    private val texts: List<TextView> by bindViews<TextView>(R.id.fragment_dynamic_colors_one, R.id.fragment_dynamic_colors_two, R.id.fragment_dynamic_colors_three)
    private val image: ImageView by bindView<ImageView>(R.id.fragment_dynamic_colors_header)
    private val toolbar: Toolbar by bindView<Toolbar>(R.id.fragment_dynamic_colors_toolbar)
    private val swipeRefresh: SwipeRefreshLayout by bindView<SwipeRefreshLayout>(R.id.fragment_dynamic_colors_swiperefresh)

    private var customUri: Uri? = null

    override val layoutResId: Int
        get() = R.layout.fragment_dynamic_colors

    override fun init() {
        NavigationHelper.setupNavigation(drawerLayout!!, toolbar!!)
        toolbar.inflateMenu(R.menu.menu_dynamic_colors)
        toolbar.menu.findItem(R.id.menu_item_configure).isChecked = activity.getSharedPreferences("preferences", Context.MODE_PRIVATE).getBoolean("smartpicking", false)
        toolbar.setOnMenuItemClickListener {
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
        fab.setOnClickListener {
            startActivityForResult(Intent.createChooser(Intent(Intent.ACTION_GET_CONTENT).setType("image/*"), "Pick a photo"), REQUEST_CODE_PICK_PHOTO)
        }
        swipeRefresh.isRefreshing = true
        swipeRefresh.setOnRefreshListener {
            refresh(if(customUri != null) DynamicColors.from(customUri!!, activity) else DynamicColors.from(resources, R.drawable.dynamic_colors))
        }
        Glide.with(this).load(R.drawable.dynamic_colors).into(image)
        refresh(DynamicColors.from(resources, R.drawable.dynamic_colors))
    }

    private fun refresh(dynamicColors: DynamicColors) {

        if(activity.getSharedPreferences("preferences", Context.MODE_PRIVATE).getBoolean("smartpicking", false)) {
            dynamicColors.generate(true, this)
        } else dynamicColors.generateSimple(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE_PICK_PHOTO && resultCode == Activity.RESULT_OK) {
            refresh(DynamicColors.from(data!!.data, activity))
            customUri = data.data
            Glide.with(this).load(data.data).crossFade().into(image)
        }
    }



}