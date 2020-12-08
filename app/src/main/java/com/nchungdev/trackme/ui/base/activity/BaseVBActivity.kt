package com.nchungdev.trackme.ui.base.activity

import android.os.Bundle
import android.view.Menu
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.nchungdev.trackme.ui.base.PermissionRequestable
import com.nchungdev.trackme.ui.main.MainViewModel
import javax.inject.Inject

abstract class BaseVBActivity<VM : ViewModel, B : ViewBinding> : BaseActivity() {
    @Inject
    lateinit var viewModel: VM

    private var binding: B? = null

    private var permissionsResultRequestable: PermissionRequestable.Callback? = null

    protected open fun initViewBinding(): B? {
        return null
    }

    private fun viewBinding(): B {
        if (binding == null) {
            throw Exception("Not init layout")
        }
        return binding as B
    }

    override fun inflateLayout(savedInstanceState: Bundle?) {
        binding = initViewBinding()
        setContentView(binding?.root)
        inits(viewBinding(), savedInstanceState)
    }

    protected open fun inits(binding: B, savedInstanceState: Bundle?) = Unit

    override fun onDestroy() {
        if (binding != null) {
            binding = null
        }
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (getMenuResId() != 0) {
            menuInflater.inflate(getMenuResId(), menu)
        }
        return super.onCreateOptionsMenu(menu);
    }
}