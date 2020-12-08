package com.nchungdev.trackme.ui.util

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment

fun FragmentManager.findNavHostFragment(@IdRes resId: Int): NavController {
    val navHostFragment = findFragmentById(resId) as NavHostFragment?
    return navHostFragment?.navController ?: throw NullPointerException()
}

fun Context.openDetailsSetting() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri: Uri = Uri.fromParts("package", packageName, null)
    intent.data = uri
    startActivity(intent)
}
