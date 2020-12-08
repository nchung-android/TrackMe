package com.nchungdev.trackme.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.LifecycleService
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.nchungdev.data.provider.TimerTickCallback
import com.nchungdev.data.util.Constant
import com.nchungdev.domain.usecase.session.ControlSessionUpdatesUseCase
import com.nchungdev.trackme.MainApp
import com.nchungdev.trackme.R
import com.nchungdev.trackme.notification.NotificationModel
import com.nchungdev.trackme.notification.NotificationUtil
import com.nchungdev.trackme.ui.util.Constants
import com.nchungdev.trackme.ui.util.isMyServiceRunning
import javax.inject.Inject


class LocationService : LifecycleService() {

    @Inject
    lateinit var controlSessionUpdatesUseCase: ControlSessionUpdatesUseCase

    private var currentTime: CharSequence = ""

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            currentTime = TimerTickCallback.extractResult(intent) ?: return
            if (isMyServiceRunning(LocationService::class.java)) {
                NotificationUtil.updateStopWatch(
                    context, NotificationModel(
                        resources.getString(R.string.app_name),
                        currentTime
                    )
                )
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        MainApp.getAppComponent().locationServiceComponent().create().inject(this)
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(receiver, IntentFilter(Constant.TIMER_TICK_ACTION))
    }

    override fun onDestroy() {
        controlSessionUpdatesUseCase.interruptUpdates()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
        currentTime = ""
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
                    currentTime
                )
            )
        )
        controlSessionUpdatesUseCase.startOrResumeUpdates()
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
