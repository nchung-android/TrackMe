package com.nchungdev.domain.usecase.session

import com.nchungdev.domain.provider.TimerProvider
import com.nchungdev.domain.repository.LocationRepository
import javax.inject.Inject

class ControlSessionUpdatesUseCase @Inject constructor(
    private val sessionUseCase: UpdateSessionUseCase,
    private val timerProvider: TimerProvider,
    private val locationRepository: LocationRepository,
) {

    private var isFirstRun = true

    fun startOrResumeUpdates() {
        if (isFirstRun) {
            locationRepository.startRequestLocationUpdates()
            isFirstRun = false
        }
        timerProvider.start()
    }

    fun stopUpdates() {
        locationRepository.stopRequestLocationUpdates()
        timerProvider.stop()
        isFirstRun = true
    }

    fun pauseUpdates() {
        timerProvider.stop()
    }

    fun interruptUpdates() {
        locationRepository.stopRequestLocationUpdates()
        timerProvider.reset()
    }
}
