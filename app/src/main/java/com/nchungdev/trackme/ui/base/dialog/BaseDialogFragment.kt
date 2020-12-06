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
import com.nchungdev.trackme.R

open class BaseDialogFragment : AppCompatDialogFragment() {
    private var onClickListener: (Event) -> Unit = {}
    private var onDismissListener: DialogInterface.OnDismissListener? = null
    private var onCancelListener: DialogInterface.OnCancelListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title = getStringFromArgument("title", "titleResId")
        val message = getStringFromArgument("message", "messageResId")
        val positive = getStringFromArgument("positive", "positiveResId")
        val negative = getStringFromArgument("negative", "negativeResId")
        val layout = LayoutInflater.from(context).inflate(R.layout.dialog_fragment, null)
        setText(layout.findViewById(R.id.tv_title), title)
        setText(layout.findViewById(R.id.tv_message), message)
        layout.findViewById<TextView>(R.id.btn_positive).apply {
            setText(this, positive)
            setOnClickListener {
                onClickListener(Event.POSITIVE)
                dismiss()
            }
        }
        layout.findViewById<TextView>(R.id.btn_negative).apply {
            setText(this, negative)
            setOnClickListener {
                onClickListener(Event.NEGATIVE)
                dismiss()
            }
        }
        return AppCompatDialog(requireActivity()).apply {
            supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(layout)
        }
    }

    private fun setText(textView: TextView, text: CharSequence?) {
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

    class Builder {
        var title: CharSequence? = null
        var message: CharSequence? = null
        var positiveButton: CharSequence? = null
        var negativeButton: CharSequence? = null
        var titleResId: Int? = null
        var messageResId: Int? = null
        var positiveButtonResId: Int? = null
        var negativeButtonResId: Int? = null

        var onClick: (Event) -> Unit = {}
        var onDismiss: DialogInterface.OnDismissListener? = null
        var onCancel: DialogInterface.OnCancelListener? = null
        var cancelable: Boolean = true

        fun create() = newInstance(
            bundleOf(
                "title" to title,
                "message" to message,
                "positive" to positiveButton,
                "negative" to negativeButton,
                "titleResId" to titleResId,
                "messageResId" to messageResId,
                "positiveResId" to positiveButtonResId,
                "negativeResId" to negativeButtonResId,
            )
        )
            .apply {
                onClickListener = onClick
                onCancelListener = onCancel
                onDismissListener = onDismiss
                isCancelable = cancelable
            }

        fun show(fragmentManager: FragmentManager) {
            create().show(fragmentManager, null)
        }
    }

    enum class Event {
        POSITIVE, NEGATIVE
    }

    companion object {
        protected fun newInstance(bundle: Bundle) = BaseDialogFragment().apply {
            arguments = bundle
        }
    }
}