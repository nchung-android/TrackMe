package com.nchungdev.domain.repository

interface LocationRepository {

    fun startRequestLocationUpdates()

    fun stopRequestLocationUpdates()
}
