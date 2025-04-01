package com.dubhe.hyperlightmaster

import android.app.Application
import android.content.IntentFilter
import android.service.quicksettings.Tile
import androidx.lifecycle.ViewModelProvider
import com.dubhe.hyperlightmaster.receiver.ScreenOnReceiver
import com.dubhe.hyperlightmaster.util.DataUtil
import com.dubhe.hyperlightmaster.viewmodel.LightViewModel
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.flow.MutableSharedFlow

sealed class ViewModelEvent {
    object ReadBrightness : ViewModelEvent()
    data class WriteBrightness(val brightness: Int) : ViewModelEvent()
    data class NotifyOn(val on: Boolean) : ViewModelEvent()
    data class AutoBrightness(val on: Boolean) : ViewModelEvent()
}

class LightApplication : Application() {

    lateinit var lightViewModel: LightViewModel//全局数据viewModel
    var nowActiveTile: Tile? = null//当前亮起的磁贴，其他磁贴点亮时要调用这个对象来熄灭磁贴

    // 用 SharedFlow 来作为事件总线
    val viewModelEventFlow = MutableSharedFlow<ViewModelEvent>()
    var isInit = false//标记 是否已完成viewModel初始化

    private val screenOnReceiver by lazy { ScreenOnReceiver() }//亮屏监听广播
    var isRegister = false//标记 是否已经添加亮屏监听广播

    companion object {
        lateinit var instance: LightApplication
        const val TAG = "Hyper亮度大师"
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        MMKV.initialize(this)
        try {
            lightViewModel = ViewModelProvider.AndroidViewModelFactory(instance).create(LightViewModel::class.java)
            isInit = true
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //通知亮度条，还没做完
//        if (DataUtil.getNotificationBarSwitch()) {
//            viewModelEventFlow.tryEmit(ViewModelEvent.NotifyOn(true))
//        }

        //开启监听亮屏广播
        if (DataUtil.getLockBrightnessMode() == DataUtil.MMKVValue.LOCK_BRIGHTNESS_RECEIVER) {
            registerScreenOnReceiver()
        }
    }

    //开启监听亮屏广播
    fun registerScreenOnReceiver() {
        if (!isRegister) {
            registerReceiver(screenOnReceiver, IntentFilter(ScreenOnReceiver.ACTION_SCREEN_ON))
            isRegister = true
        }
    }

    //取消监听亮屏广播
    fun unregisterScreenOnReceiver() {
        if (isRegister) {
            unregisterReceiver(screenOnReceiver)
        }
    }

    /**
     * 关闭多余的磁贴
     */
    fun closeTile() {
        nowActiveTile?.let {
            it.state = Tile.STATE_INACTIVE
            it.updateTile()
        }
    }

}