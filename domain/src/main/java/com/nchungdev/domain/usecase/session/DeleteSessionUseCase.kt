package com.nchungdev.domain.usecase.session

import com.nchungdev.domain.model.SessionModel
import com.nchungdev.domain.repository.SessionRepository
import com.nchungdev.domain.usecase.base.UseCase
import com.nchungdev.domain.util.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class DeleteSessionUseCase @Inject constructor(
    private val sessionRepository: SessionRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) :
    UseCase<DeleteSessionUseCase.Params, Unit>(coroutineDispatcher) {

    override suspend fun execute(parameters: Params) {
        sessionRepository.delete(parameters.session)
    }

    class Params(val session: SessionModel)
}