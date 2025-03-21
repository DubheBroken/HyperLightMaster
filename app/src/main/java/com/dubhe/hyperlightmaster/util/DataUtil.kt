package com.dubhe.hyperlightmaster.util

import com.tencent.mmkv.MMKV

object DataUtil {

    class MMKVKey {
        companion object {
            const val NOTIFICATION_BAR_SWITCH = "NOTIFICATION_BAR_SWITCH"//通知栏开关
            const val QUICK_TILE_SWITCH = "QUICK_TILE_SWITCH"//快捷图块开关
            const val MAX_BRIGHTNESS_VALUE_FROM_LOGIC = "MAX_BRIGHTNESS_VALUE_FROM_LOGIC" //亮度条的最大亮度上限，用户定义
            const val MIN_BRIGHTNESS_VALUE_FROM_LOGIC = "MIN_BRIGHTNESS_VALUE_FROM_LOGIC" //亮度条的最小亮度下限，用户定义
            const val MIN_MAX_SETUP_QUICK_TILE = "MIN_MAX_SETUP_QUICK_TILE"//快捷图块最大最小值限制开关
        }
    }

    fun saveNotificationBarSwitch(value: Boolean) {
        saveBooleanData(MMKVKey.NOTIFICATION_BAR_SWITCH, value)
    }

    fun getNotificationBarSwitch(): Boolean {
        return getBooleanData(MMKVKey.NOTIFICATION_BAR_SWITCH, false)
    }

    fun saveQuickTileSwitch(value: Boolean) {
        saveBooleanData(MMKVKey.QUICK_TILE_SWITCH, value)
    }

    fun getQuickTileSwitch(): Boolean {
        return getBooleanData(MMKVKey.QUICK_TILE_SWITCH, false)
    }

    fun saveMaxBrightnessValueFromLogic(value: Int) {
        saveIntData(MMKVKey.MAX_BRIGHTNESS_VALUE_FROM_LOGIC, value)
    }

    fun getMaxBrightnessValueFromLogic(): Int {
        return getIntData(MMKVKey.MAX_BRIGHTNESS_VALUE_FROM_LOGIC, -1)
    }

    fun saveMinBrightnessValueFromLogic(value: Int) {
        saveIntData(MMKVKey.MIN_BRIGHTNESS_VALUE_FROM_LOGIC, value)
    }

    fun getMinBrightnessValueFromLogic(): Int {
        return getIntData(MMKVKey.MIN_BRIGHTNESS_VALUE_FROM_LOGIC, 10)
    }

    fun saveMinMaxSetupQuickTile(value: Boolean) {
        saveBooleanData(MMKVKey.MIN_MAX_SETUP_QUICK_TILE, value)
    }

    fun getMinMaxSetupQuickTile(): Boolean {
        return getBooleanData(MMKVKey.MIN_MAX_SETUP_QUICK_TILE, false)
    }

    private fun saveBooleanData(key: String, value: Boolean) {
        MMKV.defaultMMKV().encode(key, value)
    }

    private fun getBooleanData(key: String, defaultValue: Boolean): Boolean {
        return MMKV.defaultMMKV().decodeBool(key, defaultValue)
    }

    private fun saveIntData(key: String, value: Int) {
        MMKV.defaultMMKV().encode(key, value)
    }

    private fun getIntData(key: String, defaultValue: Int): Int {
        return MMKV.defaultMMKV().decodeInt(key, defaultValue)
    }

}