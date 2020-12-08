package com.nchungdev.trackme.ui.tracking

import com.nchungdev.domain.model.SessionModel
import com.nchungdev.trackme.ui.base.activity.SimpleActivity

class TrackingActivity : SimpleActivity<TrackingFragment>() {
    override fun makeFragment() = TrackingFragment()
}
