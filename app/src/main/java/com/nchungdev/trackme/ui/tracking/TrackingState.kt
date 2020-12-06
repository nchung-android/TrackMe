package com.nchungdev.trackme.ui.tracking

import androidx.annotation.IntDef

@Retention(value = AnnotationRetention.SOURCE)
@IntDef(value = [TrackingState.ON_CREATE, TrackingState.ON_START, TrackingState.ON_RESUME, TrackingState.ON_PAUSE, TrackingState.ON_STOP])
annotation class TrackingState {
    companion object {
        const val ON_CREATE = 0
        const val ON_START = 1
        const val ON_PAUSE = 2
        const val ON_RESUME = 3
        const val ON_STOP = 4
    }
}
