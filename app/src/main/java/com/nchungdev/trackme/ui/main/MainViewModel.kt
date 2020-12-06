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
import com.nchungdev.trackme.ui.util.Constants
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val getLatestSessionAsyncUseCase: GetLatestSessionAsyncUseCase
) : ViewModel() {

    private val _navigation = MutableLiveData<Screen>()

    val navigation: LiveData<Screen> = _navigation

    fun onReceiveIntent(intent: Intent?) {
        if (intent?.action == Constants.ACTION_SHOW_TRACKING_FRAGMENT) {
            _navigation.value = Screen(Event.TRACKING, true)
        } else {
            viewModelScope.launch {
                when (val result = getLatestSessionAsyncUseCase(UseCase.NoParams)) {
                    is Result.Success ->
                        if (result.data != null) {
                            _navigation.value = Screen(Event.TRACKING)
                        }
                    else -> _navigation.value = Screen(Event.HOME)
                }
            }
        }
    }

    fun onOpenHomeScreen() {
        _navigation.value = Screen(Event.HOME)
    }

    fun onOpenTrackingScreen(isResumed: Boolean) {
        _navigation.value = Screen(Event.TRACKING, isResumed)
    }

    fun onOpenAboutScreen() {
        _navigation.value = Screen(Event.ABOUT)
    }

    class Screen(val event: Event, val isResumed: Boolean = false)
}
