package com.nchungdev.domain.usecase.session

import com.nchungdev.domain.model.SessionModel
import com.nchungdev.domain.repository.SessionRepository
import com.nchungdev.domain.usecase.base.MediatorUseCase
import com.nchungdev.domain.usecase.base.UseCase
import com.nchungdev.domain.util.Result
import javax.inject.Inject

class GetAllSessionsUseCase @Inject constructor(
    private val sessionRepository: SessionRepository,
) :
    MediatorUseCase<UseCase.NoParams, List<SessionModel>>() {

    override fun execute(parameters: UseCase.NoParams) {
        result.postValue(Result.Loading)
        result.addSource(sessionRepository.getAllSessions()) {
            if (it.isNullOrEmpty()) {
                result.postValue(Result.Error(Exception()))
            } else {
                result.postValue(Result.Success(it))
            }
        }
    }
}
