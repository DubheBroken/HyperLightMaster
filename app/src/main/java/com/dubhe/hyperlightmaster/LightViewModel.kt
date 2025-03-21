package com.dubhe.hyperlightmaster

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dubhe.hyperlightmaster.util.DataUtil
import com.dubhe.hyperlightmaster.util.cancelNotification
import com.dubhe.hyperlightmaster.util.showNotification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

class LightViewModel : ViewModel() {

    var brightness = MutableLiveData<Int>()
    val MAX_BRIGHTNESS by lazy { readMaxBrightness() }//最大亮度，从系统读
    var maxBrightnessValueFromLogic = MutableLiveData(MAX_BRIGHTNESS) //亮度条的最大亮度上限，用户定义
    var MIN_BRIGHTNESS = 10//最小亮度，降到0会完全黑掉
    var minBrightnessValueFromLogic = MutableLiveData(MIN_BRIGHTNESS)//亮度条的最小亮度下限，用户定义
    var autoBrightness = MutableLiveData(false)//自动亮度开关
    var notifyOn = MutableLiveData(false)//通知栏开关

    init {
        var max = DataUtil.getMaxBrightnessValueFromLogic()
        if (max < 0) {
            max = MAX_BRIGHTNESS
        }
        maxBrightnessValueFromLogic.postValue(max)

        var min = DataUtil.getMinBrightnessValueFromLogic()
        if (min < MIN_BRIGHTNESS) {
            min = MIN_BRIGHTNESS
        }
        minBrightnessValueFromLogic.postValue(min)
        notifyOn.postValue(DataUtil.getNotificationBarSwitch())

        brightness.postValue(readBrightness())

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
                            brightness.postValue(readBrightness())
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
    private fun readMaxBrightness(): Int {
        val output =
            runShellCommand("cat /sys/class/backlight/panel0-backlight/max_brightness")
        return output.toIntOrNull() ?: 0
    }

    /**
     * 获取当前亮度
     */
    fun readBrightness(): Int {
        val output =
            runShellCommand("cat /sys/class/backlight/panel0-backlight/brightness")
        return output.toIntOrNull() ?: 0
    }

    /**
     * 设置亮度
     */
    fun writeBrightness(value: Int) {
        runShellCommand("echo $value > /sys/class/backlight/panel0-backlight/brightness")
    }

    private fun runShellCommand(command: String): String {
        val process = Runtime.getRuntime().exec(arrayOf("su", "-c", command))
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        val result = StringBuilder()
        var line: String?

        while (reader.readLine().also { line = it } != null) {
            result.append(line).append("\n")
        }

        reader.close()
        process.waitFor()
        return result.toString().trim()
    }

}