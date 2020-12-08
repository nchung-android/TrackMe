package com.nchungdev.trackme.ui.base.activity

import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.nchungdev.trackme.R
import com.nchungdev.trackme.databinding.ActivitySimpleBinding
import com.nchungdev.trackme.ui.base.fragment.BaseFragment

abstract class SimpleActivity<B : ViewBinding, F : BaseFragment<B>> : BaseActivity<ActivitySimpleBinding>() {
    private var fragment: F? = null

    abstract fun makeFragment(): F

    override fun initViewBinding() = ActivitySimpleBinding.inflate(layoutInflater)

    override fun onBind(binding: ActivitySimpleBinding, savedInstanceState: Bundle?) {
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
