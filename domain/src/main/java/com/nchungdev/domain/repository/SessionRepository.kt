package com.nchungdev.domain.repository

import androidx.lifecycle.LiveData
import com.nchungdev.domain.model.SessionModel

interface SessionRepository {

    suspend fun save(session: SessionModel)

    suspend fun delete(session: SessionModel)

    suspend fun update(session: SessionModel)

    suspend fun createNewSession(state: Int)

    suspend fun getLatestSessionAsync(): SessionModel?

    fun getSession(id: Int): LiveData<SessionModel?>

    fun getLatestSession(): LiveData<SessionModel?>

    fun getAllSessions(): LiveData<List<SessionModel>>
}
