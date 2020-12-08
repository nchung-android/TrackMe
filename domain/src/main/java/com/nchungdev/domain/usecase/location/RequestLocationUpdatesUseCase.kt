package com.nchungdev.domain.usecase.location

import com.nchungdev.domain.repository.LocationRepository
import javax.inject.Inject

class RequestLocationUpdatesUseCase @Inject constructor(
    private val locationRepository: LocationRepository,
) {
    fun startUpdates() = locationRepository.startRequestLocationUpdates()

    fun stopUpdates() = locationRepository.stopRequestLocationUpdates()
}