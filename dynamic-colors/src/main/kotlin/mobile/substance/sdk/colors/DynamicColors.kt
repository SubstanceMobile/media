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

package mobile.substance.sdk.colors

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.support.v4.util.Pair
import android.support.v7.graphics.Palette
import android.util.Log
import java.io.File
import java.util.*
import java.util.concurrent.Executor

class DynamicColors private constructor(private val from: Any) {

    ///////////////////////////////////////////////////////////////////////////
    // Methods for running.
    ///////////////////////////////////////////////////////////////////////////

    private fun exec(exec: Executor, callback: DynamicColorsCallback, smartPicking: Boolean, smartText: Boolean = false) {
        DynamicColorsGenerator(callback, smartPicking, smartText).executeOnExecutor(exec, from)
    }

    @JvmOverloads fun generate(useSmartTextPicking: Boolean = false, callback: DynamicColorsCallback = DynamicColorsOptions.defaultCallback, executor: Executor = AsyncTask.THREAD_POOL_EXECUTOR) {
        exec(executor, callback, true, useSmartTextPicking)
    }

    @JvmOverloads fun generateSimple(callback: DynamicColorsCallback = DynamicColorsOptions.defaultCallback, executor: Executor = AsyncTask.THREAD_POOL_EXECUTOR) {
        exec(executor, callback, false)
    }

    ///////////////////////////////////////////////////////////////////////////
    // The actual task
    ///////////////////////////////////////////////////////////////////////////

    private class DynamicColorsGenerator internal constructor(private val callback: DynamicColorsCallback, smartPicking: Boolean, smartText: Boolean) : AsyncTask<Any, Void, ColorPackage>() {
        private val smartPicking = smartPicking
        private val smartText = smartText

        override fun doInBackground(vararg params: Any): ColorPackage? {
            var bitmap: Bitmap? = null
            if (params[0] is Bitmap) bitmap = params[0] as Bitmap
            if (params[0] is String) bitmap = BitmapFactory.decodeFile(params[0] as String)
            if (params[0] is Pair<*, *>) {
                val pair = params[0] as Pair<*, *>
                if(pair.first is Uri) {
                    val uri = pair.first as Uri
                    val context = pair.second as Context
                    if(uri.scheme == "file") {
                        bitmap = BitmapFactory.decodeFile(uri.path)
                    } else {
                        bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri))
                    }
                } else if(pair.first is Resources) {
                    val resources = pair.first as Resources
                    val resId = pair.second as Int
                    bitmap = BitmapFactory.decodeResource(resources, resId)
                }

            }

            if (bitmap == null) return null


            val palette = Palette.from(bitmap).generate()

            if (smartPicking) {
                Log.d(DynamicColors.javaClass.simpleName, "DynamicColors is using smart picking")
                val sortedSwatches = ArrayList(palette.swatches)
                Collections.sort(sortedSwatches) { a, b -> a.population.toInt().compareTo(b.population) }

                try {
                    val swatches = arrayOf(sortedSwatches[sortedSwatches.size - 1], sortedSwatches[0])
                    val primary = swatches[0].rgb
                    val accent = swatches[1].rgb

                    if (smartText) {
                        return ColorPackage(primary, accent)
                    } else {
                        val title = swatches[0].titleTextColor
                        val accentTitle = swatches[1].titleTextColor
                        val accentSubtitle = swatches[1].bodyTextColor
                        return ColorPackage(primary, DynamicColorsUtil.generatePrimaryDark(primary),
                                title, swatches[0].bodyTextColor, DynamicColorsUtil.makeDisabledColor(title, DynamicColorsUtil.isColorLight(title)),
                                accent, accentTitle, accentSubtitle, DynamicColorsUtil.makeDisabledColor(accentTitle, DynamicColorsUtil.isColorLight(accentTitle)),
                                if (DynamicColorsUtil.isColorLight(primary)) ColorConstants.ICON_COLOR_ACTIVE_LIGHT_BG else ColorConstants.ICON_COLOR_ACTIVE_DARK_BG,
                                if (DynamicColorsUtil.isColorLight(primary)) ColorConstants.ICON_COLOR_INACTIVE_LIGHT_BG else ColorConstants.ICON_COLOR_INACTIVE_DARK_BG,
                                if (DynamicColorsUtil.isColorLight(accent)) ColorConstants.ICON_COLOR_ACTIVE_LIGHT_BG else ColorConstants.ICON_COLOR_ACTIVE_DARK_BG,
                                if (DynamicColorsUtil.isColorLight(accent)) ColorConstants.ICON_COLOR_INACTIVE_LIGHT_BG else ColorConstants.ICON_COLOR_INACTIVE_DARK_BG)
                    }
                } catch (e: Exception) {
                    return DynamicColorsOptions.defaultColors
                }

            } else {
                try {
                    return ColorPackage(palette.getDarkVibrantColor(DynamicColorsOptions.defaultColors.primaryColor), palette.getVibrantColor(DynamicColorsOptions.defaultColors.accentColor))
                } catch (e: Exception) {
                    return DynamicColorsOptions.defaultColors
                }

            }
        }

        override fun onPostExecute(colorPackage: ColorPackage) {
            callback.onColorsReady(colorPackage)
        }

    }

    companion object {

        fun from(image: Bitmap): DynamicColors {
            return DynamicColors(image)
        }

        fun from(uri: Uri, context: Context): DynamicColors {
            return DynamicColors(Pair(uri, context))
        }

        fun from(path: String): DynamicColors {
            return DynamicColors(path)
        }

        fun from(image: File): DynamicColors {
            return DynamicColors(image.path)
        }

        fun from(res: Resources, resId: Int): DynamicColors {
            return DynamicColors(Pair(res, Integer.valueOf(resId)))
        }
    }
}
