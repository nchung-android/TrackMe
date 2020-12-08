package com.nchungdev.data.provider

import com.nchungdev.data.db.dao.SessionDAO
import com.nchungdev.domain.provider.TimerProvider
import com.nchungdev.domain.util.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TimerProviderImpl @Inject constructor(
    private val sessionDAO: SessionDAO,
    private val stopWatch: StopWatch,
    @IoDispatcher
    private val dispatcher: CoroutineDispatcher,
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
        CoroutineScope(dispatcher).launch {
            val latestSessionAsync = sessionDAO.getLatestSessionAsync()
            Timber.e("Session %s", latestSessionAsync.toString())
            stopWatch.init(sessionDAO.getLatestSessionAsync()?.timeInMillis ?: 0)
            stopWatch.start()
        }
    }

    override fun stop() {
        stopWatch.stop()
    }
}