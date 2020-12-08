package com.nchungdev.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.nchungdev.data.db.CombinedLiveData
import com.nchungdev.data.db.LocationDAO
import com.nchungdev.data.db.mapper.LocationMapper
import com.nchungdev.domain.model.LocationModel
import com.nchungdev.domain.provider.LocationProvider
import com.nchungdev.domain.repository.LocationRepository
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val locationDAO: LocationDAO,
    private val locationProvider: LocationProvider,
    private val locationMapper: LocationMapper
) : LocationRepository {

    private val data = CombinedLiveData(
        Transformations.map(locationDAO.getLastLocation()) {
            locationMapper.fromDTO(it ?: return@map null)
        },
        locationProvider.getLastLocation()
    ) { data1, data2 ->
        if (data1 != null) return@CombinedLiveData data1
        if (data2 != null) return@CombinedLiveData data2
        return@CombinedLiveData LocationModel()
    }

    override fun getLastLocation(): LiveData<LocationModel?> = data

    override fun startRequestLocationUpdates() = locationProvider.startRequestLocationUpdates()

    override fun stopRequestLocationUpdates() = locationProvider.stopRequestLocationUpdates()
}
