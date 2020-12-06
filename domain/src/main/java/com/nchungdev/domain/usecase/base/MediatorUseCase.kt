package com.nchungdev.domain.usecase.base

import androidx.lifecycle.MediatorLiveData
import com.nchungdev.domain.util.Result

abstract class MediatorUseCase<in Params, Type> {
    protected val result = MediatorLiveData<Result<Type>>()

    operator fun invoke(parameters: Params): MediatorLiveData<Result<Type>> {
        execute(parameters)
        return result
    }

    abstract fun execute(parameters: Params)
}
