package com.dubhe.hyperlightmaster.dialog

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class HueSatPicker @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var hue: Float = 0f
        set(value) {
            field = value.coerceIn(0f, 360f)
            updateSatPos()
            invalidate()
        }

    var saturation: Float = 1f
        set(value) {
            field = value.coerceIn(0f, 1f)
            updateSatPos()
            currentColor = Color.HSVToColor(floatArrayOf(hue, saturation, value))
            invalidate()
        }

    var value: Float = 1f
        set(value) {
            field = value.coerceIn(0f, 1f)
            currentColor = Color.HSVToColor(floatArrayOf(hue, saturation, this.value))
            invalidate()
        }

    var currentColor: Int = Color.RED
        internal set

    var onColorChanged: ((Int) -> Unit)? = null

    private val satPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val cursorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFFFFFFF.toInt()
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }
    private val cursorFill = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val rect = RectF()
    private var cursorX = 0f
    private var cursorY = 0f

    init {
        currentColor = Color.HSVToColor(floatArrayOf(hue, saturation, value))
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateSatPos()
    }

    private fun updateSatPos() {
        val w = width.toFloat()
        val h = height.toFloat()
        cursorX = ((saturation).coerceIn(0f, 1f) * w)
        cursorY = ((1f - value).coerceIn(0f, 1f) * h)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()

        val hueColor = Color.HSVToColor(floatArrayOf(hue, 1f, 1f))

        // White-to-hue horizontal gradient
        val whiteShader = LinearGradient(0f, 0f, w, 0f,
            intArrayOf(Color.WHITE, hueColor), null, Shader.TileMode.CLAMP)
        satPaint.shader = whiteShader
        rect.set(0f, 0f, w, h)
        canvas.drawRect(rect, satPaint)

        // Black vertical gradient overlay
        val blackShader = LinearGradient(0f, 0f, 0f, h,
            intArrayOf(Color.TRANSPARENT, Color.BLACK), null, Shader.TileMode.CLAMP)
        satPaint.shader = blackShader
        canvas.drawRect(rect, satPaint)

        // Cursor
        val cx = cursorX.coerceIn(4f, w - 4f)
        val cy = cursorY.coerceIn(4f, h - 4f)
        cursorPaint.color = if (value > 0.5f && saturation < 0.5f) 0xFF333333.toInt() else 0xFFFFFFFF.toInt()
        canvas.drawCircle(cx, cy, 8f, cursorPaint)
        cursorFill.color = currentColor
        canvas.drawCircle(cx, cy, 5f, cursorFill)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x.coerceIn(0f, width.toFloat())
        val y = event.y.coerceIn(0f, height.toFloat())
        saturation = (x / width).coerceIn(0f, 1f)
        value = 1f - (y / height).coerceIn(0f, 1f)
        currentColor = Color.HSVToColor(floatArrayOf(hue, saturation, value))
        onColorChanged?.invoke(currentColor)
        invalidate()
        return true
    }
}
