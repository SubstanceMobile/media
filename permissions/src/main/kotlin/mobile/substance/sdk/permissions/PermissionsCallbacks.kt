package mobile.substance.sdk.permissions

interface PermissionsCallbacks {

    fun onPermissionGranted(permission: String)

    fun onShouldShowRationale(permission: String)

    fun onAllGranted()

    fun onPermissionUnavailable(permission: String)

}