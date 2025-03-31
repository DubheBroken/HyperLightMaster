package com.dubhe.hyperlightmaster.dialog

import android.app.ActionBar
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Html
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import com.dubhe.hyperlightmaster.R
import com.dubhe.hyperlightmaster.databinding.OkDialogBinding

/**
 * 只有确定的Dialog
 */
class OkDialog(var mContext: Context) : Dialog(mContext, R.style.centerDialog) {
    companion object {
        private var tab = 0
    }

    var onOkClickListener: OnOkClickListener? = null

    private var colorText: String = ""

    interface OnOkClickListener {
        fun onOkClick()
    }

    private val binding: OkDialogBinding =
        OkDialogBinding.inflate(LayoutInflater.from(context))

    init {
        setContentView(binding.root)
        val window = this.window
        window!!.setBackgroundDrawable(ColorDrawable(0))
        window.setGravity(Gravity.CENTER)
        val lp = window.attributes
        //设置宽
        lp.width = context.resources.getDimensionPixelOffset(R.dimen.dp_320)
        //设置高
        lp.height = ActionBar.LayoutParams.WRAP_CONTENT
        window.attributes = lp
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        initView()
        setCanceledOnTouchOutside(false)
    }

    fun setShowCloseBtn(isShow: Boolean) {
        if (isShow) {
            binding.textbtnOk.visibility = View.VISIBLE
        } else {
            binding.textbtnOk.visibility = View.INVISIBLE
        }
    }

    fun setTitle(title: String) {
        binding.textTag.text = title
    }

    fun setInfo(info: String) {
        binding.textInfo.text = info
    }

    fun setInfoHtml(info: String) {
        binding.textInfo.text = Html.fromHtml(info)
    }

    fun setOkText(text: String) {
        binding.textbtnOk.text = text
    }

    private fun initView() {
        binding.textbtnOk.setOnClickListener {
            dismiss()
            onOkClickListener?.onOkClick()
        }
    }

}
