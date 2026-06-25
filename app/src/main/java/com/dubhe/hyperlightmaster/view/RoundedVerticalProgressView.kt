package com.dubhe.hyperlightmaster.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
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

    private val bottomPath = Path()
    private val topPath = Path()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        val r = cornerRadius.coerceAtMost(w / 2f)

        trackPaint.color = ContextCompat.getColor(context, R.color.md3_surface_container_highest)
        rect.set(0f, 0f, w, h)
        canvas.drawRoundRect(rect, r, r, trackPaint)

        val totalRange = (max - min).coerceAtLeast(1)
        val fraction = (progress - min).toFloat() / totalRange
        val middleHeight = (h - 2f * r) * fraction
        val totalProgressHeight = 2f * r + middleHeight

        if (totalProgressHeight <= 0f) return

        val deepColor = ThemeColorManager.resolvePrimaryColor(context)
        val lightColor = ThemeColorManager.generateLighterShade(deepColor)

        val gradient = LinearGradient(
            0f, h,
            0f, h - totalProgressHeight,
            intArrayOf(deepColor, lightColor),
            null,
            Shader.TileMode.CLAMP
        )
        progressPaint.shader = gradient

        val bottomY = h - totalProgressHeight

        if (middleHeight <= 0f) {
            rect.set(0f, bottomY, w, h)
            canvas.drawRoundRect(rect, r, r, progressPaint)
            return
        }

        bottomPath.reset()
        bottomPath.addRoundRect(
            0f, h - r, w, h,
            floatArrayOf(0f, 0f, 0f, 0f, r, r, r, r),
            Path.Direction.CW
        )
        canvas.drawPath(bottomPath, progressPaint)

        canvas.drawRect(0f, bottomY + r, w, h - r, progressPaint)

        topPath.reset()
        topPath.addRoundRect(
            0f, bottomY, w, bottomY + r,
            floatArrayOf(r, r, r, r, 0f, 0f, 0f, 0f),
            Path.Direction.CW
        )
        canvas.drawPath(topPath, progressPaint)
    }
}
