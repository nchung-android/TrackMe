package com.nchungdev.data.entity

import androidx.annotation.IntDef

@Retention(value = AnnotationRetention.SOURCE)
@IntDef(SessionState.NOT_RUNNING, SessionState.RUNNING, SessionState.FINISHED)
annotation class SessionState {
    companion object {
        const val NOT_RUNNING = 0
        const val RUNNING = 1
        const val FINISHED = 2
    }
}
