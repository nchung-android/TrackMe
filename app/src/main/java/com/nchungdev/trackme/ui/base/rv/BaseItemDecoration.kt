package com.nchungdev.trackme.ui.base.rv

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.nchungdev.trackme.R

abstract class BaseItemDecoration(context: Context) : RecyclerView.ItemDecoration() {
    val spacing = context.resources.getDimensionPixelSize(R.dimen.spacing_normal)
    val spacingLarge = context.resources.getDimensionPixelSize(R.dimen.spacing_large)
    val spacingSmall = context.resources.getDimensionPixelSize(R.dimen.spacing_small)
}