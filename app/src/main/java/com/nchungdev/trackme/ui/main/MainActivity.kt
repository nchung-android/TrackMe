package com.nchungdev.trackme.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.nchungdev.domain.model.SessionModel
import com.nchungdev.trackme.MainApp
import com.nchungdev.trackme.R
import com.nchungdev.trackme.databinding.ActivityMainBinding
import com.nchungdev.trackme.event.Screen
import com.nchungdev.trackme.service.LocationService
import com.nchungdev.trackme.ui.base.activity.BaseVBActivity
import com.nchungdev.trackme.ui.util.Navigator
import com.nchungdev.trackme.ui.util.findNavHostFragment
import com.nchungdev.trackme.ui.util.isMyServiceRunning

class MainActivity : BaseVBActivity<MainViewModel, ActivityMainBinding>() {

    override fun injectDagger() {
        MainApp.getAppComponent().mainComponent().create().inject(this)
    }

    override fun getLayoutResId() = R.layout.activity_main

    override fun initViewBinding() = ActivityMainBinding.inflate(layoutInflater)

    override fun inits(binding: ActivityMainBinding, savedInstanceState: Bundle?) {
        subscribeToObservers()
        setupNavigation(binding)
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

    private fun setupNavigation(binding: ActivityMainBinding) {
        val navController = supportFragmentManager.findNavHostFragment(R.id.nav_host_fragment)
        val appBarConfiguration =
            AppBarConfiguration(setOf(R.id.homeFragment, R.id.recordFragment, R.id.aboutFragment))
        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(this, navController, appBarConfiguration)

        binding.bottomNavView.apply {
            setupWithNavController(navController)
            setOnNavigationItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.home -> viewModel.onOpenHomeScreen()
                    R.id.about -> viewModel.onOpenAboutScreen()
                }
                return@setOnNavigationItemSelectedListener true
            }
        }
        binding.fab.fab.setOnClickListener {
            viewModel.onOpenTrackingScreen()
        }
    }

    private fun subscribeToObservers() {
        val navController = supportFragmentManager.findNavHostFragment(R.id.nav_host_fragment)
        viewModel.navigation.observe(this) {
            when (it.event) {
                Screen.Event.HOME -> navController.navigate(R.id.homeFragment)
                Screen.Event.ABOUT -> navController.navigate(R.id.aboutFragment)
                Screen.Event.TRACKING -> {
                    Navigator.openTrackingActivity(
                        this,
                        if (it.data is SessionModel) it.data else null
                    )
                }
                else -> Unit
            }
        }
    }
}
