package com.nchungdev.trackme.ui.detail

import com.nchungdev.domain.model.SessionModel
import com.nchungdev.trackme.R
import com.nchungdev.trackme.databinding.FragmentDetailBinding
import com.nchungdev.trackme.ui.base.activity.SimpleActivity

class DetailActivity : SimpleActivity<FragmentDetailBinding, DetailFragment>() {

    override fun makeFragment(): DetailFragment {
        val sessionModel = intent.getParcelableExtra<SessionModel>(DetailFragment.EXTRA_SESSION)
        return DetailFragment.newInstance(sessionModel)
    }

    override fun getTitleResId() = R.string.title_session_detail
}