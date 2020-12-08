package com.nchungdev.trackme.util

import android.content.Context
import android.content.Intent
import com.nchungdev.domain.model.SessionModel
import com.nchungdev.trackme.ui.detail.DetailActivity
import com.nchungdev.trackme.ui.detail.DetailFragment.Companion.EXTRA_SESSION
import com.nchungdev.trackme.ui.main.MainActivity
import com.nchungdev.trackme.ui.tracking.TrackingActivity
import com.nchungdev.trackme.ui.tracking.TrackingFragment

object Navigator {

    fun openMainActivity(context: Context) {
        context.startActivity(Intent(context, MainActivity::class.java))
    }

    fun openTrackingActivity(context: Context, fromSplash: Boolean = false) {
        context.startActivity(Intent(context, TrackingActivity::class.java).apply {
            putExtra(TrackingFragment.EXTRA_OPEN_FROM_SPLASH, fromSplash)
        })
    }

    fun openSessionDetails(context: Context, sessionModel: SessionModel) {
        context.startActivity(Intent(context, DetailActivity::class.java).apply {
            putExtra(EXTRA_SESSION, sessionModel)
        })
    }
}