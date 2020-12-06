package com.nchungdev.trackme.ui.base.activity

import android.os.Bundle
import com.nchungdev.trackme.R
import com.nchungdev.trackme.databinding.ActivitySimpleBinding
import com.nchungdev.trackme.ui.base.fragment.BaseFragment

abstract class SimpleActivity<F : BaseFragment> : BaseActivity() {
    private var fragment: F? = null

    override fun getLayoutResId() = R.layout.activity_simple

    abstract fun makeFragment(): F

    override fun inflateLayout(savedInstanceState: Bundle?) {
        val binding = ActivitySimpleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initToolbar(binding.toolbar)
        inits(savedInstanceState)
    }

    override fun inits(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            fragment = makeFragment()
            fragment?.let { addFragment(it) }
        } else {
            fragment = supportFragmentManager.findFragmentById(R.id.fragment) as F
        }
    }

    override fun onBackPressed() {
        if (fragment?.onBackPressed() == false) {
            super.onBackPressed()
        }
    }
}
