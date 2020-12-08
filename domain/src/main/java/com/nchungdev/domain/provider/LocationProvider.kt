package com.nchungdev.domain.provider

import com.nchungdev.domain.model.LocationModel

interface LocationProvider {
    suspend fun getStartLocation(): LocationModel

    fun startRequestLocationUpdates()

    fun stopRequestLocationUpdates()
}
