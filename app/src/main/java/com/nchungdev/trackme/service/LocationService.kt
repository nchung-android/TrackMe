package com.nchungdev.trackme.service

import android.content.Intent
import androidx.lifecycle.LifecycleService
import com.nchungdev.data.util.TimeUtils
import com.nchungdev.domain.usecase.base.UseCase
import com.nchungdev.domain.usecase.session.ControlSessionUpdatesUseCase
import com.nchungdev.domain.usecase.session.GetLatestSessionUseCase
import com.nchungdev.domain.util.Result
import com.nchungdev.trackme.MainApp
import com.nchungdev.trackme.R
import com.nchungdev.trackme.notification.NotificationModel
import com.nchungdev.trackme.notification.NotificationUtil
import com.nchungdev.trackme.util.Constants
import javax.inject.Inject

class LocationService : LifecycleService() {
    @Inject
    lateinit var getLatestSessionUseCase: GetLatestSessionUseCase

    @Inject
    lateinit var controlSessionUpdatesUseCase: ControlSessionUpdatesUseCase

    private var currentTime: Long = 0L

    override fun onCreate() {
        super.onCreate()
        MainApp.getAppComponent().locationServiceComponent().create().inject(this)
        getLatestSessionUseCase(UseCase.NoParams).observeForever {
            when (it) {
                is Result.Success -> {
                    currentTime = it.data.timeInMillis
                    NotificationUtil.updateStopWatch(
                        this, NotificationModel(
                            resources.getString(R.string.app_name),
                            TimeUtils.getFormattedStopWatchTime(currentTime)
                        )
                    )
                }
                is Result.Error -> Unit
                Result.Loading -> Unit
            }
        }
    }

    override fun onDestroy() {
        controlSessionUpdatesUseCase.interruptUpdates()
        currentTime = 0L
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                Constants.ACTION_START_SERVICE -> startTracking()
                Constants.ACTION_PAUSE_SERVICE -> pauseTracking()
                Constants.ACTION_STOP_SERVICE -> stopTracking()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startTracking() {
        startForeground(
            NotificationUtil.NOTIFICATION_ID,
            NotificationUtil.makeNotification(
                this, NotificationModel(
                    resources.getString(R.string.app_name),
                    TimeUtils.getFormattedStopWatchTime(currentTime)
                )
            )
        )
        controlSessionUpdatesUseCase.startUpdates()
    }

    private fun pauseTracking() {
        controlSessionUpdatesUseCase.pauseUpdates()
    }

    private fun stopTracking() {
        controlSessionUpdatesUseCase.stopUpdates()
        stopSelf()
        stopForeground(true)
    }
}
