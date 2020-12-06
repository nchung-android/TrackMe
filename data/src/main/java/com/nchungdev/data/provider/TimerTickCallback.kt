package com.nchungdev.data.provider

import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.nchungdev.data.util.Constant.TIMER_TICK_ACTION
import javax.inject.Inject

class TimerTickCallback @Inject constructor(private val context: Context) : TimerTickListener {
    override fun onTick(time: CharSequence) {
        LocalBroadcastManager.getInstance(context).sendBroadcast(Intent(TIMER_TICK_ACTION).apply {
            putExtra("timeInMillis", time)
        })
    }
}
