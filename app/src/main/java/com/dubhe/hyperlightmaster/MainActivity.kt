package com.dubhe.hyperlightmaster

import android.annotation.SuppressLint
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : BaseActivity() {

    var brightness = MutableLiveData<Int>()
    val MAX_BRIGHTNESS by lazy { readMaxBrightness() }//最大亮度，从系统读
    val MIN_BRIGHTNESS = 10//最小亮度，降到0会完全黑掉

    val gestureDetector by lazy {
        GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onScroll(
                e1: MotionEvent?,
                e2: MotionEvent?,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                adjustProgress(distanceY)
                return true
            }
        })
    }

    override fun getLayout(): Int {
        return R.layout.activity_main
    }

    @SuppressLint("NewApi", "ClickableViewAccessibility", "SetTextI18n")
    override fun initView() {
        brightness.postValue(readBrightness())
        brightness.observe(this, Observer {
            textLight.text = "当前亮度: $it"
        })

        textLightMax.text = "最大亮度: $MAX_BRIGHTNESS"
        progressBar.max = MAX_BRIGHTNESS
        progressBar.min = MIN_BRIGHTNESS
        progressBar.progress = readBrightness()

        constraintRoot.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }
    }

    // 根据滑动距离调整进度
    private fun adjustProgress(distanceY: Float) {
        val currentProgress = progressBar.progress
        val maxProgress = progressBar.max

        // 上滑 distanceY 为负数，下滑为正数，所以需要反向
        val progressChange = (0 - distanceY).toInt() * 2 // 根据需要调整滑动速度
        val newProgress = (currentProgress - progressChange).coerceIn(MIN_BRIGHTNESS, maxProgress)

        progressBar.progress = newProgress
        writeBrightness(newProgress)
        brightness.postValue(newProgress)
    }

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

    fun readMaxBrightness(): Int {
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

    override fun initData() {
        
    }

}