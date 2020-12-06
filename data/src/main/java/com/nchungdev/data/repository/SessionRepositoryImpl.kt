package com.nchungdev.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.nchungdev.data.db.SessionDAO
import com.nchungdev.data.db.mapper.SessionMapper
import com.nchungdev.data.model.SessionEntity
import com.nchungdev.domain.model.SessionModel
import com.nchungdev.domain.provider.LocationProvider
import com.nchungdev.domain.repository.SessionRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepositoryImpl @Inject constructor(
    private val sessionDAO: SessionDAO,
    private val locationProvider: LocationProvider,
    private val sessionMapper: SessionMapper
) : SessionRepository {

    override suspend fun update(session: SessionModel) {
        sessionDAO.update(sessionMapper.toDTO(session))
    }

    override suspend fun createNewSession() {
        sessionDAO.insert(SessionEntity(startLocation = locationProvider.getStartLocation()))
    }

    override suspend fun save(session: SessionModel) = sessionDAO.insert(sessionMapper.toDTO(session))

    override suspend fun delete(session: SessionModel) = sessionDAO.delete(sessionMapper.toDTO(session))

    override fun getLatestSession(): LiveData<SessionModel?> = Transformations.map(sessionDAO.getLatestSession()) {
        sessionMapper.fromDTO(it ?: return@map null)
    }

    override suspend fun getLatestSessionAsync(): SessionModel? {
        val sessionEntity = sessionDAO.getLatestSessionAsync() ?: return null
        return sessionMapper.fromDTO(sessionEntity)
    }

    override fun getSession(id: Int): LiveData<SessionModel?> = Transformations.map(sessionDAO.getSession(id)) {
        sessionMapper.fromDTO(it ?: return@map null)
    }

    override fun getAllSessions(): LiveData<List<SessionModel>> =
        Transformations.map(sessionDAO.getAllRunsSortedByDate()) {
            it.map(sessionMapper::fromDTO)
        }
}
