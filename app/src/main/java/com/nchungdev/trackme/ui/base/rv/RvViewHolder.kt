package com.nchungdev.trackme.ui.base.rv

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class RvViewHolder<D>(itemView: View) : RecyclerView.ViewHolder(itemView) {

    abstract fun bind(data: D)
}