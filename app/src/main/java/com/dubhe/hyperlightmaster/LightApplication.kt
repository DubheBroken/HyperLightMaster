package com.dubhe.hyperlightmaster

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import com.dubhe.hyperlightmaster.util.DataUtil
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.flow.MutableSharedFlow

sealed class ViewModelEvent {
    object ReadBrightness : ViewModelEvent()
    data class WriteBrightness(val brightness: Int) : ViewModelEvent()
    data class NotifyOn(val on: Boolean) : ViewModelEvent()
    data class AutoBrightness(val on: Boolean) : ViewModelEvent()
}

class LightApplication: Application() {

    lateinit var lightViewModel: LightViewModel
    // 用 SharedFlow 来作为事件总线
    val viewModelEventFlow = MutableSharedFlow<ViewModelEvent>()

    companion object {
        lateinit var instance: LightApplication
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        MMKV.initialize(this)
        lightViewModel = ViewModelProvider.AndroidViewModelFactory(this).create(LightViewModel::class.java)

        if (DataUtil.getNotificationBarSwitch()) {
            viewModelEventFlow.tryEmit(ViewModelEvent.NotifyOn(true))
        }
    }

}