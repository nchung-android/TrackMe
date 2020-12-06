package com.nchungdev.domain.util

import java.util.concurrent.TimeUnit

object SpeedUtil {
    fun mpsToKmph(mps: Float) = 3.6f * mps

    fun calcSpeed(distanceInKm: Float, timeInMillis: Long): Float {
        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeInMillis)
        return if (seconds == 0L) 0F else mpsToKmph(distanceInKm * 1000 / seconds)
    }
}