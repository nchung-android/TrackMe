package com.nchungdev.domain.usecase.location

import com.nchungdev.domain.model.LocationModel
import com.nchungdev.domain.repository.LocationRepository
import com.nchungdev.domain.usecase.base.MediatorUseCase
import com.nchungdev.domain.usecase.base.UseCase
import com.nchungdev.domain.util.Result
import javax.inject.Inject

class GetLastLocationUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) :
    MediatorUseCase<UseCase.NoParams, LocationModel>() {

    override fun execute(parameters: UseCase.NoParams) {
        result.addSource(locationRepository.getLastLocation()) {
            if (it != null) {
                result.postValue(Result.Success(it))
            }
        }
    }
}
