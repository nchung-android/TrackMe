package com.nchungdev.domain.usecase.session

import com.nchungdev.domain.model.SessionModel
import com.nchungdev.domain.repository.SessionRepository
import com.nchungdev.domain.usecase.base.UseCase
import com.nchungdev.domain.util.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class GetLatestSessionAsyncUseCase @Inject constructor(
    private val sessionRepository: SessionRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : UseCase<UseCase.NoParams, SessionModel?>(coroutineDispatcher) {

    override suspend fun execute(parameters: NoParams): SessionModel? {
        return sessionRepository.getLatestSessionAsync()
    }
}
