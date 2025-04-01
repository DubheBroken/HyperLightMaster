package com.dubhe.hyperlightmaster.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dubhe.hyperlightmaster.LightApplication
import com.dubhe.hyperlightmaster.ViewModelEvent
import com.dubhe.hyperlightmaster.util.DataUtil
import com.dubhe.hyperlightmaster.util.SystemVersionUtil
import com.dubhe.hyperlightmaster.util.cancelNotification
import com.dubhe.hyperlightmaster.util.runShellCommand
import com.dubhe.hyperlightmaster.util.showNotification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LightViewModel : ViewModel() {

    //    var brightness = MutableLiveData<Int>()
//    val MAX_BRIGHTNESS by lazy { readMaxBrightness() }//最大亮度，从系统读
//    var maxBrightnessValueFromLogic = MutableLiveData(MAX_BRIGHTNESS) //亮度条的最大亮度上限，用户定义
//    var MIN_BRIGHTNESS = 10//最小亮度，降到0会完全黑掉
//    var minBrightnessValueFromLogic = MutableLiveData(MIN_BRIGHTNESS)//亮度条的最小亮度下限，用户定义
//    var autoBrightness = MutableLiveData(false)//自动亮度开关
    var notifyOn = MutableLiveData(false)//通知栏开关

    val deviceState by lazy {
        val dir = SystemVersionUtil.getLightDeviceDirByModel()
        DeviceState(MAX_BRIGHTNESS = readMaxBrightness(dir), deviceDir = dir)
    }

    init {
        var max = DataUtil.getMaxBrightnessValueFromLogic()
        if (max < 0) {
            max = deviceState.MAX_BRIGHTNESS
        }
        deviceState.maxBrightnessValueFromLogic.postValue(max)

        var min = DataUtil.getMinBrightnessValueFromLogic()
        if (min < deviceState.MIN_BRIGHTNESS) {
            min = deviceState.MIN_BRIGHTNESS
        }
        deviceState.minBrightnessValueFromLogic.postValue(min)
        notifyOn.postValue(DataUtil.getNotificationBarSwitch())

        deviceState.brightness.postValue(readBrightness())

        val app = LightApplication.instance
        viewModelScope.launch {
            app.viewModelEventFlow.collect { event ->
                when (event) {
                    is ViewModelEvent.NotifyOn -> {
                        withContext(Dispatchers.IO) {
                            notifyOn.postValue(event.on)
                            //开/关 通知
                            if (event.on) {
                                //开启通知
                                showNotification()
                            } else {
                                //关闭通知
                                cancelNotification()
                            }
                        }
                    }

                    is ViewModelEvent.AutoBrightness -> {
                        withContext(Dispatchers.IO) {
                            //开/关 自动亮度

                        }
                    }

                    is ViewModelEvent.ReadBrightness -> {
                        withContext(Dispatchers.IO) {
                            //读取当前亮度
                            deviceState.brightness.postValue(readBrightness())
                        }
                    }

                    is ViewModelEvent.WriteBrightness -> {
                        withContext(Dispatchers.IO) {
                            //调整亮度
                            writeBrightness(event.brightness)
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取系统最大亮度
     */
    private fun readMaxBrightness(dir: String? = null): Int {
        val output = runShellCommand("cat ${dir ?: deviceState.deviceDir}max_brightness")
        return output.toIntOrNull() ?: 0
    }

    /**
     * 获取当前亮度
     */
    fun readBrightness(): Int {
        val output = runShellCommand("cat ${deviceState.deviceDir}brightness")
        return output.toIntOrNull() ?: 0
    }

    /**
     * 设置亮度
     */
    fun writeBrightness(value: Int) {
        setWriteable()
        runShellCommand("echo $value > ${getBrightnessPath()}")

        if (DataUtil.getLockBrightnessMode() == DataUtil.MMKVValue.LOCK_BRIGHTNESS_READ_ONLY) {//设为只读
            setReadOnly()
        }
    }

    private fun getBrightnessPath(): String {
        return "${deviceState.deviceDir}brightness"
    }

    /**
     * 把文件标记为只读来锁定亮度
     */
    fun setReadOnly(){
        runShellCommand("chmod 444 ${getBrightnessPath()}")
    }

    /**
     * 文件标记为允许写入
     */
    fun setWriteable(){
        runShellCommand("chmod 644 ${getBrightnessPath()}")
    }

    /**
     * 检查亮度范围是否合法
     */
    fun checkBrightness(brightness: Int): Int {
        var br = brightness
        if (deviceState.maxBrightnessValueFromLogic.value != null && br > deviceState.maxBrightnessValueFromLogic.value!!) {
            br = deviceState.maxBrightnessValueFromLogic.value!!
        } else if (deviceState.minBrightnessValueFromLogic.value != null && br < deviceState.minBrightnessValueFromLogic.value!!) {
            br = deviceState.minBrightnessValueFromLogic.value!!
        }
        return br
    }

}