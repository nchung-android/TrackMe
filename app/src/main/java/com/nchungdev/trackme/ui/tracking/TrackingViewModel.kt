package com.nchungdev.trackme.ui.tracking

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nchungdev.data.util.Constant
import com.nchungdev.domain.model.SessionModel
import com.nchungdev.domain.usecase.base.UseCase
import com.nchungdev.domain.usecase.session.CancelSessionUseCase
import com.nchungdev.domain.usecase.session.CreateSessionUseCase
import com.nchungdev.domain.usecase.session.GetLatestSessionUseCase
import com.nchungdev.domain.usecase.session.UpdateSessionUseCase
import com.nchungdev.domain.util.Result
import com.nchungdev.trackme.ui.helper.BitmapHandler
import kotlinx.coroutines.launch
import javax.inject.Inject


class TrackingViewModel @Inject constructor(
    private val getLatestSessionUseCase: GetLatestSessionUseCase,
    private val createSessionUseCase: CreateSessionUseCase,
    private val updateSessionUseCase: UpdateSessionUseCase,
    private val cancelSessionUseCase: CancelSessionUseCase,
    private val bitmapHandler: BitmapHandler
) : ViewModel() {

    private val _stopWatchTime = MutableLiveData<CharSequence>()
    private val _trackingState = MutableLiveData<Int>()
    private val _action = MutableLiveData<Action>()

    val stopWatchTime: LiveData<CharSequence> = _stopWatchTime

    val session: LiveData<Result<SessionModel>> = getLatestSessionUseCase(UseCase.NoParams)

    val trackingState: LiveData<Int> = _trackingState

    val action: LiveData<Action> = _action

    fun onLocationPermissionGranted() {
        _trackingState.postValue(TrackingState.ON_CREATE)
    }

    fun onTrackingClicked() {
        when (trackingState.value) {
            TrackingState.ON_CREATE -> {
                viewModelScope.launch {
                    createSessionUseCase(UseCase.NoParams)
                    _trackingState.value = TrackingState.ON_START
                }
            }
            TrackingState.ON_START -> _trackingState.value = TrackingState.ON_PAUSE
            TrackingState.ON_PAUSE -> _action.postValue(Action.CONFIRM_SAVE_SESSION)
        }
    }

    fun onContinueClicked() {
        if (trackingState.value == TrackingState.ON_PAUSE)
            _trackingState.value = TrackingState.ON_START
    }

    fun onStopWatchReceived(intent: Intent?) {
        if (intent?.action != Constant.TIMER_TICK_ACTION) {
            return
        }
        val time = intent.getCharSequenceExtra("timeInMillis") ?: return
        _stopWatchTime.postValue(time)
    }

    fun onBackPressed() = when (trackingState.value) {
        TrackingState.ON_START,
        TrackingState.ON_PAUSE -> {
            _action.postValue(Action.WARNING_EXIT_SESSION)
            true
        }
        else -> false
    }

    fun onInit(arguments: Bundle?) {
        val isResume = arguments?.getBoolean("isResume") ?: false
        _trackingState.postValue(TrackingState.ON_CREATE)
        if (isResume) {
            _trackingState.postValue(TrackingState.ON_RESUME)
        } else {
            if (session.value != null) {
                _trackingState.postValue(TrackingState.ON_RESUME)
            }
        }
    }

    fun onSaveSession(bitmap: Bitmap) {
        val session = session.value ?: return
        if (session !is Result.Success) {
            return
        }
        viewModelScope.launch {
            session.data.isCompleted = true
            session.data.imgPath = bitmapHandler.save(bitmap, session.data.id).toString()
            when (updateSessionUseCase(UpdateSessionUseCase.Params(session.data))) {
                is Result.Success -> {
                    _trackingState.postValue(TrackingState.ON_STOP)
                    _action.postValue(Action.CLOSE_SESSION)
                }
                is Result.Error -> Unit
                Result.Loading -> Unit
            }
        }
    }

    fun onCancelSession() {
        val session = session.value ?: return
        if (session !is Result.Success) {
            return
        }
        viewModelScope.launch {
            when (cancelSessionUseCase(CancelSessionUseCase.Params(session.data))) {
                is Result.Success -> {
                    _trackingState.postValue(TrackingState.ON_STOP)
                    _action.postValue(Action.CLOSE_SESSION)
                }
                is Result.Error -> Unit
                Result.Loading -> Unit
            }
        }
    }

    enum class Action {
        WARNING_EXIT_SESSION,
        CONFIRM_SAVE_SESSION,
        CLOSE_SESSION
    }
}
