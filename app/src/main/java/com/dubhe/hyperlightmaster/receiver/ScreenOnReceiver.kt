package com.dubhe.hyperlightmaster.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.dubhe.hyperlightmaster.LightApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ScreenOnReceiver : BroadcastReceiver() {

    companion object {
        val ACTION_SCREEN_ON = Intent.ACTION_SCREEN_ON
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            ACTION_SCREEN_ON -> {
                //屏幕亮起
                Log.d(LightApplication.TAG, "侦测到屏幕亮起")
                CoroutineScope(Dispatchers.IO).launch {
                    delay(500)//延迟半秒等亮屏动画
                    val readBrightness = LightApplication.instance.lightViewModel.readBrightness()//现在实际的亮度值
                    val targetBrightness = LightApplication.instance.lightViewModel.deviceState.brightness//用户最后一次设置的亮度值
                    if (targetBrightness.value != null && readBrightness != targetBrightness.value) {//两个亮度不一致，说明被系统改了
                        targetBrightness.value?.let {
                            LightApplication.instance.lightViewModel.writeBrightness(it)//再写入一次亮度值
                        }
                    }
                }
            }
        }
    }

}