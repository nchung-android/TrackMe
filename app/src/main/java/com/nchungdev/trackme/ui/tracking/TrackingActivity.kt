package com.nchungdev.trackme.ui.tracking

import androidx.core.os.bundleOf
import com.nchungdev.trackme.R
import com.nchungdev.trackme.databinding.FragmentTrackingBinding
import com.nchungdev.trackme.ui.base.activity.SimpleActivity

class TrackingActivity : SimpleActivity<FragmentTrackingBinding, TrackingFragment>() {

    override fun makeFragment() = TrackingFragment().apply {
        val isFromNotif = intent?.getBooleanExtra(TrackingFragment.EXTRA_OPEN_FROM_NOTIF, false)
        val isFromSplash = intent?.getBooleanExtra(TrackingFragment.EXTRA_OPEN_FROM_SPLASH, false)
        arguments = bundleOf(
            TrackingFragment.EXTRA_OPEN_FROM_NOTIF to isFromNotif,
            TrackingFragment.EXTRA_OPEN_FROM_SPLASH to isFromSplash,
        )
    }

    override fun getTitleResId() = R.string.title_tracking
}
