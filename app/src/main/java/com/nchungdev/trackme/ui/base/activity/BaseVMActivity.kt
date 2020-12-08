package com.nchungdev.trackme.ui.base.activity

import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import javax.inject.Inject

abstract class BaseVMActivity<VM : ViewModel, B : ViewBinding> : BaseActivity<B>() {
    @Inject
    lateinit var viewModel: VM
}