package com.dubhe.hyperlightmaster.util

import android.content.Context
import android.graphics.Color
import androidx.core.graphics.ColorUtils

object ThemeColorManager {

    fun getPrimaryColor(): Int {
        val saved = DataUtil.getThemeColor()
        if (saved != -1) return saved
        return -1
    }

    fun hasCustomColor(): Boolean = DataUtil.getThemeColor() != -1

    fun isMonetEnabled(): Boolean = DataUtil.getMonetEnabled()

    fun generateLighterShade(color: Int, factor: Float = 0.25f): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        hsv[1] = (hsv[1] * (1f - factor)).coerceIn(0f, 1f)
        hsv[2] = (hsv[2] + (1f - hsv[2]) * factor).coerceIn(0f, 1f)
        return Color.HSVToColor(hsv)
    }

    fun resolvePrimaryColor(context: Context): Int {
        val saved = DataUtil.getThemeColor()
        if (saved != -1) return saved
        val ta = context.theme.obtainStyledAttributes(intArrayOf(androidx.appcompat.R.attr.colorPrimary))
        val color = ta.getColor(0, Color.BLUE)
        ta.recycle()
        return color
    }

    private val presetColors = mapOf(
        "海洋蓝" to 0xFF0061A4.toInt(),
        "青翠绿" to 0xFF006A5E.toInt(),
        "日落橙" to 0xFFFF6D00.toInt(),
        "玫瑰红" to 0xFFBA1A1A.toInt(),
        "紫罗兰" to 0xFF6750A4.toInt(),
        "春日绿" to 0xFF386A20.toInt(),
        "琥珀金" to 0xFF6B5E00.toInt(),
        "深海蓝" to 0xFF003258.toInt(),
        "樱花粉" to 0xFFB32676.toInt(),
        "石墨灰" to 0xFF5C5C5C.toInt()
    )

    fun getPresetColors(): Map<String, Int> = presetColors

    fun isDarkColor(color: Int): Boolean {
        return ColorUtils.calculateLuminance(color) < 0.5
    }
}
