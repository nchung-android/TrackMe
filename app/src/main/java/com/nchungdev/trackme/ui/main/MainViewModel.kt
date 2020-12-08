package com.nchungdev.trackme.ui.main

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nchungdev.domain.usecase.base.UseCase
import com.nchungdev.domain.usecase.session.GetLatestSessionAsyncUseCase
import com.nchungdev.domain.util.Result
import com.nchungdev.trackme.ui.base.event.Event
import com.nchungdev.trackme.ui.base.event.Screen
import com.nchungdev.trackme.ui.util.Constants
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val getLatestSessionAsyncUseCase: GetLatestSessionAsyncUseCase,
) : ViewModel() {

    private val _navigation = MutableLiveData<Screen>()

    val navigation: LiveData<Screen> = _navigation

    fun onReceiveIntent(intent: Intent?) {
        if (intent?.action == Constants.ACTION_SHOW_TRACKING_FRAGMENT) {
            _navigation.value = Screen(Event.TRACKING)
        } else {
            viewModelScope.launch {
                when (val result = getLatestSessionAsyncUseCase(UseCase.NoParams)) {
                    is Result.Success -> _navigation.value = Screen(Event.TRACKING, result.data)
                    else -> _navigation.value = Screen(Event.HOME)
                }
            }
        }
    }

    fun onOpenHomeScreen() {
        _navigation.value = Screen(Event.HOME)
    }

    fun onOpenTrackingScreen() {
        _navigation.value = Screen(Event.TRACKING)
    }

    fun onOpenAboutScreen() {
        _navigation.value = Screen(Event.ABOUT)
    }

    fun onRestoreTracking() {
        viewModelScope.launch {
            when (val result = getLatestSessionAsyncUseCase(UseCase.NoParams)) {
                is Result.Success -> _navigation.value = Screen(Event.TRACKING, result.data)
                else -> _navigation.value = Screen(Event.HOME)
            }
        }
    }
}
