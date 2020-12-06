package com.nchungdev.trackme.ui.tracking

import com.nchungdev.domain.usecase.session.GetLatestSessionUseCase
import com.nchungdev.trackme.di.ActivityScope
import com.nchungdev.trackme.ui.helper.BitmapHandler
import dagger.Subcomponent

@ActivityScope
@Subcomponent
interface TrackingComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): TrackingComponent
    }

    fun inject(fragment: TrackingFragment)

    fun getStartLocationUseCase(): GetLatestSessionUseCase

    fun bitmapHandler(): BitmapHandler
}
