package com.dubhe.hyperlightmaster.viewmodel

import androidx.lifecycle.MutableLiveData

data class DeviceState(
    var path: String = "",                          // 设备路径，例如 "/sys/class/backlight/panel0-backlight"
    var brightness: MutableLiveData<Int> = MutableLiveData(), // 当前亮度
    val MAX_BRIGHTNESS: Int = -1,                    // 系统最大亮度
    val MIN_BRIGHTNESS: Int = 10,               // 最小亮度（避免完全黑屏）
    var maxBrightnessValueFromLogic: MutableLiveData<Int> = MutableLiveData(), // 用户定义的逻辑最大亮度
    var minBrightnessValueFromLogic: MutableLiveData<Int> = MutableLiveData(), // 用户定义的逻辑最小亮度
    var autoBrightness: MutableLiveData<Boolean> = MutableLiveData(false), // 自动亮度开关
    var notifyOn: MutableLiveData<Boolean> = MutableLiveData(false),        // 通知栏开关
    var isLit: MutableLiveData<Boolean> = MutableLiveData(false), // 是否点亮
    val deviceDir: String //亮度设备路径
)