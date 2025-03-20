package com.dubhe.hyperlightmaster

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.BufferedReader
import java.io.InputStreamReader

class LightViewModel: ViewModel() {

    var brightness = MutableLiveData<Int>()
    val MAX_BRIGHTNESS by lazy { readMaxBrightness() }//最大亮度，从系统读
    val MIN_BRIGHTNESS = 10//最小亮度，降到0会完全黑掉

    fun initData() {
        brightness.postValue(readBrightness())
    }

    private fun readMaxBrightness(): Int {
        val output = runShellCommand("cat /sys/class/backlight/panel0-backlight/max_brightness")
        return output.toIntOrNull() ?: 0
    }


    fun readBrightness(): Int {
        val output = runShellCommand("cat /sys/class/backlight/panel0-backlight/brightness")
        return output.toIntOrNull() ?: 0
    }

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