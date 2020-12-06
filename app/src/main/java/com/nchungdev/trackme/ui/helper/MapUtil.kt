package com.nchungdev.trackme.ui.helper

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.PolylineOptions
import com.nchungdev.data.db.mapper.toLatLng
import com.nchungdev.domain.model.LocationModel

object MapUtil {

    fun addLatestPolyline(
        map: GoogleMap,
        polylineOptions: PolylineOptions,
        latestPolyline: List<LocationModel>?
    ) {
        latestPolyline ?: return
        if (latestPolyline.size <= 1) return
        val preLastLatLng = latestPolyline[latestPolyline.size - 2].toLatLng()
        val last = latestPolyline.last().toLatLng()
        polylineOptions.add(preLastLatLng).add(last)
        map.addPolyline(polylineOptions)
    }

    fun addPolyline(
        map: GoogleMap,
        polylineOptions: PolylineOptions,
        locationModels: List<LocationModel>
    ) {
        polylineOptions.addAll(locationModels.map { it.toLatLng() })
        map.addPolyline(polylineOptions)
    }

    fun addAllPolylines(
        map: GoogleMap,
        polylineOptions: PolylineOptions,
        locationModels: List<List<LocationModel>>
    ) {
        locationModels.forEach { addPolyline(map, polylineOptions, it) }
    }
}
