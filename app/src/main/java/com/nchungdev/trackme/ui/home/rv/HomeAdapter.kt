package com.nchungdev.trackme.ui.home.rv

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.RequestManager
import com.nchungdev.domain.model.SessionModel
import com.nchungdev.trackme.R
import com.nchungdev.trackme.ui.base.rv.RvAdapter

class HomeAdapter(
    context: Context,
    data: MutableList<SessionModel> = mutableListOf(),
    private val requestManager: RequestManager,
    onClickListener: View.OnClickListener
) : RvAdapter<SessionViewHolder, SessionModel>(context, data, onClickListener) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        SessionViewHolder(layoutInflater.inflate(R.layout.item_session, parent, false), requestManager)
}
