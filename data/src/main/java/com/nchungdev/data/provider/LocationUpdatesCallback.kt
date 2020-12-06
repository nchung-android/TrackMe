package com.nchungdev.data.provider

import android.location.Location
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.nchungdev.data.db.SessionDAO
import com.nchungdev.data.util.toModel
import com.nchungdev.domain.model.TrackingModel
import com.nchungdev.domain.provider.TimerProvider
import com.nchungdev.domain.util.SpeedUtil
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LocationUpdatesCallback @Inject constructor() : LocationCallback() {
    @Inject
    lateinit var timerProvider: TimerProvider

    @Inject
    lateinit var sessionDAO: SessionDAO

    private var lastLocationGps: Location? = null

    private var lastTrackingModel: TrackingModel = TrackingModel()

    override fun onLocationResult(locationResult: LocationResult?) {
        super.onLocationResult(locationResult)
        (locationResult?.locations ?: return).forEach { location ->
            lastTrackingModel.apply {
                lastLocation = location.toModel()
                addPathPoint(location)
                val (distance, speed) = calcDistanceAndSpeed(location, lastLocationGps)
                speedInKmph = speed
                distanceInKm += distance
                lastLocationGps = location
            }
            Timber.e("%s", lastTrackingModel.toString())
            GlobalScope.launch {
                val sessionEntity = (sessionDAO.getLatestSessionAsync() ?: return@launch).apply {
                    timeInMillis = timerProvider.getTimeIn(TimeUnit.MILLISECONDS)
                    polylines = lastTrackingModel.polylines
                    distanceInKm = lastTrackingModel.distanceInKm
                    speedInKmph = lastTrackingModel.speedInKmph
                    avgSpeedInKmph = SpeedUtil.calcSpeed(distanceInKm , timeInMillis)
                }
                sessionDAO.update(sessionEntity)
            }
        }
    }

    private fun calcDistanceAndSpeed(current: Location, last: Location?): Pair<Float, Float> {
        val distanceInMeters = last?.distanceTo(current) ?: 0f
        return when {
            current.hasSpeed() -> Pair(distanceInMeters / 1000, SpeedUtil.mpsToKmph(current.speed))
            else -> {
                val timeInSeconds =
                    TimeUnit.MILLISECONDS.toSeconds(current.time - (last?.time ?: 0L))
                Pair(distanceInMeters / 1000, SpeedUtil.mpsToKmph(distanceInMeters / timeInSeconds))
            }
        }
    }

    private fun addPathPoint(location: Location?) {
        lastTrackingModel.polylines.apply {
            if (isEmpty()) {
                add(mutableListOf())
            }
            last().add((location ?: return).toModel())
        }
    }
}