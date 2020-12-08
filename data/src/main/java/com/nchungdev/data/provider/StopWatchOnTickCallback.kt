package com.nchungdev.data.provider

import com.nchungdev.data.db.dao.SessionDAO
import com.nchungdev.domain.util.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class StopWatchOnTickCallback @Inject constructor(
    private val sessionDAO: SessionDAO,
    @IoDispatcher
    private val dispatcher: CoroutineDispatcher,
) :
    StopWatch.OnTickListener {

    override fun onTick(timeInMillis: Long) {
        CoroutineScope(dispatcher).launch {
            val session = sessionDAO.getLatestSessionAsync() ?: return@launch
            session.timeInMillis = timeInMillis
            sessionDAO.update(session)
        }
    }
}
