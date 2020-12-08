package com.nchungdev.trackme.ui.home

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nchungdev.domain.model.SessionModel
import com.nchungdev.domain.util.Result
import com.nchungdev.trackme.MainApp
import com.nchungdev.trackme.R
import com.nchungdev.trackme.databinding.FragmentHomeBinding
import com.nchungdev.trackme.event.Screen
import com.nchungdev.trackme.ui.base.fragment.BaseVBFragment
import com.nchungdev.trackme.ui.base.rv.BaseItemDecoration
import com.nchungdev.trackme.ui.home.rv.HomeAdapter
import com.nchungdev.trackme.ui.util.Navigator

class HomeFragment : BaseVBFragment<HomeViewModel, FragmentHomeBinding>() {
    override fun getLayoutResId() = R.layout.fragment_home

    override fun injectDagger() {
        MainApp.getAppComponent().homeComponent().create().inject(this)
    }

    override fun initViewBinding(view: View) = FragmentHomeBinding.bind(view)

    override fun inits(binding: FragmentHomeBinding, savedInstanceState: Bundle?) {
        super.inits(binding, savedInstanceState)
        val rv = binding.content.recyclerView.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            addItemDecoration(SpacingItemDecoration(requireContext()))
        }

        viewModel.sessions.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Success -> {
                    rv.adapter =
                        HomeAdapter(
                            requireContext(),
                            it.data.toMutableList(),
                            Glide.with(this)
                        ) { v ->
                            if (v.tag is SessionModel) {
                                viewModel.onSessionClick(v.tag as SessionModel)
                            }
                        }
                    rv.isVisible = true
                    binding.error.tvError.isVisible = false
                    binding.loading.progress.isVisible = false
                }
                is Result.Error -> {
                    rv.isVisible = false
                    binding.error.tvError.isVisible = true
                    binding.loading.progress.isVisible = false
                }
                Result.Loading -> {
                    rv.isVisible = false
                    binding.error.tvError.isVisible = false
                    binding.loading.progress.isVisible = true
                }
            }
        }

        viewModel.screen.observe(viewLifecycleOwner) {
            when (it.event) {
                Screen.Event.SESSION_DETAIL -> {
                    if (it.data is SessionModel) {
                        Navigator.openSessionDetails(requireContext(), it.data)
                    }
                }
                else -> Unit
            }
        }
    }

    inner class SpacingItemDecoration(context: Context) : BaseItemDecoration(context) {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            outRect.left = spacing
            outRect.right = spacing
            val adapterPosition = parent.findContainingViewHolder(view)?.adapterPosition
            if (adapterPosition == 0) outRect.top = spacing
            else outRect.top = spacingSmall
            val count = parent.adapter?.itemCount ?: return
            if (adapterPosition == count - 1) outRect.bottom = spacing
            else outRect.bottom = spacingSmall
        }
    }
}