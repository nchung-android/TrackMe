package com.nchungdev.trackme.event

import android.os.Parcelable

class Screen(val event: Event, val data: Parcelable? = null) {
    enum class Event {
        HOME, TRACKING, ABOUT, SESSION_DETAIL
    }
}
