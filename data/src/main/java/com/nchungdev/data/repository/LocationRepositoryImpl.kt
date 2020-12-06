package com.nchungdev.data.repository

import com.nchungdev.domain.provider.LocationProvider
import com.nchungdev.domain.repository.LocationRepository
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val locationProvider: LocationProvider
) : LocationRepository {

    override fun startRequestLocationUpdates() = locationProvider.startRequestLocationUpdates()

    override fun stopRequestLocationUpdates() = locationProvider.stopRequestLocationUpdates()
}
