package com.nchungdev.trackme.di.module

import com.nchungdev.trackme.service.LocationComponent
import com.nchungdev.trackme.ui.detail.DetailComponent
import com.nchungdev.trackme.ui.home.HomeComponent
import com.nchungdev.trackme.ui.main.MainComponent
import com.nchungdev.trackme.ui.tracking.TrackingComponent
import dagger.Module

@Module(
    subcomponents = [
        MainComponent::class,
        TrackingComponent::class,
        LocationComponent::class,
        DetailComponent::class,
        HomeComponent::class
    ]
)
class AppModule