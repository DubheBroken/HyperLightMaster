package com.dubhe.hyperlightmaster.util

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.content.Intent
import com.dubhe.hyperlightmaster.LightApplication
import android.app.PendingIntent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import android.widget.RemoteViews
import androidx.core.content.ContextCompat.getSystemService
import androidx.preference.SwitchPreference
import androidx.recyclerview.widget.RecyclerView
import com.dubhe.hyperlightmaster.receiver.NotificationActionReceiver
import com.dubhe.hyperlightmaster.R

private val NOTIFICATION_CHANNEL_ID = "brightness_channel"
private val NOTIFICATION_ID = 1001

@SuppressLint("NotificationPermission")
fun showNotification() {
    // 创建通知通道（适用于 Android O 及以上）
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "亮度调节",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "用于亮度调节的通知通道"
        }
        val notificationManager = LightApplication.instance.getSystemService(NotificationManager::class.java)
        notificationManager?.createNotificationChannel(channel)
    }

    // 创建点击事件 PendingIntent（点击进度条）
    val progressIntent = Intent(LightApplication.instance, NotificationActionReceiver::class.java).apply {
        action = "ACTION_PROGRESS_CLICK"
    }
    val progressPendingIntent = PendingIntent.getBroadcast(
        LightApplication.instance, 0, progressIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // 创建点击事件 PendingIntent（点击按钮）
    val buttonIntent = Intent(LightApplication.instance, NotificationActionReceiver::class.java).apply {
        action = "ACTION_BUTTON_CLICK"
    }
    val buttonPendingIntent = PendingIntent.getBroadcast(
        LightApplication.instance, 1, buttonIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // 使用自定义布局构建通知内容
    val remoteViews = RemoteViews(LightApplication.instance.packageName, R.layout.custom_notification).apply {
        setProgressBar(R.id.progressBar,
                       LightApplication.instance.lightViewModel.deviceState.maxBrightnessValueFromLogic.value!!,//最大值
                       LightApplication.instance.lightViewModel.deviceState.brightness.value?:10,//当前值
                       false)//禁止滑动
        // 为进度条设置点击事件
        setOnClickPendingIntent(R.id.progressBar, progressPendingIntent)
        // 为按钮设置点击事件
        setOnClickPendingIntent(R.id.buttonBrightness, buttonPendingIntent)
    }

    // 构建通知（常驻、优先级高）
    val notificationBuilder = NotificationCompat.Builder(LightApplication.instance, NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(R.drawable.icon_light)//通知图标
        .setCustomContentView(remoteViews)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)//优先级
        .setOngoing(true)
        .setOnlyAlertOnce(true)
        .setAutoCancel(false)

    // 发送通知
    val notificationManagerCompat = NotificationManagerCompat.from(LightApplication.instance)
    notificationManagerCompat.notify(NOTIFICATION_ID, notificationBuilder.build())
}

@SuppressLint("NotificationPermission")
fun cancelNotification() {
    // 获取通知管理器
    val notificationManager = LightApplication.instance.getSystemService(NotificationManager::class.java)

    // 取消通知
    notificationManager?.cancel(NOTIFICATION_ID)
}
