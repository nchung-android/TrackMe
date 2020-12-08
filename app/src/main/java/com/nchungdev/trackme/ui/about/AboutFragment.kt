package com.nchungdev.trackme.ui.about

import android.view.LayoutInflater
import com.nchungdev.trackme.databinding.FragmentAboutBinding
import com.nchungdev.trackme.ui.base.fragment.BaseFragment

class AboutFragment : BaseFragment<FragmentAboutBinding>() {

    override fun injectDagger() = Unit

    override fun initViewBinding(inflater: LayoutInflater) = FragmentAboutBinding.inflate(inflater)
}