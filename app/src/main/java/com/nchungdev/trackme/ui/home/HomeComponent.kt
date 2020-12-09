package com.nchungdev.trackme.ui.home

import com.nchungdev.domain.usecase.session.GetAllSessionsUseCase
import com.nchungdev.trackme.di.ActivityScope
import dagger.Subcomponent

@ActivityScope
@Subcomponent
interface HomeComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): HomeComponent
    }

    fun inject(fragment: HomeFragment)

    fun getAllSessionsUseCase(): GetAllSessionsUseCase
}