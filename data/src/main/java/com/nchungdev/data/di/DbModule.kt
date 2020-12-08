package com.nchungdev.data.di

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import com.nchungdev.data.db.dao.LocationDAO
import com.nchungdev.data.db.dao.SessionDAO
import com.nchungdev.data.db.TrackingDatabase
import com.nchungdev.data.db.mapper.LocationMapper
import com.nchungdev.data.db.mapper.SessionMapper
import com.nchungdev.data.provider.*
import com.nchungdev.data.repository.LocationRepositoryImpl
import com.nchungdev.data.repository.SessionRepositoryImpl
import com.nchungdev.domain.provider.LocationProvider
import com.nchungdev.domain.provider.TimerProvider
import com.nchungdev.domain.repository.LocationRepository
import com.nchungdev.domain.repository.SessionRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object DbModule {

    @Provides
    @Singleton
    fun provideRunningDatabase(context: Context): TrackingDatabase =
        TrackingDatabase.invoke(context)

    @Singleton
    @Provides
    fun provideRunDAO(db: TrackingDatabase): SessionDAO = db.getSessionDao()

    @Singleton
    @Provides
    fun provideLocationDAO(db: TrackingDatabase): LocationDAO = db.getLocationDao()

    @Singleton
    @Provides
    fun provideSessionMapper(): SessionMapper = SessionMapper()

    @Singleton
    @Provides
    fun provideLocationMapper(): LocationMapper = LocationMapper()

    @Singleton
    @Provides
    fun provideSessionRepository(impl: SessionRepositoryImpl): SessionRepository = impl

    @Singleton
    @Provides
    fun provideLocationRepository(impl: LocationRepositoryImpl): LocationRepository = impl

    @Singleton
    @Provides
    fun provideFusedLocationProviderClient(context: Context): FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @Singleton
    @Provides
    fun provideLocationProvider(providerImpl: LocationProviderImpl): LocationProvider = providerImpl

    @Singleton
    @Provides
    fun provideLocationCallback(callback: LocationUpdatesCallback): LocationCallback = callback

    @Singleton
    @Provides
    fun provideStopWatch(stopWatch: StopWatchImpl): StopWatch = stopWatch

    @Singleton
    @Provides
    fun provideStopWatchListener(callback: StopWatchOnTickCallback): StopWatch.OnTickListener = callback

    @Singleton
    @Provides
    fun provideTimerProvider(providerImpl: TimerProviderImpl): TimerProvider = providerImpl
}
