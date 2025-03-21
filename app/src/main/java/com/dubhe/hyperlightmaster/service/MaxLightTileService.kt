package com.dubhe.hyperlightmaster.service

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.dubhe.hyperlightmaster.LightApplication
import com.dubhe.hyperlightmaster.ViewModelEvent
import com.dubhe.hyperlightmaster.util.DataUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MaxLightTileService: TileService() {

    private var beforeBrightness = -1

    override fun onClick() {
        // 处理磁贴的点击事件
        when (qsTile.state) {
            Tile.STATE_INACTIVE -> {
                qsTile.state = Tile.STATE_ACTIVE// 更改成活跃状态
                //开启最大亮度
                beforeBrightness = LightApplication.instance.lightViewModel.brightness.value ?:-1 //记录当前亮度
                CoroutineScope(Dispatchers.Default).launch {
                    var brightness = LightApplication.instance.lightViewModel.MAX_BRIGHTNESS
                    if (DataUtil.getMinMaxSetupQuickTile() && LightApplication.instance.lightViewModel.maxBrightnessValueFromLogic.value != null) {
                        brightness = LightApplication.instance.lightViewModel.maxBrightnessValueFromLogic.value!!
                    }
                    LightApplication.instance.viewModelEventFlow.emit(ViewModelEvent.WriteBrightness(brightness))
                }
            }
            Tile.STATE_ACTIVE -> {
                //                icon = Icon.createWithResource(applicationContext, R.drawable.logo_wechat)
                qsTile.state = Tile.STATE_INACTIVE//更改成非活跃状态
                //恢复之前的亮度
                if (beforeBrightness >= 10) {
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