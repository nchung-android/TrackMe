package com.nchungdev.trackme.service

import com.nchungdev.domain.usecase.session.ControlSessionUpdatesUseCase
import dagger.Subcomponent

@Subcomponent
interface LocationComponent {
    // Factory to create instances of UserComponent
    @Subcomponent.Factory
    interface Factory {
        fun create(): LocationComponent
    }

    // Classes that can be injected by this Component
    fun inject(service: LocationService)

    fun controlSessionUpdatesUseCase(): ControlSessionUpdatesUseCase
}