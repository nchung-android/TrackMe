package com.nchungdev.trackme.ui.base.dialog

import android.view.LayoutInflater
import com.nchungdev.trackme.databinding.DialogConfirmationBinding

class ConfirmationDialogFragment : BaseDialogFragment<DialogConfirmationBinding>() {

    override fun initViewBinding(inflater: LayoutInflater) = DialogConfirmationBinding.inflate(inflater)

    override fun onBindView(binding: DialogConfirmationBinding) {
        setText(binding.tvTitle, getTitle())
        setText(binding.tvMessage, getMessage())
        binding.btnPositive.apply {
            setText(this, getPositive())
            setOnClickListener {
                onClickListener(POSITIVE)
                dismiss()
            }
        }
        binding.btnNegative.apply {
            setText(this, getNegative())
            setOnClickListener {
                onClickListener(NEGATIVE)
                dismiss()
            }
        }
    }

    companion object {
        fun newInstance(builder: Builder) = ConfirmationDialogFragment().apply {
            arguments = builder.toBundle()
            onClickListener = builder.onClick
            onCancelListener = builder.onCancel
            onDismissListener = builder.onDismiss
            isCancelable = builder.cancelable
        }
    }
}