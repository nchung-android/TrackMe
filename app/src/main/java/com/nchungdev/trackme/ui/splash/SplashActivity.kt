package com.nchungdev.trackme.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import com.nchungdev.domain.model.SessionModel
import com.nchungdev.trackme.MainApp
import com.nchungdev.trackme.R
import com.nchungdev.trackme.databinding.ActivitySplashBinding
import com.nchungdev.trackme.event.Screen
import com.nchungdev.trackme.service.LocationService
import com.nchungdev.trackme.ui.base.activity.BaseVBActivity
import com.nchungdev.trackme.ui.util.Navigator
import com.nchungdev.trackme.ui.util.isMyServiceRunning

class SplashActivity : BaseVBActivity<SplashViewModel, ActivitySplashBinding>() {

    override fun injectDagger() {
        MainApp.getAppComponent().splashComponent().create().inject(this)
    }

    override fun getLayoutResId() = R.layout.activity_splash

    override fun initViewBinding() = ActivitySplashBinding.inflate(layoutInflater)

    override fun inits(binding: ActivitySplashBinding, savedInstanceState: Bundle?) {
        binding.loading.progress.isVisible = true
        subscribeToObservers()
        if (isMyServiceRunning(LocationService::class.java)) {
            viewModel.onRestoreTracking()
        } else {
            viewModel.onReceiveIntent(intent)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        viewModel.onReceiveIntent(intent)
    }

    private fun subscribeToObservers() {
        viewModel.navigation.observe(this) {
            when (it.event) {
                Screen.Event.HOME -> {
                    Navigator.openMainActivity(this)
                    overridePendingTransition(0, 0)
                    finish()
                }
                Screen.Event.TRACKING -> {
                    Navigator.openTrackingActivity(
                        this,
                        if (it.data is SessionModel) it.data else null
                    )
                    overridePendingTransition(0, 0)
                    finish()
                }
                else -> Unit
            }
        }
    }
}
