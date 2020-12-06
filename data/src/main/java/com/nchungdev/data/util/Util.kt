package com.nchungdev.data.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build

object Util {

    fun hasM() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

    fun hasO() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

    fun hasQ() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    @SuppressLint("QueryPermissionsNeeded")
    fun checkIntent(context: Context, intent: Intent?): Boolean {
        return if (intent == null) false else intent.resolveActivity(context.packageManager) != null
    }
}