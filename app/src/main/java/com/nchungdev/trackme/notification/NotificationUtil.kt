package com.nchungdev.trackme.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.nchungdev.data.util.Util
import com.nchungdev.trackme.R
import com.nchungdev.trackme.ui.main.MainActivity
import com.nchungdev.trackme.ui.util.Constants

object NotificationUtil {
    const val NOTIFICATION_ID = 1
    private const val NOTIFICATION_TRACKING_CHANNEL_ID = "tracking_channel"
    private const val NOTIFICATION_TRACKING_CHANNEL_NAME = "Tracking"

    private fun createPendingIntent(context: Context) = PendingIntent.getActivity(
        context,
        0,
        Intent(context, MainActivity::class.java).also {
            it.action = Constants.ACTION_SHOW_TRACKING_FRAGMENT
        },
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    @RequiresApi(Build.VERSION_CODES.O)
    fun createChannel(context: Context) {
        getNotificationManager(context)?.createNotificationChannel(NotificationChannel(
            NOTIFICATION_TRACKING_CHANNEL_ID,
            NOTIFICATION_TRACKING_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        ))
    }

    private fun createNotification(context: Context, notificationModel: NotificationModel): Notification =
        NotificationCompat.Builder(context, NOTIFICATION_TRACKING_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_record)
            .setContentTitle(notificationModel.title)
            .setContentText(notificationModel.content)
            .setContentIntent(createPendingIntent(context))
            .build()

    private fun getNotificationManager(context: Context) =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

    fun updateStopWatch(context: Context, notificationModel: NotificationModel) {
        getNotificationManager(context)?.notify(NOTIFICATION_ID, makeNotification(context, notificationModel))
    }

    fun makeNotification(context: Context, notificationModel: NotificationModel): Notification {
        if (Util.hasO()) {
            createChannel(context)
        }
        return createNotification(context, notificationModel)
    }
}
