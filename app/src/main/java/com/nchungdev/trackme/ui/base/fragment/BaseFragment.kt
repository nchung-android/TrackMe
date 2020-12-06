package com.nchungdev.trackme.ui.base.fragment

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.*
import androidx.annotation.LayoutRes
import androidx.annotation.MenuRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager

abstract class BaseFragment : Fragment() {
    private var layout: View? = null

    abstract fun injectDagger()

    private val registerReceivers = HashSet<Int>()

    @LayoutRes
    abstract fun getLayoutResId(): Int

    @MenuRes
    protected open fun getMenuResId(): Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        injectDagger()
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        when {
            layout == null -> {
                layout = inflater.inflate(getLayoutResId(), container, false)
                inits(layout as View, savedInstanceState)
            }
            layout?.parent != null -> {
                (layout?.parent as ViewGroup).removeView(layout)
            }
        }
        return layout
    }

    protected open fun inits(view: View, savedInstanceState: Bundle?) = Unit

    override fun onDestroyView() {
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

    protected fun registerReceiver(receiver: BroadcastReceiver, intentFilter: IntentFilter) {
        if (!registerReceivers.contains(receiver.hashCode())) {
            LocalBroadcastManager.getInstance(requireContext()).registerReceiver(receiver, intentFilter)
            registerReceivers.add(receiver.hashCode())
        }
    }

    protected fun unregisterReceiver(receiver: BroadcastReceiver) {
        if (registerReceivers.contains(receiver.hashCode())) {
            LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(receiver)
            registerReceivers.remove(receiver.hashCode())
        }
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
