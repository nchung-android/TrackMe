package com.nchungdev.trackme.ui.home.rv

import android.view.View
import com.bumptech.glide.RequestManager
import com.nchungdev.data.util.TimeUtils
import com.nchungdev.domain.model.SessionModel
import com.nchungdev.trackme.R
import com.nchungdev.trackme.databinding.ItemSessionBinding
import com.nchungdev.trackme.ui.base.rv.RvViewHolder

class SessionViewHolder(itemView: View, private val requestManager: RequestManager) :
    RvViewHolder<SessionModel>(itemView) {

    private val binding = ItemSessionBinding.bind(itemView)

    override fun bind(data: SessionModel) {
        requestManager.load(data.imgPath).placeholder(R.drawable.placeholder).into(binding.imgMapView)
        binding.tvAvgSpeed.text = itemView.resources.getString(R.string.speed_kmh_unit, data.avgSpeedInKmph)
        binding.tvDistance.text = itemView.resources.getString(R.string.distance_km_unit, data.distanceInKm)
        binding.tvTime.text = TimeUtils.getFormattedStopWatchTime(data.timeInMillis)
    }
}
