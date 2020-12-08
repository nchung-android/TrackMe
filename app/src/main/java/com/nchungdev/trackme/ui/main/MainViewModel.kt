package com.nchungdev.trackme.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nchungdev.trackme.event.Event
import javax.inject.Inject

class MainViewModel @Inject constructor() : ViewModel() {

    private val _navigation = MutableLiveData<Event>()

    val navigation: LiveData<Event> = _navigation

    fun onOpenHomeScreen() {
        _navigation.value = Event.HOME
    }

    fun onOpenTrackingScreen() {
        _navigation.value = Event.TRACKING
    }

    fun onOpenAboutScreen() {
        _navigation.value = Event.ABOUT
    }
}
