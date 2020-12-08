package com.nchungdev.trackme.di.component

import android.content.Context
import com.nchungdev.data.di.DbModule
import com.nchungdev.domain.usecase.location.GetLastLocationUseCase
import com.nchungdev.domain.usecase.location.RequestLocationUpdatesUseCase
import com.nchungdev.domain.usecase.session.ControlSessionUpdatesUseCase
import com.nchungdev.domain.usecase.session.*
import com.nchungdev.trackme.di.module.AppModule
import com.nchungdev.trackme.di.module.CoroutinesModule
import com.nchungdev.trackme.service.LocationComponent
import com.nchungdev.trackme.ui.detail.DetailComponent
import com.nchungdev.trackme.ui.home.HomeComponent
import com.nchungdev.trackme.ui.splash.SplashComponent
import com.nchungdev.trackme.ui.tracking.TrackingComponent
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [DbModule::class, AppModule::class, CoroutinesModule::class])
interface AppComponent {

    // Factory to create instances of the AppComponent
    @Component.Factory
    interface Factory {
        // With @BindsInstance, the Context passed in will be available in the graph
        fun create(@BindsInstance context: Context): AppComponent
    }

    // Types that can be retrieved from the graph
    fun splashComponent(): SplashComponent.Factory
    fun trackingComponent(): TrackingComponent.Factory
    fun locationServiceComponent(): LocationComponent.Factory
    fun homeComponent(): HomeComponent.Factory
    fun detailComponent(): DetailComponent.Factory

    // use case
    fun trackingLocationUseCase(): GetLatestSessionUseCase
    fun saveSessionUseCase(): SaveSessionUseCase
    fun cancelSessionUseCase(): DeleteSessionUseCase
    fun controlSessionUpdatesUseCase(): ControlSessionUpdatesUseCase
    fun updateSessionUseCase(): UpdateSessionUseCase
    fun createSessionUseCase(): CreateSessionUseCase
    fun getLatestSessionAsynUseCase(): GetLatestSessionAsyncUseCase
    fun getCurrentLocationUseCase(): GetLastLocationUseCase
    fun requestLocationUpdatesUseCase(): RequestLocationUpdatesUseCase
}