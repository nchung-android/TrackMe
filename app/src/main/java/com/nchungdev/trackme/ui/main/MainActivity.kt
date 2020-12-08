package com.nchungdev.trackme.ui.main

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.nchungdev.trackme.R
import com.nchungdev.trackme.databinding.ActivityMainBinding
import com.nchungdev.trackme.ui.base.activity.BaseActivity
import com.nchungdev.trackme.util.Navigator

class MainActivity : BaseActivity<ActivityMainBinding>() {
    private lateinit var navController: NavController

    override fun initViewBinding(): ActivityMainBinding =
        ActivityMainBinding.inflate(layoutInflater)

    override fun onBind(binding: ActivityMainBinding, savedInstanceState: Bundle?) {
        setupNavigation(binding)
    }

    private fun setupNavigation(binding: ActivityMainBinding) {
        navController = findNavController(R.id.nav_host_fragment)
        setupActionBarWithNavController(
            navController, AppBarConfiguration(
                setOf(R.id.nav_home, R.id.nav_about)
            )
        )
        binding.bottomNavView.setupWithNavController(navController)
        binding.fabLayout.fab.setOnClickListener {
            Navigator.openTrackingActivity(this)
        }
    }

    override fun onSupportNavigateUp() = navController.navigateUp()
}
