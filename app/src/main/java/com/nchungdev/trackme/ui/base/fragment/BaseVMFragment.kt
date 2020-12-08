package com.nchungdev.trackme.ui.base.fragment

import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import javax.inject.Inject

abstract class BaseVMFragment<VM : ViewModel, B : ViewBinding> : BaseFragment<B>() {
    // inject view model
    @Inject
    lateinit var viewModel: VM
}