package com.nchungdev.trackme.ui.splash

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nchungdev.domain.usecase.base.UseCase
import com.nchungdev.domain.usecase.session.GetLatestSessionAsyncUseCase
import com.nchungdev.domain.util.Result
import com.nchungdev.trackme.event.Screen
import com.nchungdev.trackme.ui.util.Actions
import kotlinx.coroutines.launch
import javax.inject.Inject

class SplashViewModel @Inject constructor(
    private val getLatestSessionAsyncUseCase: GetLatestSessionAsyncUseCase,
) : ViewModel() {

    private val _navigation = MutableLiveData<Screen>()

    val navigation: LiveData<Screen> = _navigation

    fun onReceiveIntent(intent: Intent?) {
        viewModelScope.launch {
            when (val result = getLatestSessionAsyncUseCase(UseCase.NoParams)) {
                is Result.Success -> _navigation.value = Screen(Screen.Event.TRACKING, result.data)
                else -> _navigation.value = Screen(Screen.Event.HOME)
            }
        }
    }

    fun onOpenHomeScreen() {
        _navigation.value = Screen(Screen.Event.HOME)
    }

    fun onOpenTrackingScreen() {
        _navigation.value = Screen(Screen.Event.TRACKING)
    }

    fun onOpenAboutScreen() {
        _navigation.value = Screen(Screen.Event.ABOUT)
    }

    fun onRestoreTracking() {
        viewModelScope.launch {
            when (val result = getLatestSessionAsyncUseCase(UseCase.NoParams)) {
                is Result.Success -> _navigation.value = Screen(Screen.Event.TRACKING, result.data)
                else -> _navigation.value = Screen(Screen.Event.HOME)
            }
        }
    }
}
