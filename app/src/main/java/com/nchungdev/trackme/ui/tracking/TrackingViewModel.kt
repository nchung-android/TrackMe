package com.nchungdev.trackme.ui.tracking

import android.graphics.Bitmap
import android.os.Bundle
import androidx.lifecycle.*
import com.nchungdev.data.entity.SessionState
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
import com.nchungdev.trackme.ui.util.BitmapHandler
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

    private val _currentLocation = MediatorLiveData<Result<LocationModel>>()
    private val _session = MediatorLiveData<Result<SessionModel>>()
    private val _trackingState = MutableLiveData<TrackingState>()
    private val _event = MutableLiveData<Event>()

    val currentLocation: LiveData<Result<LocationModel>> = _currentLocation

    val session: LiveData<Result<SessionModel>> = _session

    val trackingState: LiveData<TrackingState> = _trackingState

    val event: LiveData<Event> = _event

    private var shouldRestore = false

    fun onInit(data: Bundle?) {
        val isFromNotif = data?.getBoolean(TrackingFragment.EXTRA_OPEN_FROM_NOTIF, false)
        val isFromNewIntent = data?.getBoolean(TrackingFragment.EXTRA_OPEN_FROM_SPLASH, false)
        shouldRestore = isFromNotif == true || isFromNewIntent == true
    }

    fun onLocationPermissionGranted() {
        _trackingState.value = TrackingState.START
        _currentLocation.addSource(getLastLocationUseCase(UseCase.NoParams)) {
            if (trackingState.value != TrackingState.RUNNING) {
                _currentLocation.postValue(it)
            }
        }
        _currentLocation.postValue(Result.Success(LocationModel(10.768484668348659, 106.66023725668128)))
        _session.addSource(getLatestSessionUseCase(UseCase.NoParams)) {
            if (shouldRestore && it is Result.Success) {
                if (it.data.state == SessionState.RUNNING) {
                    _trackingState.postValue(TrackingState.RUNNING)
                } else {
                    _trackingState.postValue(TrackingState.PAUSE)
                }
                _session.postValue(it)
                shouldRestore = false
            } else if (trackingState.value == TrackingState.RUNNING) {
                _session.postValue(it)
            }
        }
    }

    fun onTrackingClicked() {
        when (trackingState.value) {
            TrackingState.START -> {
                viewModelScope.launch {
                    when (createSessionUseCase(CreateSessionUseCase.Params(SessionState.RUNNING))) {
                        is Result.Success -> _trackingState.postValue(TrackingState.RUNNING)
                        is Result.Error -> _event.postValue(Event.CLOSE_SESSION)
                        else -> Unit
                    }
                }
            }
            TrackingState.RUNNING -> {
                viewModelScope.launch {
                    val session = getSession() ?: return@launch
                    session.state = SessionState.NOT_RUNNING
                    updateSessionUseCase(UpdateSessionUseCase.Params(session))
                    _trackingState.postValue(TrackingState.PAUSE)
                }
            }
            TrackingState.PAUSE -> _event.postValue(Event.CONFIRM_SAVE_SESSION)
            else -> Unit
        }
    }

    fun onContinueClicked() {
        if (trackingState.value == TrackingState.PAUSE) {
            viewModelScope.launch {
                val session = getSession() ?: return@launch
                session.state = SessionState.RUNNING
                updateSessionUseCase(UpdateSessionUseCase.Params(session))
                _trackingState.postValue(TrackingState.RUNNING)
            }
            requestLocationUpdatesUseCase.stopUpdates()
        }
    }

    fun onBackPressed() = when (trackingState.value) {
        TrackingState.RUNNING,
        TrackingState.PAUSE,
        -> {
            _event.postValue(Event.WARNING_EXIT_SESSION)
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
                    _event.postValue(Event.CLOSE_SESSION)
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
                    _event.postValue(Event.CLOSE_SESSION)
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

    enum class Event {
        WARNING_EXIT_SESSION,
        CONFIRM_SAVE_SESSION,
        CLOSE_SESSION,
    }
}
