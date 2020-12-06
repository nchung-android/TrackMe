package com.nchungdev.trackme.ui.detail

import com.nchungdev.trackme.di.ActivityScope
import dagger.Subcomponent

@Subcomponent
@ActivityScope
interface DetailComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): DetailComponent
    }

    fun inject(fragment: DetailFragment)
}