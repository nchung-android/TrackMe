package com.nchungdev.trackme.ui.helper

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.PolylineOptions
import com.nchungdev.data.db.mapper.toLatLng
import com.nchungdev.domain.model.LocationModel

class PolylineHelper(private val map: GoogleMap, private val polylineOptions: PolylineOptions) {

    fun addLatestPolyline(latestPolyline: List<LocationModel>?) {
        latestPolyline ?: return
        if (latestPolyline.size <= 1) return
        val preLastLatLng = latestPolyline[latestPolyline.size - 2].toLatLng()
        val last = latestPolyline.last().toLatLng()
        polylineOptions.add(preLastLatLng).add(last)
        map.addPolyline(polylineOptions)
    }

    fun addAllPolylines(locationModels: List<List<LocationModel>>) {
        locationModels.forEach { addPolyline(it) }
    }

    private fun addPolyline(locationModels: List<LocationModel>) {
        polylineOptions.addAll(locationModels.map { it.toLatLng() })
        map.addPolyline(polylineOptions)
    }
}