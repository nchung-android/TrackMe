package com.nchungdev.trackme.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nchungdev.domain.model.SessionModel
import com.nchungdev.domain.usecase.base.UseCase
import com.nchungdev.domain.usecase.session.GetAllSessionsUseCase
import com.nchungdev.domain.util.Result
import com.nchungdev.trackme.event.Event
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    getAllSessionsUseCase: GetAllSessionsUseCase
) : ViewModel() {
    private val _screen = MutableLiveData<SessionModel>()

    val sessionSelected: LiveData<SessionModel> = _screen

    val sessions: LiveData<Result<List<SessionModel>>> = getAllSessionsUseCase(UseCase.NoParams)

    fun onSessionClick(sessionModel: SessionModel) {
        _screen.postValue(sessionModel)
    }
}