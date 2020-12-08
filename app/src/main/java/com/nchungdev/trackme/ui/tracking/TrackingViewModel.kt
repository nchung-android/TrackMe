package com.nchungdev.trackme.ui.tracking

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.lifecycle.*
import com.nchungdev.data.entity.SessionState
import com.nchungdev.data.provider.TimerTickCallback
import com.nchungdev.domain.model.LocationModel
import com.nchungdev.domain.model.SessionModel
import com.nchungdev.domain.usecase.base.UseCase
import com.nchungdev.domain.usecase.location.GetLastLocationUseCase
import com.nchungdev.domain.usecase.location.RequestLocationUpdatesUseCase
import com.nchungdev.domain.usecase.session.CreateSessionUseCase
import com.nchungdev.domain.usecase.session.DeleteSessionUseCase
import com.nchungdev.domain.usecase.session.GetLatestSessionUseCase
import com.nchungdev.domain.usecase.session.UpdateSessionUseCase
import com.nchungdev.domain.util.Result
import com.nchungdev.trackme.ui.helper.BitmapHandler
import kotlinx.coroutines.launch
import javax.inject.Inject


class TrackingViewModel @Inject constructor(
    private val getLastLocationUseCase: GetLastLocationUseCase,
    private val getLatestSessionUseCase: GetLatestSessionUseCase,
    private val createSessionUseCase: CreateSessionUseCase,
    private val updateSessionUseCase: UpdateSessionUseCase,
    private val deleteSessionUseCase: DeleteSessionUseCase,
    private val requestLocationUpdatesUseCase: RequestLocationUpdatesUseCase,
    private val bitmapHandler: BitmapHandler,
) : ViewModel() {

    private val _stopWatchTime = MutableLiveData<CharSequence>()
    private val _currentLocation = MediatorLiveData<Result<LocationModel>>()
    private val _session = MediatorLiveData<Result<SessionModel>>()
    private val _trackingState = MutableLiveData<TrackingState>()
    private val _action = MutableLiveData<Action>()

    val stopWatchTime: LiveData<CharSequence> = _stopWatchTime

    val currentLocation: LiveData<Result<LocationModel>> = _currentLocation

    val session: LiveData<Result<SessionModel>> = _session

    val trackingState: LiveData<TrackingState> = _trackingState

    val action: LiveData<Action> = _action

    fun onInit(arguments: Bundle?, isServiceRunning: Boolean) {
        val sessionModel = arguments?.getParcelable<SessionModel>(TrackingFragment.EXTRA_SESSION)
        if (sessionModel != null) {
            val trackingState = if (isServiceRunning) TrackingState.START else TrackingState.PAUSE
            _trackingState.postValue(trackingState)
            _session.postValue(Result.Success(sessionModel))
        }
    }

    fun onLocationPermissionGranted() {
        _trackingState.postValue(TrackingState.READY)
        _currentLocation.addSource(getLastLocationUseCase(UseCase.NoParams)) {
            if (trackingState.value != TrackingState.START) {
                _currentLocation.postValue(it)
            }
        }
        _session.addSource(getLatestSessionUseCase(UseCase.NoParams)) {
            if (trackingState.value == TrackingState.START) {
                _session.postValue(it)
            }
        }
    }

    fun onTrackingClicked() {
        when (trackingState.value) {
            TrackingState.READY -> {
                viewModelScope.launch {
                    val session = createSessionUseCase(UseCase.NoParams)
                    if (session is Result.Success) {
                        val sessionModel = session.data
                        sessionModel.state = SessionState.RUNNING
                        updateSessionUseCase(UpdateSessionUseCase.Params(sessionModel))
                        _trackingState.value = TrackingState.START
                    }
                }
            }
            TrackingState.START -> {
                viewModelScope.launch {
                    val session = getSession() ?: return@launch
                    session.state = SessionState.READY
                    updateSessionUseCase(UpdateSessionUseCase.Params(getSession() ?: return@launch))
                    _trackingState.postValue(TrackingState.PAUSE)
                }
            }
            TrackingState.PAUSE -> _action.postValue(Action.CONFIRM_SAVE_SESSION)
            else -> Unit
        }
    }

    fun onContinueClicked() {
        if (trackingState.value == TrackingState.PAUSE) {
            _trackingState.value = TrackingState.START
            requestLocationUpdatesUseCase.stopUpdates()
        }
    }

    fun onStopWatchReceived(intent: Intent?) {
        _stopWatchTime.postValue(TimerTickCallback.extractResult(intent) ?: return)
    }

    fun onBackPressed() = when (trackingState.value) {
        TrackingState.START,
        TrackingState.PAUSE,
        -> {
            _action.postValue(Action.WARNING_EXIT_SESSION)
            true
        }
        else -> false
    }

    fun onSaveSession(bitmap: Bitmap) {
        val session = getSession() ?: return
        viewModelScope.launch {
            session.state = SessionState.FINISHED
            session.imgPath = bitmapHandler.save(bitmap, session.id).toString()
            when (updateSessionUseCase(UpdateSessionUseCase.Params(session))) {
                is Result.Success -> {
                    _trackingState.postValue(TrackingState.FINISH)
                    _action.postValue(Action.CLOSE_SESSION)
                }
                is Result.Error -> Unit
                Result.Loading -> Unit
            }
        }
    }

    fun onCancelSession() {
        val session = getSession() ?: return
        viewModelScope.launch {
            when (deleteSessionUseCase(DeleteSessionUseCase.Params(session))) {
                is Result.Success -> {
                    _trackingState.postValue(TrackingState.FINISH)
                    _action.postValue(Action.CLOSE_SESSION)
                }
                is Result.Error -> Unit
                Result.Loading -> Unit
            }
        }
    }

    override fun onCleared() {
        requestLocationUpdatesUseCase.stopUpdates()
        super.onCleared()
    }

    fun onStartRequestLocationUpdates() {
        requestLocationUpdatesUseCase.startUpdates()
    }

    fun onStopRequestLocationUpdates() {
        requestLocationUpdatesUseCase.stopUpdates()
    }

    private fun getSession(): SessionModel? {
        val session = session.value ?: return null
        if (session !is Result.Success) {
            return null
        }
        return session.data
    }

    enum class Action {
        WARNING_EXIT_SESSION,
        CONFIRM_SAVE_SESSION,
        CLOSE_SESSION
    }
}
