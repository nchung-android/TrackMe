package com.nchungdev.data.provider

import com.nchungdev.domain.provider.TimerProvider
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TimerProviderImpl @Inject constructor(
    private val stopWatch: StopWatch
) : TimerProvider {

    override fun reset() {
        stopWatch.reset()
    }

    override fun getFormattedTime(): CharSequence {
        return stopWatch.getFormattedTime()
    }

    override fun getTimeIn(timeUnit: TimeUnit): Long {
        return stopWatch.getTimeIn(timeUnit)
    }

    override fun start() {
        stopWatch.start()
    }

    override fun stop() {
        stopWatch.stop()
    }
}