package com.dubhe.hyperlightmaster.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseActivity<DB : ViewDataBinding> : AppCompatActivity() {

    protected var isViewReady = false //view是否渲染完成

    protected lateinit var dataBinding: DB
    abstract val layout: Int
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, layout)
        dataBinding.lifecycleOwner = this
        //        dataBinding.root.findViewById<ConstraintLayout>(R.id.module_body).layoutParams.height += BarUtils.getStatusBarHeight()
        initView()
    }

    abstract fun initView()
    abstract fun clear()
    open fun onViewReady() {

    }

    override fun onDestroy() {
        clear()
        super.onDestroy()
    }
}