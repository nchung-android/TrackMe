package com.nchungdev.trackme.ui.util

import android.content.Context
import android.content.Intent
import com.nchungdev.domain.model.SessionModel
import com.nchungdev.trackme.ui.detail.DetailActivity
import com.nchungdev.trackme.ui.detail.DetailFragment.Companion.EXTRA_SESSION
import com.nchungdev.trackme.ui.tracking.TrackingActivity

object Navigator {

    fun openTrackingActivity(context: Context, isResume: Boolean) {
        context.startActivity(Intent(context, TrackingActivity::class.java).apply {
            putExtra("isResume", isResume)
        })
    }

    fun openSessionDetails(context: Context, sessionModel: SessionModel) {
        context.startActivity(Intent(context, DetailActivity::class.java).apply {
            putExtra(EXTRA_SESSION, sessionModel)
        })
    }
}