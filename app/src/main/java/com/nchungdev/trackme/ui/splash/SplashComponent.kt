package com.nchungdev.trackme.ui.splash

import com.nchungdev.domain.usecase.session.GetLatestSessionAsyncUseCase
import com.nchungdev.trackme.di.ActivityScope
import dagger.Subcomponent

@ActivityScope
@Subcomponent
interface SplashComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): SplashComponent
    }

    fun inject(splashActivity: SplashActivity)

    fun getLatestSessionAsyncUseCase(): GetLatestSessionAsyncUseCase
}
