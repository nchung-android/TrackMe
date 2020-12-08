package com.nchungdev.data.provider

import android.os.Handler
import android.os.Looper
import android.os.Message
import com.nchungdev.data.util.TimeUtils
import java.util.concurrent.TimeUnit
import javax.inject.Inject

interface StopWatch {
    fun init(timeInMillis: Long)

    fun getFormattedTime(): CharSequence

    fun getTimeIn(timeUnit: TimeUnit): Long

    fun start()

    fun stop()

    fun reset()
}

enum class State {
    START, STOP
}

interface TimerTickListener {
    fun onTick(time: CharSequence)
}

class StopWatchImpl @Inject constructor(private val timerTickListener: TimerTickListener) :
    StopWatch {

    // Current time of stopwatch (in millis)
    private var currentTime: Long = 0

    private var state = State.STOP

    private var handler: Handler? = null

    private val runnable = object : Runnable {
        override fun run() {
            if (state == State.START) {
                currentTime += 1000
                handler?.sendEmptyMessage(0)
                handler?.postDelayed(this, 1000)
            }
        }
    }

    override fun init(timeInMillis: Long) {
        currentTime = timeInMillis
    }

    override fun getFormattedTime() = TimeUtils.getFormattedStopWatchTime(currentTime)

    override fun start() {
        if (state != State.START) {
            state = State.START
            if (handler == null) {
                handler = object : Handler(Looper.getMainLooper()) {
                    override fun handleMessage(msg: Message) {
                        super.handleMessage(msg)
                        if (msg.what == 0) {
                            timerTickListener.onTick(TimeUtils.getFormattedStopWatchTime(currentTime))
                        }
                    }
                }
            }
            handler?.post(runnable)
        }
    }

    override fun stop() {
        if (state != State.STOP) {
            state = State.STOP
        }
    }

    override fun reset() {
        stop()
        currentTime = 0L
        handler = null
    }

    override fun getTimeIn(timeUnit: TimeUnit): Long {
        return timeUnit.convert(currentTime, TimeUnit.MILLISECONDS);
    }
}

