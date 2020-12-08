package com.nchungdev.trackme.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nchungdev.domain.usecase.base.UseCase
import com.nchungdev.domain.usecase.session.GetLatestSessionAsyncUseCase
import com.nchungdev.domain.util.Result
import com.nchungdev.trackme.event.Event
import kotlinx.coroutines.launch
import javax.inject.Inject

class SplashViewModel @Inject constructor(
    private val getLatestSessionAsyncUseCase: GetLatestSessionAsyncUseCase,
) : ViewModel() {

    private val _navigation = MutableLiveData<Event>()

    val navigation: LiveData<Event> = _navigation

    fun onReceiveIntent() {
        viewModelScope.launch {
            when (getLatestSessionAsyncUseCase(UseCase.NoParams)) {
                is Result.Success -> _navigation.value = Event.TRACKING
                else -> _navigation.value = Event.HOME
            }
        }
    }
}
