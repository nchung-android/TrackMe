package com.nchungdev.domain.provider

import androidx.lifecycle.LiveData
import com.nchungdev.domain.model.LocationModel

interface LocationProvider {
    suspend fun getStartLocation(): LocationModel

    fun getLastLocation(): LiveData<LocationModel>

    fun startRequestLocationUpdates()

    fun stopRequestLocationUpdates()
}
