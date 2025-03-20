package com.dubhe.hyperlightmaster.base

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel

abstract class BaseVMActivity<VM : ViewModel, DB : ViewDataBinding> : BaseActivity<DB>() {

    abstract val mViewModel: VM
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerObserver()
    }

    abstract fun registerObserver()
}