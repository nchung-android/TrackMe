package com.nchungdev.trackme.ui.detail

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nchungdev.domain.model.SessionModel
import javax.inject.Inject

class DetailViewModel @Inject constructor() : ViewModel() {
    private val _session = MutableLiveData<SessionModel>()

    val session: LiveData<SessionModel> = _session

    fun onReceiveIntent(bundle: Bundle?) {
        val sessionModel: SessionModel = bundle?.getParcelable(DetailFragment.EXTRA_SESSION) ?: return
        _session.postValue(sessionModel)
    }

}
