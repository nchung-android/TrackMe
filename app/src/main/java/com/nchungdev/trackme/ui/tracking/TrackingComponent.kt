package com.nchungdev.trackme.ui.tracking

import com.nchungdev.domain.usecase.location.GetLastLocationUseCase
import com.nchungdev.domain.usecase.location.RequestLocationUpdatesUseCase
import com.nchungdev.domain.usecase.session.CreateSessionUseCase
import com.nchungdev.domain.usecase.session.DeleteSessionUseCase
import com.nchungdev.domain.usecase.session.UpdateSessionUseCase
import com.nchungdev.trackme.di.ActivityScope
import com.nchungdev.trackme.util.BitmapHandler
import dagger.Subcomponent

@ActivityScope
@Subcomponent
interface TrackingComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): TrackingComponent
    }

    fun inject(fragment: TrackingFragment)

    fun createSessionUseCase(): CreateSessionUseCase

    fun deleteSessionUseCase(): DeleteSessionUseCase

    fun getLastLocationUseCase(): GetLastLocationUseCase

    fun requestLocationUpdatesUseCase(): RequestLocationUpdatesUseCase

    fun updateSessionUseCase(): UpdateSessionUseCase

    fun bitmapHandler(): BitmapHandler
}
