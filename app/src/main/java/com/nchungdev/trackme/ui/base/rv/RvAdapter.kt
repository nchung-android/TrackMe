package com.nchungdev.trackme.ui.base.rv

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class RvAdapter<VH : RvViewHolder<D>, D> constructor(
    private val context: Context,
    private val data: MutableList<D> = mutableListOf(),
    private val onClickListener: View.OnClickListener
) : RecyclerView.Adapter<VH>() {

    protected val layoutInflater = LayoutInflater.from(context)

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(data[position])
        holder.itemView.setOnClickListener(onClickListener)
        holder.itemView.tag = data[position]
    }
}
