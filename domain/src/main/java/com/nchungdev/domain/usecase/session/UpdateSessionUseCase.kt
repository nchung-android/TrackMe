package com.nchungdev.domain.usecase.session

import com.nchungdev.domain.model.SessionModel
import com.nchungdev.domain.repository.SessionRepository
import com.nchungdev.domain.usecase.base.UseCase
import com.nchungdev.domain.util.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class UpdateSessionUseCase @Inject constructor(
    private val sessionRepository: SessionRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) :
    UseCase<UpdateSessionUseCase.Params, Unit>(coroutineDispatcher) {

    override suspend fun execute(parameters: Params) {
        sessionRepository.update(parameters.sessionModel)
    }

    open class Params(val sessionModel: SessionModel)
}
