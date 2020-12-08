package com.nchungdev.trackme.ui.base.fragment

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.annotation.MenuRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<B : ViewBinding> : Fragment() {
    private var layout: View? = null

    private var binding: B? = null

    abstract fun injectDagger()

    @MenuRes
    protected open fun getMenuResId(): Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        injectDagger()
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(getMenuResId() != 0)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        when {
            layout == null -> {
                binding = initViewBinding(inflater)
                layout = binding?.root
                onBindView(binding as B, savedInstanceState)
            }
            layout?.parent != null -> {
                (layout?.parent as ViewGroup).removeView(layout)
            }
        }
        return layout
    }

    abstract fun initViewBinding(inflater: LayoutInflater): B

    protected open fun onBindView(binding: B, savedInstanceState: Bundle?) = Unit

    override fun onDestroyView() {
        if (binding != null) {
            binding = null
        }
        if (view != null) {
            val parentView = view?.parent
            if (parentView != null && parentView is ViewGroup) {
                parentView.removeView(view)
            }
        }
        super.onDestroyView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (getMenuResId() != 0) {
            inflater.inflate(getMenuResId(), menu)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    protected fun startService(intent: Intent) {
        context?.startService(intent)
    }

    protected fun getSupportActionBar(): ActionBar? {
        return (requireActivity() as AppCompatActivity).supportActionBar
    }

    protected fun setSupportActionBar(toolbar: Toolbar?) {
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
    }

    open fun onBackPressed(): Boolean = false
}
