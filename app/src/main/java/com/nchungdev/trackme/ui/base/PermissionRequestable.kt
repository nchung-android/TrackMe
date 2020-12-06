package com.nchungdev.trackme.ui.base

interface PermissionRequestable {
    fun checkPermissions(permissions: Array<String>): Array<String>

    fun requestPermissions(permissions: Array<String>, callback: Callback)

    fun requestPermissions(permission: String, callback: Callback)

    interface Callback {
        fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray,
            showDialog: Boolean
        )
    }
}
