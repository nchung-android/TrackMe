package com.nchungdev.trackme.ui.helper

import android.annotation.SuppressLint
import com.google.android.gms.maps.GoogleMap

object MapConfig {

    @SuppressLint("MissingPermission")
    operator fun invoke(map: GoogleMap) {
        with(map.uiSettings) {
            isZoomControlsEnabled = true
            isMyLocationButtonEnabled = false
        }
        map.isMyLocationEnabled = true
    }
}