package com.nchungdev.trackme

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import timber.log.Timber

class AppLifecycleObserver(private val callback: (Boolean) -> Unit) : LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onMoveToForeground() {
        callback(true)
        Timber.e("OnStart")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onMoveToBackground() {
        callback(false)
        Timber.e("OnStop")
    }
}