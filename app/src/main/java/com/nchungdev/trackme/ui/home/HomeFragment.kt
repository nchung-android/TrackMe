package com.nchungdev.trackme.ui.home

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nchungdev.domain.model.SessionModel
import com.nchungdev.domain.util.Result
import com.nchungdev.trackme.MainApp
import com.nchungdev.trackme.databinding.FragmentHomeBinding
import com.nchungdev.trackme.ui.base.fragment.BaseVMFragment
import com.nchungdev.trackme.ui.base.rv.BaseItemDecoration
import com.nchungdev.trackme.ui.home.rv.HomeAdapter
import com.nchungdev.trackme.util.Navigator

class HomeFragment : BaseVMFragment<HomeViewModel, FragmentHomeBinding>() {

    override fun injectDagger() {
        MainApp.getAppComponent().homeComponent().create().inject(this)
    }

    override fun initViewBinding(inflater: LayoutInflater) = FragmentHomeBinding.inflate(inflater)

    override fun onBindView(binding: FragmentHomeBinding, savedInstanceState: Bundle?) {
        super.onBindView(binding, savedInstanceState)
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

        viewModel.sessionSelected.observe(viewLifecycleOwner) {
            Navigator.openSessionDetails(requireContext(), it)
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