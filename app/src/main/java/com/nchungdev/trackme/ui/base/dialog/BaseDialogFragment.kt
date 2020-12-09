package com.nchungdev.trackme.ui.base.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding

abstract class BaseDialogFragment<B : ViewBinding> : AppCompatDialogFragment() {
    var onClickListener: (Int) -> Unit = {}
    var onDismissListener: DialogInterface.OnDismissListener? = null
    var onCancelListener: DialogInterface.OnCancelListener? = null

    abstract fun initViewBinding(inflater: LayoutInflater): B

    abstract fun onBindView(binding: B)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = initViewBinding(LayoutInflater.from(requireContext()))
        onBindView(binding)
        return AppCompatDialog(requireActivity()).apply {
            supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(binding.root)
        }
    }

    protected fun setText(textView: TextView, text: CharSequence?) {
        textView.text = text ?: return run { textView.isVisible = false }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        onDismissListener?.onDismiss(dialog)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener?.onDismiss(dialog)
    }

    private fun getStringFromArgument(stringKey: String, resIdKey: String): String? {
        val arguments = arguments ?: return null
        return when {
            arguments.getString(stringKey) != null -> arguments.getString(stringKey)
            arguments.getInt(resIdKey, 0) != 0 -> resources.getString(arguments.getInt(resIdKey))
            else -> null
        }
    }

    fun show(manager: FragmentManager) {
        super.show(manager, null)
    }

    protected fun getTitle() = getStringFromArgument(EXTRA_TITLE, EXTRA_TITLE_RES_ID)

    protected fun getMessage() = getStringFromArgument(EXTRA_MESSAGE, EXTRA_MESSAGE_RES_ID)

    protected fun getPositive() = getStringFromArgument(EXTRA_POSITIVE, EXTRA_POSITIVE_RES_ID)

    protected fun getNegative() = getStringFromArgument(EXTRA_NEGATIVE, EXTRA_NEGATIVE_RES_ID)

    class Builder {
        var title: CharSequence? = null
        var message: CharSequence? = null
        var positiveButton: CharSequence? = null
        var negativeButton: CharSequence? = null
        var titleResId: Int? = null
        var messageResId: Int? = null
        var positiveButtonResId: Int? = null
        var negativeButtonResId: Int? = null

        var onClick: (Int) -> Unit = {}
        var onDismiss: DialogInterface.OnDismissListener? = null
        var onCancel: DialogInterface.OnCancelListener? = null
        var cancelable: Boolean = true

        fun toBundle() = bundleOf(
            EXTRA_TITLE to title,
            EXTRA_MESSAGE to message,
            EXTRA_POSITIVE to positiveButton,
            EXTRA_NEGATIVE to negativeButton,
            EXTRA_TITLE_RES_ID to titleResId,
            EXTRA_MESSAGE_RES_ID to messageResId,
            EXTRA_POSITIVE_RES_ID to positiveButtonResId,
            EXTRA_NEGATIVE_RES_ID to negativeButtonResId,
        )
    }

    companion object {
        const val POSITIVE = 1
        const val NEGATIVE = 0
        const val EXTRA_TITLE = "xTitle"
        const val EXTRA_TITLE_RES_ID = "xTitleResId"
        const val EXTRA_MESSAGE = "xMessage"
        const val EXTRA_MESSAGE_RES_ID = "xMessageResId"
        const val EXTRA_POSITIVE = "xPositive"
        const val EXTRA_POSITIVE_RES_ID = "xPositiveResId"
        const val EXTRA_NEGATIVE = "xNegative"
        const val EXTRA_NEGATIVE_RES_ID = "xNegativeResId"
    }
}