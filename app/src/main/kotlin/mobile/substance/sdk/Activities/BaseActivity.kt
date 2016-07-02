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

package mobile.substance.sdk.activities

import android.Manifest
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import mobile.substance.sdk.permissions.PermissionsCallbacks
import mobile.substance.sdk.permissions.PermissionsHandler

abstract class BaseActivity : AppCompatActivity(), PermissionsCallbacks {
    val permissionHandler = PermissionsHandler(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), this)

    var savedInstanceState: Bundle? = null

    override fun onPermissionGranted(permission: String) {
        Log.d("Permission Granted!", permission)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        if (permissionHandler.allGranted)
            super.onSaveInstanceState(outState)
    }

    override fun onShouldShowRationale(permission: String) {}

    override fun onAllGranted() {
        Log.d(BaseActivity::class.java.simpleName, "onAllGranted()")
        runOnUiThread { init(savedInstanceState) }
    }

    override fun onPermissionUnavailable(permission: String) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.savedInstanceState = savedInstanceState
        setContentView(layoutResId)
        Thread() {
            run { permissionHandler.handlePermissions() }
        }.start()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (!permissionHandler.handleRequestPermissionsResult(requestCode, permissions, grantResults))
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    abstract fun init(savedInstanceState: Bundle?)

    abstract val layoutResId: Int

}