package com.dubhe.hyperlightmaster.service

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.dubhe.hyperlightmaster.LightApplication
import com.dubhe.hyperlightmaster.ViewModelEvent
import com.dubhe.hyperlightmaster.util.DataUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ThreeQuarterTileService: TileService() {

    private var beforeBrightness = -1

    override fun onClick() {
        // 处理磁贴的点击事件
        when (qsTile.state) {
            Tile.STATE_INACTIVE -> {
                LightApplication.instance.closeTile()
                qsTile.state = Tile.STATE_ACTIVE// 更改成活跃状态
                LightApplication.instance.nowActiveTile = qsTile
                //开启 3/4 亮度
                beforeBrightness = LightApplication.instance.lightViewModel.deviceState.brightness.value ?:-1 //记录当前亮度
                CoroutineScope(Dispatchers.Default).launch {
                    var brightness = LightApplication.instance.lightViewModel.deviceState.MAX_BRIGHTNESS
                    if (DataUtil.getMinMaxSetupQuickTile() && LightApplication.instance.lightViewModel.deviceState.maxBrightnessValueFromLogic.value != null) {
                        brightness = LightApplication.instance.lightViewModel.deviceState.maxBrightnessValueFromLogic.value!!
                    }
                    brightness = (brightness * 0.75).toInt()
                    brightness = LightApplication.instance.lightViewModel.checkBrightness(brightness)
                    LightApplication.instance.viewModelEventFlow.emit(ViewModelEvent.WriteBrightness(brightness))
                }
            }
            Tile.STATE_ACTIVE -> {
                //                icon = Icon.createWithResource(applicationContext, R.drawable.logo_wechat)
                qsTile.state = Tile.STATE_INACTIVE//更改成非活跃状态
                //恢复之前的亮度
                if (beforeBrightness >= 10) {
                    LightApplication.instance.viewModelEventFlow.tryEmit(ViewModelEvent.WriteBrightness(beforeBrightness))
                    CoroutineScope(Dispatchers.Default).launch {
                        LightApplication.instance.viewModelEventFlow.emit(ViewModelEvent.WriteBrightness(beforeBrightness))
                    }
                }
            }
            Tile.STATE_UNAVAILABLE -> {
                //                icon = Icon.createWithResource(applicationContext, R.drawable.ic_noti_action_cancel)
            }
        }
        qsTile.updateTile()//更新Tile
    }

}