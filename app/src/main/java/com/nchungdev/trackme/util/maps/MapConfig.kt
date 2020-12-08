package com.nchungdev.trackme.util.maps

import android.annotation.SuppressLint
import com.google.android.gms.maps.GoogleMap

object MapConfig {

    const val DEFAULT_ZOOM = 15f

    @SuppressLint("MissingPermission")
    operator fun invoke(map: GoogleMap) {
        with(map.uiSettings) {
            isZoomControlsEnabled = true
            isMyLocationButtonEnabled = false
        }
        map.isMyLocationEnabled = true
    }
}