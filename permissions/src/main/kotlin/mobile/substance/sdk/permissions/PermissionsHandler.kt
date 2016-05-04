package mobile.substance.sdk.permissions

import android.app.Activity
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import java.util.*

class PermissionsHandler(activity: Activity, permissions: Array<String>?, callbacks: PermissionsCallbacks) {
    var activity: Activity? = null
    var permissions: Array<String>? = null
    var callbacks: PermissionsCallbacks? = null
    val requestCode = Random().nextInt(100)
    var granted: Array<Boolean>? = null
    var showedRationale: Array<Boolean>? = null

    init {
        this.activity = activity
        this.permissions = permissions
        this.callbacks = callbacks;
        this.granted = Array(permissions!!.size, { false })
        this.showedRationale = Array(permissions.size, { false })
    }

    fun handlePermissions(): Boolean {
        if (permissions == null) {
            callbacks!!.onAllGranted()
            return true
        }

        val permissionsToCheck = ArrayList<String>()

        for (i in permissions!!.indices) {
            if (ContextCompat.checkSelfPermission(activity!!, permissions!![i]) != PackageManager.PERMISSION_GRANTED) {
                permissionsToCheck.add(permissions!![i])
            } else if (granted!![i] != true) {
                granted!![i] = true
                callbacks!!.onPermissionGranted(permissions!![i])
                permissionsToCheck.add(permissions!![i])
            }
        }

        ActivityCompat.requestPermissions(activity!!, permissionsToCheck.toTypedArray(), requestCode)
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

        if (granted!!.all { true })
            callbacks!!.onAllGranted()

        return true
    }
}