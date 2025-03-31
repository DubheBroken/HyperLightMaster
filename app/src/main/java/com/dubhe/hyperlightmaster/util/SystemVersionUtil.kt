package com.dubhe.hyperlightmaster.util

import android.util.Log
import com.dubhe.hyperlightmaster.LightApplication
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * 执行shell命令
 * @param command 要执行的命令
 *
 * @return 执行结果
 */
fun runShellCommand(command: String): String {
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

object SystemVersionUtil {

    /**
     * 根据设备型号返回亮度设备路径
     */
    fun getLightDeviceDirByModel(): String {
        when (readBrightness().trim().uppercase()) {
            Mi12s, MixFold2 -> {
                return "/sys/class/backlight/panel0-backlight/"
            }

            NOTE13_5G -> {
                return "/sys/class/leds/lcd-backlight/"
            }

            else -> {
                return "/sys/class/backlight/panel0-backlight/"
            }
        }
    }

    /**
     * 获取设备型号
     */
    private fun readBrightness(): String {
        val output = runShellCommand("getprop ro.product.model")
        Log.d(LightApplication.TAG, "获取设备型号: $output")
        return output
    }

    /**
     * /sys/class/backlight/panel0-backlight
     */
    const val Mi11u = ""
    const val Mi12s = "2206123SC"
    const val MixFold2 = "22061218C"
    const val K50u_Mi12TPro = ""

    /**
     * /sys/class/leds/lcd-backlight
     */
    const val NOTE13_5G = "2312DRAABC"

}
