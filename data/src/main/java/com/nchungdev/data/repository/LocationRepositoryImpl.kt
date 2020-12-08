package com.nchungdev.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.nchungdev.data.db.dao.LocationDAO
import com.nchungdev.data.db.mapper.LocationMapper
import com.nchungdev.domain.model.LocationModel
import com.nchungdev.domain.provider.LocationProvider
import com.nchungdev.domain.repository.LocationRepository
import com.nchungdev.domain.util.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val locationDAO: LocationDAO,
    private val locationProvider: LocationProvider,
    private val locationMapper: LocationMapper,
    @IoDispatcher
    private val dispatcher: CoroutineDispatcher
) : LocationRepository {

    override fun getLastLocation(): LiveData<LocationModel?> {
        val source = MediatorLiveData<LocationModel>()
        CoroutineScope(dispatcher).launch {
            val location = locationDAO.getLastLocationAsync()
            if (location == null) {
                locationProvider.getStartLocation().let {
                    locationDAO.insert(locationMapper.toDTO(it))
                    source.postValue(it)
                }
            } else {
                source.postValue(locationMapper.fromDTO(location))
            }
        }
        return source
    }

    override fun startRequestLocationUpdates() = locationProvider.startRequestLocationUpdates()

    override fun stopRequestLocationUpdates() = locationProvider.stopRequestLocationUpdates()
}
