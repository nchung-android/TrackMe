package com.nchungdev.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SessionModel(
    var id: Int = -1,
    var imgPath: String = "",
    var timestamp: Long = 0L,
    var startLocation: LocationModel = LocationModel(),
    var polylines: List<Polyline> = mutableListOf(mutableListOf()),
    val speedInKmph: Float = 0F,
    var avgSpeedInKmph: Float = 0f,
    var distanceInKm: Float = 0f,
    var timeInMillis: Long = 0L,
    var state: Int = 0,
) : Parcelable