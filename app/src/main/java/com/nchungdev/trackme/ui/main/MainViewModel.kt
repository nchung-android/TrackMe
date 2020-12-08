package com.nchungdev.trackme.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nchungdev.trackme.event.Screen
import javax.inject.Inject

class MainViewModel @Inject constructor() : ViewModel() {

    private val _navigation = MutableLiveData<Screen>()

    val navigation: LiveData<Screen> = _navigation

    fun onOpenHomeScreen() {
        _navigation.value = Screen(Screen.Event.HOME)
    }

    fun onOpenTrackingScreen() {
        _navigation.value = Screen(Screen.Event.TRACKING)
    }

    fun onOpenAboutScreen() {
        _navigation.value = Screen(Screen.Event.ABOUT)
    }
}
