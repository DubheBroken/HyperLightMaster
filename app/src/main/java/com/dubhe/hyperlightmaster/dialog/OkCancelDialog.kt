package com.dubhe.hyperlightmaster.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import com.dubhe.hyperlightmaster.R
import com.dubhe.hyperlightmaster.databinding.OkCancelDialogBinding

/**
 * 确定取消Dialog
 */
class OkCancelDialog(var mContext: Context, var cancelAble: Boolean = true) :
    Dialog(mContext, R.style.centerDialog) {

    var onOkClickListener: OnOkClickListener? = null
    var onCancelClickListener: OnCancelClickListener? = null

    interface OnOkClickListener {
        fun onOkClick()
    }

    interface OnCancelClickListener {
        fun onCancelClick()
    }

    private val binding: OkCancelDialogBinding = OkCancelDialogBinding.inflate(LayoutInflater.from(context))

    init {
        setContentView(binding.root)
        val window = this.window
        window!!.setBackgroundDrawable(ColorDrawable(0))
        window.setGravity(Gravity.CENTER)
        val lp = window.attributes
        //设置宽
        lp.width = context.resources.getDimensionPixelOffset(R.dimen.width_ok_cancel_dialog)
        //设置高
        lp.height = context.resources.getDimensionPixelOffset(R.dimen.height_ok_cancel_dialog)
        window.attributes = lp
        window.setBackgroundDrawable(ColorDrawable(0))
        initView()
        setCanceledOnTouchOutside(cancelAble)
        setCancelable(cancelAble)
    }

    fun setCancleText(text: String) {
        binding.textbtnCancel.text = text
    }

    fun setTitle(title: String) {
        binding.textTag.text = title
    }

    fun setInfo(info: String) {
        binding.textInfo.text = info
    }

    fun setOkText(text: String) {
        binding.textbtnOk.text = text
    }

    private fun initView() {
        binding.textbtnCancel.setOnClickListener {
            dismiss()
            onCancelClickListener?.onCancelClick()
        }
        binding.textbtnOk.setOnClickListener {
            onOkClickListener?.onOkClick()
            dismiss()
        }
    }

}
