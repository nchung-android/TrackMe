package com.nchungdev.data.provider

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.nchungdev.data.util.Constant.LOCATION_REQUEST_FASTEST_INTERVAL
import com.nchungdev.data.util.Constant.LOCATION_REQUEST_INTERVAL
import com.nchungdev.data.util.LocationPermissionNotGrantedException
import com.nchungdev.data.util.asDeferred
import com.nchungdev.data.util.toModel
import com.nchungdev.domain.model.LocationModel
import com.nchungdev.domain.provider.LocationProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@SuppressLint("MissingPermission")
@Singleton
class LocationProviderImpl @Inject constructor(
    context: Context,
    private val fusedLocationClient: FusedLocationProviderClient,
    private val locationUpdatesCallback: LocationUpdatesCallback,
) : LocationProvider {
    private val appContext = context.applicationContext

    private val locationRequest: LocationRequest = LocationRequest.create().apply {
        interval = LOCATION_REQUEST_INTERVAL
        fastestInterval = LOCATION_REQUEST_FASTEST_INTERVAL
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private val mediatorLiveData = MutableLiveData<LocationModel>()

    override suspend fun getStartLocation(): LocationModel = try {
        getStartDeviceLocation().toModel()
    } catch (e: Exception) {
        LocationModel(0.0, 0.0)
    }

    override fun getLastLocation(): LiveData<LocationModel> {
        if (hasLocationPermission()) {
            CoroutineScope(Dispatchers.IO).launch {
                val location = getStartDeviceLocation() ?: return@launch
                mediatorLiveData.postValue(LocationModel(location.latitude, location.longitude))
            }
        }
        return mediatorLiveData
    }

    @SuppressLint("MissingPermission")
    override fun startRequestLocationUpdates() {
        try {
            if (hasLocationPermission()) {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationUpdatesCallback,
                    Looper.getMainLooper()
                )
            }
        } catch (e: SecurityException) {
            Timber.e("Permission was revoked")
        }
    }

    override fun stopRequestLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationUpdatesCallback)
    }

    private suspend fun getStartDeviceLocation() =
        if (hasLocationPermission())
            fusedLocationClient.lastLocation.asDeferred()
        else
            throw LocationPermissionNotGrantedException()

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            appContext,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}
