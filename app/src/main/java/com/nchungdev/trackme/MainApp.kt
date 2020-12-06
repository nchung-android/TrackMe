package com.nchungdev.trackme

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.nchungdev.trackme.di.component.AppComponent
import com.nchungdev.trackme.di.component.DaggerAppComponent
import timber.log.Timber

class MainApp : Application() {

    var isForeground = true

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        ProcessLifecycleOwner
            .get()
            .lifecycle
            .addObserver(AppLifecycleObserver { isForeground ->
                this@MainApp.isForeground = isForeground
            })
    }

    // Instance of the AppComponent that will be used by all the Activities in the project
    val appComponent: AppComponent by lazy {
        // Creates an instance of AppComponent using its Factory constructor
        // We pass the applicationContext that will be used as Context in the graph
        DaggerAppComponent.factory().create(applicationContext)
    }

    companion object {
        lateinit var instance: MainApp

        fun getAppComponent(): AppComponent = instance.appComponent
    }
}
