package com.nchungdev.domain.usecase.session

import com.nchungdev.domain.model.SessionModel
import com.nchungdev.domain.repository.SessionRepository
import com.nchungdev.domain.usecase.base.UseCase
import com.nchungdev.domain.util.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class SaveSessionUseCase @Inject constructor(
    private val sessionRepository: SessionRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) :
    UseCase<SaveSessionUseCase.Params, Unit>(coroutineDispatcher) {

    override suspend fun execute(parameters: Params) = sessionRepository.save(parameters.session)

    class Params(val session: SessionModel)
}
