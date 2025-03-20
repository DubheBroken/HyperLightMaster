package com.dubhe.hyperlightmaster.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

/**
 * 文件描述：基础的Fragment
 */
abstract class BaseFragment<DB : ViewDataBinding> : Fragment() {
    //是否是第一次渲染
    protected var isFirstRendering = true
    protected lateinit var dataBinding: DB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(
            inflater, getLayoutResId(), container, false
        )
        dataBinding.lifecycleOwner = this
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        isFirstRendering = false
    }

    abstract fun getLayoutResId(): Int

    abstract fun initView()

}