package com.nchungdev.domain.util

import java.util.concurrent.TimeUnit

object SpeedUtil {
    fun mpsToKmph(mps: Float) = 3.6f * mps

    fun calcSpeed(distanceInKm: Float?, timeInMillis: Long?) = when {
        distanceInKm == null -> 0F
        timeInMillis == null || TimeUnit.MILLISECONDS.toSeconds(timeInMillis) == 0L -> 0F
        else -> {
            val seconds = TimeUnit.MILLISECONDS.toSeconds(timeInMillis)
            if (seconds == 0L) 0F else mpsToKmph(distanceInKm * 1000 / seconds)
        }
    }
}
