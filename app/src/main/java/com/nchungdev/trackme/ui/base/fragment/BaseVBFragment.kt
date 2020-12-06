package com.nchungdev.trackme.ui.base.fragment

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.nchungdev.trackme.ui.tracking.TrackingViewModel
import javax.inject.Inject

abstract class BaseVBFragment<VM : ViewModel, B : ViewBinding> : BaseFragment() {
    // inject view model
    @Inject
    lateinit var viewModel: VM

    private var binding: B? = null

    abstract fun initViewBinding(view: View): B

    override fun inits(view: View, savedInstanceState: Bundle?) {
        binding = initViewBinding(view)
        inits(binding as B, savedInstanceState)
    }

    protected open fun inits(binding: B, savedInstanceState: Bundle?) = Unit

    override fun onDestroyView() {
        if (binding != null) {
            binding = null
        }
        super.onDestroyView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (getMenuResId() != 0) {
            inflater.inflate(getMenuResId(), menu)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }
}