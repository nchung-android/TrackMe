package com.nchungdev.domain.model

typealias Polyline = MutableList<LocationModel>

data class TrackingModel(
    var polylines: MutableList<Polyline> = mutableListOf(mutableListOf()),
    var lastLocation: LocationModel = LocationModel(),
    var speedInKmph: Float = 0f,
    var distanceInKm: Float = 0f
)
