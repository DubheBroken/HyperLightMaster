package com.dubhe.hyperlightmaster

import android.app.Application
import android.service.quicksettings.Tile
import androidx.lifecycle.ViewModelProvider
import com.dubhe.hyperlightmaster.dialog.OkDialog
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

    lateinit var lightViewModel: LightViewModel
    var nowActiveTile: Tile? = null

    // 用 SharedFlow 来作为事件总线
    val viewModelEventFlow = MutableSharedFlow<ViewModelEvent>()
    var isInit = false

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

        if (DataUtil.getNotificationBarSwitch()) {
            viewModelEventFlow.tryEmit(ViewModelEvent.NotifyOn(true))
        }
    }

    /**
     * 关闭多余的图块
     */
    fun closeTile() {
        nowActiveTile?.let {
            it.state = Tile.STATE_INACTIVE
            it.updateTile()
        }
    }

}