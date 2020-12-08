package com.nchungdev.domain.repository

import androidx.lifecycle.LiveData
import com.nchungdev.domain.model.LocationModel

interface LocationRepository {

    fun getLastLocation(): LiveData<LocationModel?>

    fun startRequestLocationUpdates()

    fun stopRequestLocationUpdates()
}
