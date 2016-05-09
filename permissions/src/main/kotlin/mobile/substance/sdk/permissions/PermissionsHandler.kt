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

package mobile.substance.sdk.permissions

import android.app.Activity
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import java.util.*

class PermissionsHandler(activity: Activity, permissions: Array<String>?, callbacks: PermissionsCallbacks) {
    private var activity: Activity = activity
    private var permissions: Array<String>? = permissions
    private var callbacks: PermissionsCallbacks = callbacks
    private val requestCode = Random().nextInt(100)
    private var granted: Array<Boolean>? = null
    private var showedRationale: Array<Boolean>? = null

    var allGranted: Boolean = false

    init {
        this.granted = Array(permissions!!.size, { false })
        this.showedRationale = Array(permissions.size, { false })
    }

    fun handlePermissions(): Boolean {
        if (permissions == null) {
            allGranted = true
            callbacks.onAllGranted()
            return true
        }

        val permissionsToCheck = ArrayList<String>()

        for (i in permissions!!.indices) {
            if (ContextCompat.checkSelfPermission(activity, permissions!![i]) != PackageManager.PERMISSION_GRANTED) {
                permissionsToCheck.add(permissions!![i])
            } else if (granted!![i] != true) {
                granted!![i] = true
                callbacks.onPermissionGranted(permissions!![i])
                permissionsToCheck.add(permissions!![i])
            }
        }

        if (granted!!.all { true }) {
            allGranted = true
            callbacks.onAllGranted()
            return true
        }

        ActivityCompat.requestPermissions(activity, permissionsToCheck.toTypedArray(), requestCode)
        return false
    }

    fun handleRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray): Boolean {
        if (this.requestCode != requestCode) return false

        for (i in permissions.indices) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                if (granted!![i] == true)
                    continue
                granted!![i] = true
                callbacks!!.onPermissionGranted(permissions[i])
            } else if (showedRationale!![i] != true) {
                showedRationale!![i] = true
                callbacks!!.onShouldShowRationale(permissions[i])
            } else callbacks!!.onPermissionUnavailable(permissions[i])
        }

        if (granted!!.all { true }) {
            callbacks!!.onAllGranted()
            allGranted = true
        }


        return true
    }
}