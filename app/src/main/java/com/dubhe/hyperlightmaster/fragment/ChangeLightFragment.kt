package com.dubhe.hyperlightmaster.fragment

import android.annotation.SuppressLint
import android.view.GestureDetector
import android.view.MotionEvent
import com.dubhe.hyperlightmaster.LightApplication
import com.dubhe.hyperlightmaster.R
import com.dubhe.hyperlightmaster.base.BaseFragment
import com.dubhe.hyperlightmaster.databinding.FragmentChangeLightBinding

class ChangeLightFragment : BaseFragment<FragmentChangeLightBinding>() {

    private val viewModel by lazy { LightApplication.instance.lightViewModel }

    private val gestureDetector by lazy {
        GestureDetector(requireContext(), object : GestureDetector.SimpleOnGestureListener() {
            override fun onScroll(e1: MotionEvent?,
                                  e2: MotionEvent,
                                  distanceX: Float,
                                  distanceY: Float): Boolean {
                adjustProgress(distanceY)
                return true
            }
        })
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_change_light
    }

    @SuppressLint("NewApi", "ClickableViewAccessibility", "SetTextI18n")
    override fun initView() {
        if (!LightApplication.instance.isInit) {
            return
        }
        viewModel.deviceState.brightness.observe(this) {
            dataBinding.textLight.text = "当前亮度: $it"
            dataBinding.progressBar.progress = it
        }

        viewModel.deviceState.maxBrightnessValueFromLogic.observe(this){
            dataBinding.progressBar.max = it
            dataBinding.textLightMax.text = "最大亮度: ${LightApplication.instance.lightViewModel.deviceState.maxBrightnessValueFromLogic.value}"
            viewModel.deviceState.brightness.value?.let {
                dataBinding.progressBar.progress = it// 刷新一下进度条渲染
            }
        }
        viewModel.deviceState.minBrightnessValueFromLogic.observe(this){
            dataBinding.progressBar.min = it
            viewModel.deviceState.brightness.value?.let {
                dataBinding.progressBar.progress = it// 刷新一下进度条渲染
            }
        }

        dataBinding.constraintRoot.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }
    }

    // 根据滑动距离调整进度
    private fun adjustProgress(distanceY: Float) {
        val currentProgress = dataBinding.progressBar.progress
        val maxProgress = dataBinding.progressBar.max

        // 上滑 distanceY 为负数，下滑为正数，所以需要反向
        val progressChange = (0 - distanceY).toInt() * 2 // 根据需要调整滑动速度
        var newProgress = (currentProgress - progressChange).coerceIn(viewModel.deviceState.MIN_BRIGHTNESS, maxProgress)

        newProgress = viewModel.checkBrightness(newProgress)

        dataBinding.progressBar.progress = newProgress
        viewModel.writeBrightness(newProgress)
        viewModel.deviceState.brightness.postValue(newProgress)
    }

}