package com.nchungdev.trackme.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import com.nchungdev.trackme.MainApp
import com.nchungdev.trackme.databinding.ActivitySplashBinding
import com.nchungdev.trackme.event.Event
import com.nchungdev.trackme.ui.base.activity.BaseVMActivity
import com.nchungdev.trackme.util.Navigator

class SplashActivity : BaseVMActivity<SplashViewModel, ActivitySplashBinding>() {

    override fun injectDagger() {
        MainApp.getAppComponent().splashComponent().create().inject(this)
    }

    override fun initViewBinding() = ActivitySplashBinding.inflate(layoutInflater)

    override fun onBind(binding: ActivitySplashBinding, savedInstanceState: Bundle?) {
        binding.loading.progress.isVisible = true
        subscribeToObservers()
        viewModel.onInit()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        viewModel.onInit()
    }

    private fun subscribeToObservers() {
        viewModel.navigation.observe(this) {
            when (it) {
                Event.HOME -> {
                    Navigator.openMainActivity(this)
                    overridePendingTransition(0, 0)
                    finish()
                }
                Event.TRACKING -> {
                    Navigator.openTrackingActivity(this, true)
                    overridePendingTransition(0, 0)
                    finish()
                }
                else -> Unit
            }
        }
    }
}
