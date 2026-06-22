package com.dubhe.hyperlightmaster.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.dubhe.hyperlightmaster.R
import com.dubhe.hyperlightmaster.util.ThemeColorManager

class RoundedVerticalProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var max: Int = 100
        set(value) {
            field = value
            invalidate()
        }

    var min: Int = 0
        set(value) {
            field = value
            invalidate()
        }

    var progress: Int = 0
        set(value) {
            field = value.coerceIn(min, max)
            invalidate()
        }

    private val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val rect = RectF()

    private val cornerRadius: Float by lazy {
        resources.getDimension(R.dimen.progress_corner_radius)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        val r = cornerRadius.coerceAtMost(w / 2f)

        trackPaint.color = ContextCompat.getColor(context, R.color.md3_surface_container_highest)
        rect.set(0f, 0f, w, h)
        canvas.drawRoundRect(rect, r, r, trackPaint)

        if (progress <= min) return

        val totalRange = (max - min).coerceAtLeast(1)
        val fraction = (progress - min).toFloat() / totalRange
        val progressHeight = (h * fraction).coerceAtLeast(r * 2f)

        val top = h - progressHeight

        val deepColor = ThemeColorManager.resolvePrimaryColor(context)
        val lightColor = ThemeColorManager.generateLighterShade(deepColor)

        val gradient = LinearGradient(
            0f, h,
            0f, top,
            intArrayOf(deepColor, lightColor),
            null,
            Shader.TileMode.CLAMP
        )
        progressPaint.shader = gradient
        rect.set(0f, top, w, h)
        canvas.drawRoundRect(rect, r, r, progressPaint)
    }
}
