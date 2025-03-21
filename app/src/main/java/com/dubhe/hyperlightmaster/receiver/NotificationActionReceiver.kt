package com.dubhe.hyperlightmaster.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.dubhe.hyperlightmaster.LightApplication
import com.dubhe.hyperlightmaster.ViewModelEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationActionReceiver : BroadcastReceiver() {

    companion object {
        val ACTION_NOTIFY_ON = "ACTION_NOTIFY_ON"
        val ACTION_AUTO_BRIGHTNESS = "ACTION_AUTO_BRIGHTNESS"
        val ACTION_READ_BRIGHTNESS = "ACTION_READ_BRIGHTNESS"
        val ACTION_WRITE_BRIGHTNESS = "ACTION_WRITE_BRIGHTNESS"
        val autoLightOn = "autoLightOn"
        val notifyOn = "notifyOn"
        val brightness = "brightness"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            ACTION_NOTIFY_ON -> {
                //开/关 通知
                val notifyOn = intent.getBooleanExtra(notifyOn, false)
                CoroutineScope(Dispatchers.Default).launch {
                    LightApplication.instance.viewModelEventFlow.emit(ViewModelEvent.NotifyOn(notifyOn))
                }
            }
            ACTION_AUTO_BRIGHTNESS -> {
                //开/关 自动亮度
                val autoLightOn = intent.getBooleanExtra(autoLightOn, false)
                CoroutineScope(Dispatchers.Default).launch {
                    LightApplication.instance.viewModelEventFlow.emit(ViewModelEvent.NotifyOn(autoLightOn))
                }
            }
            ACTION_READ_BRIGHTNESS -> {
                //读取当前亮度
                CoroutineScope(Dispatchers.Default).launch {
                    LightApplication.instance.viewModelEventFlow.emit(ViewModelEvent.ReadBrightness)
                }
            }
            ACTION_WRITE_BRIGHTNESS -> {
                //调整亮度
                val brightness = intent.getIntExtra(brightness, -1)
                if (brightness != -1) {
                    CoroutineScope(Dispatchers.Default).launch {
                        LightApplication.instance.viewModelEventFlow.emit(ViewModelEvent.WriteBrightness(brightness))
                    }
                }
            }
        }
    }


}
