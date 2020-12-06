package com.nchungdev.trackme.ui.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.nchungdev.data.util.Util
import com.nchungdev.trackme.ui.base.activity.BaseActivity
import com.nchungdev.trackme.ui.base.PermissionRequestable

object PermissionUtils {

    private val permissionsRequested by lazy {
        if (Util.hasQ()) {
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            )
        } else {
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    fun isLocationPermissionGranted(activity: Context) =
        checkPermissions(activity, permissionsRequested).isEmpty()

    fun requestLocationPermissions(activity: BaseActivity, requestable: PermissionRequestable.Callback) {
        activity.requestPermissions(permissionsRequested, requestable)
    }

    fun checkPermissions(activity: Context, permissions: Array<String>): Array<String> {
        val notGrantedPermissions = mutableListOf<String>()
        if (Util.hasM()) {
            val ret = IntArray(permissions.size)
            var notGranted = 0
            for (i in ret.indices) {
                ret[i] = ContextCompat.checkSelfPermission(activity, permissions[i])
                if (ret[i] != PackageManager.PERMISSION_GRANTED) notGranted++
            }
            if (notGranted > 0) {
                notGranted = 0
                for (i in ret.indices) if (ret[i] != PackageManager.PERMISSION_GRANTED) {
                    notGrantedPermissions.add(permissions[i])
                    if (notGranted == ret.size) break
                }
            }
        }
        return notGrantedPermissions.toTypedArray()
    }
}
