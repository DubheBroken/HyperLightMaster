package com.dubhe.hyperlightmaster.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.SeekBar
import android.widget.EditText
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ScrollView
import com.dubhe.hyperlightmaster.util.ThemeColorManager

class ColorPickerDialog(
    context: Context,
    private val initialColor: Int
) : Dialog(context) {

    var onColorPicked: ((Int) -> Unit)? = null

    private var currentColor = initialColor
    private val hsv = FloatArray(3)
    private lateinit var previewView: View
    private lateinit var picker: HueSatPicker
    private lateinit var hueBar: SeekBar
    private lateinit var hexInput: EditText
    private var suppressHexUpdate = false
    private val presetColors = ThemeColorManager.getPresetColors()

    init {
        Color.colorToHSV(initialColor, hsv)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = ScrollView(context).apply { setPadding(0, 24, 0, 0) }
        val container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 0, 24, 24)
        }

        container.addView(TextView(context).apply {
            text = "选择主题色"
            textSize = 20f
            setTextColor(0xFF1A1C1E.toInt())
            setPadding(0, 0, 0, 16)
        })

        container.addView(createPresets())

        container.addView(sectionLabel("色相"))
        hueBar = SeekBar(context).apply {
            max = 360
            progress = hsv[0].toInt()
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(sb: SeekBar?, v: Int, fromUser: Boolean) {
                    if (!fromUser) return
                    hsv[0] = v.toFloat()
                    picker.hue = v.toFloat()
                    applyHsv()
                }
                override fun onStartTrackingTouch(sb: SeekBar?) {}
                override fun onStopTrackingTouch(sb: SeekBar?) {}
            })
        }
        container.addView(hueBar, ViewGroup.LayoutParams.MATCH_PARENT, 48)

        container.addView(sectionLabel("饱和度 / 明度"))
        picker = HueSatPicker(context).apply {
            hue = hsv[0]
            saturation = hsv[1]
            value = hsv[2]
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 280)
            onColorChanged = { c ->
                currentColor = c
                Color.colorToHSV(c, hsv)
                updatePreview()
                hueBar.progress = hsv[0].toInt()
                syncHex()
            }
        }
        container.addView(picker)

        val bottom = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 20, 0, 8)
        }

        previewView = View(context).apply {
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 12f
                setColor(currentColor)
            }
            val lp = LinearLayout.LayoutParams(56, 56)
            lp.marginEnd = 16
            bottom.addView(this, lp)
        }

        hexInput = EditText(context).apply {
            inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            setText(formatHex(currentColor))
            setTextColor(0xFF1A1C1E.toInt())
            setBackgroundColor(0xFFEEF1F7.toInt())
            setPadding(16, 12, 16, 12)
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (suppressHexUpdate) return
                    val hex = s.toString().trim()
                    if (hex.length == 7 && hex.startsWith("#")) {
                        try {
                            val c = Color.parseColor(hex)
                            if (c != currentColor) {
                                currentColor = c
                                Color.colorToHSV(c, hsv)
                                picker.hue = hsv[0]
                                picker.saturation = hsv[1]
                                picker.value = hsv[2]
                                hueBar.progress = hsv[0].toInt()
                                updatePreview()
                            }
                        } catch (_: Exception) {}
                    }
                }
            })
            val lp = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            bottom.addView(this, lp)
        }
        container.addView(bottom)

        val btns = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.END
            setPadding(0, 16, 0, 0)
        }
        btns.addView(btn("取消", 0xFF666666.toInt()) { dismiss() })
        btns.addView(btn("确定", 0xFF0061A4.toInt()) {
            onColorPicked?.invoke(currentColor)
            dismiss()
        })
        container.addView(btns)

        root.addView(container)
        setContentView(root)
        window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setGravity(Gravity.BOTTOM)
        }
    }

    private fun sectionLabel(text: String) = TextView(context).apply {
        this.text = text
        textSize = 14f
        setTextColor(0xFF666666.toInt())
        setPadding(0, 18, 0, 6)
    }

    private fun btn(text: String, color: Int, onClick: () -> Unit) = Button(context).apply {
        this.text = text
        setBackgroundColor(Color.TRANSPARENT)
        setTextColor(color)
        textSize = 15f
        setOnClickListener { onClick() }
    }

    private fun createPresets(): View {
        val scroll = HorizontalScrollView(context)
        val row = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 0, 0, 8)
        }
        for ((_, color) in presetColors) {
            val v = View(context).apply {
                val sel = if (color == initialColor) 0xFF000000.toInt() else 0x33000000
                background = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(color)
                    setStroke(3, sel)
                    setSize(52, 52)
                }
                val lp = LinearLayout.LayoutParams(52, 52)
                lp.marginEnd = 14
                row.addView(this, lp)
                setOnClickListener { selectPreset(color, row) }
            }
        }
        scroll.addView(row)
        return scroll
    }

    private fun selectPreset(color: Int, row: ViewGroup) {
        currentColor = color
        Color.colorToHSV(color, hsv)
        picker.hue = hsv[0]
        picker.saturation = hsv[1]
        picker.value = hsv[2]
        hueBar.progress = hsv[0].toInt()
        updatePreview()
        syncHex()
        var i = 0
        for ((_, pc) in presetColors) {
            if (i < row.childCount) {
                val c = row.getChildAt(i)
                c.background = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(pc)
                    setStroke(3, if (pc == color) 0xFF000000.toInt() else 0x33000000)
                    setSize(52, 52)
                }
            }
            i++
        }
    }

    private fun applyHsv() {
        currentColor = Color.HSVToColor(hsv)
        updatePreview()
        syncHex()
    }

    private fun updatePreview() {
        (previewView.background as? GradientDrawable)?.setColor(currentColor)
    }

    private fun syncHex() {
        suppressHexUpdate = true
        hexInput.setText(formatHex(currentColor))
        suppressHexUpdate = false
    }

    private fun formatHex(color: Int) = String.format("#%06X", 0xFFFFFF and color)

    companion object {
        fun show(context: Context, initialColor: Int, onPicked: (Int) -> Unit) {
            ColorPickerDialog(context, initialColor).apply {
                onColorPicked = onPicked
                show()
            }
        }
    }
}
