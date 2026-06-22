package com.dubhe.hyperlightmaster.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.text.InputType
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.checkSelfPermission
import com.dubhe.hyperlightmaster.LightApplication
import com.dubhe.hyperlightmaster.R
import com.dubhe.hyperlightmaster.base.BaseFragment
import com.dubhe.hyperlightmaster.databinding.FragmentSettingsMd3Binding
import com.dubhe.hyperlightmaster.dialog.ColorPickerDialog
import com.dubhe.hyperlightmaster.util.DataUtil
import com.dubhe.hyperlightmaster.util.ThemeColorManager
import com.dubhe.hyperlightmaster.util.showNotification
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class SettingsFragment : BaseFragment<FragmentSettingsMd3Binding>() {

    override fun getLayoutResId(): Int = R.layout.fragment_settings_md3

    override fun initView() {
        val viewModel = LightApplication.instance.lightViewModel

        // ===== 通知栏开关 =====
        dataBinding.switchNotification.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) requestPermissions()
        }
        dataBinding.cardNotification.setOnClickListener {
            dataBinding.switchNotification.isChecked = !dataBinding.switchNotification.isChecked
        }

        // ===== 快捷图块生效开关 =====
        dataBinding.switchQuickTile.isChecked = DataUtil.getMinMaxSetupQuickTile()
        dataBinding.switchQuickTile.setOnCheckedChangeListener { _, isChecked ->
            DataUtil.saveMinMaxSetupQuickTile(isChecked)
        }
        dataBinding.constraintQuickTile.setOnClickListener {
            dataBinding.switchQuickTile.isChecked = !dataBinding.switchQuickTile.isChecked
        }

        // ===== 亮度锁定方案 =====
        dataBinding.constraintLockBrightnessMode.setOnClickListener {
            showLockModeDialog()
        }

        // ===== 联系作者 =====
        dataBinding.llContactAuthor.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://www.coolapk.com/u/2444842")))
        }

        // ===== 当前版本 =====
        dataBinding.llVersion.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/DubheBroken/HyperLightMaster/releases")))
        }

        // ===== 亮度上限滑动条 =====
        dataBinding.constraintMaxBright.setOnClickListener {
            showInputDialog("最大亮度上限", dataBinding.sliderMaxBrightness.value.toInt()) { value ->
                if (changeMax(value)) {
                    dataBinding.sliderMaxBrightness.value = value.toFloat()
                }
            }
        }

        // ===== 亮度下限点击修改 =====
        dataBinding.constraintMinBright.setOnClickListener {
            showInputDialog("最小亮度下限", dataBinding.sliderMinBrightness.value.toInt()) { value ->
                if (changeMin(value)) {
                    dataBinding.sliderMinBrightness.value = value.toFloat()
                }
            }
        }

        // ===== 莫奈取色 =====
        dataBinding.switchMonet.isChecked = DataUtil.getMonetEnabled()
        updateThemeColorEnabled(!DataUtil.getMonetEnabled())
        dataBinding.switchMonet.setOnCheckedChangeListener { _, isChecked ->
            DataUtil.saveMonetEnabled(isChecked)
            requireActivity().recreate()
        }

        // ===== 主题色选择 =====
        updateThemeColorPreview(ThemeColorManager.getPrimaryColor())
        dataBinding.constraintThemeColor.setOnClickListener {
            val current = DataUtil.getThemeColor()
            val seed = if (current != -1) current else ThemeColorManager.resolvePrimaryColor(requireContext())
            ColorPickerDialog.show(requireContext(), seed) { color ->
                DataUtil.saveThemeColor(color)
                requireActivity().recreate()
            }
        }


        if (LightApplication.instance.isInit) {
            // 初始化通知栏开关
            viewModel.notifyOn.value?.let {
                dataBinding.switchNotification.isChecked = it
                dataBinding.switchNotification.isEnabled = true
            }
            viewModel.notifyOn.observe(this) {
                dataBinding.switchNotification.isChecked = it
            }

            // 初始化最大亮度
            dataBinding.sliderMaxBrightness.valueFrom = 0f
            val maxBrightness = viewModel.deviceState.MAX_BRIGHTNESS
            dataBinding.sliderMaxBrightness.valueTo = if (maxBrightness <= 0) 100f else maxBrightness.toFloat()
            viewModel.deviceState.maxBrightnessValueFromLogic.value?.let { maxVal ->
                dataBinding.sliderMaxBrightness.value = maxVal.toFloat()
                dataBinding.tvMaxBrightnessValue.text = maxVal.toString()
                val safeTo = maxVal.coerceAtLeast(viewModel.deviceState.MIN_BRIGHTNESS + 1)
                    .coerceAtMost(viewModel.deviceState.MAX_BRIGHTNESS - 1).toFloat()
                dataBinding.sliderMinBrightness.valueTo = safeTo
            }

            viewModel.deviceState.maxBrightnessValueFromLogic.observe(this) { maxVal ->
                dataBinding.sliderMaxBrightness.value = maxVal.toFloat()
                dataBinding.tvMaxBrightnessLabel.text = "最大亮度上限"
                dataBinding.tvMaxBrightnessValue.text = maxVal.toString()
                val safeTo = maxVal.coerceAtLeast(viewModel.deviceState.MIN_BRIGHTNESS + 1)
                    .coerceAtMost(viewModel.deviceState.MAX_BRIGHTNESS - 1).toFloat()
                dataBinding.sliderMinBrightness.valueTo = safeTo
            }

            dataBinding.sliderMaxBrightness.addOnChangeListener { _, value, fromUser ->
                if (fromUser) changeMax(value.toInt())
            }

            // 初始化最小亮度
            dataBinding.sliderMinBrightness.valueFrom = viewModel.deviceState.MIN_BRIGHTNESS.toFloat()
            viewModel.deviceState.minBrightnessValueFromLogic.value?.let { minVal ->
                dataBinding.sliderMinBrightness.value = minVal.toFloat()
                dataBinding.tvMinBrightnessValue.text = minVal.toString()
                val safeFrom = minVal.coerceAtLeast(viewModel.deviceState.MIN_BRIGHTNESS + 1)
                    .coerceAtMost(viewModel.deviceState.MAX_BRIGHTNESS - 1).toFloat()
                dataBinding.sliderMaxBrightness.valueFrom = safeFrom
            }

            viewModel.deviceState.minBrightnessValueFromLogic.observe(this) { minVal ->
                dataBinding.sliderMinBrightness.value = minVal.toFloat()
                dataBinding.tvMinBrightnessLabel.text = "最小亮度下限"
                dataBinding.tvMinBrightnessValue.text = minVal.toString()
                val safeFrom = minVal.coerceAtLeast(viewModel.deviceState.MIN_BRIGHTNESS + 1)
                    .coerceAtMost(viewModel.deviceState.MAX_BRIGHTNESS - 1).toFloat()
                dataBinding.sliderMaxBrightness.valueFrom = safeFrom
            }

            dataBinding.sliderMinBrightness.addOnChangeListener { _, value, fromUser ->
                if (fromUser) changeMin(value.toInt())
            }

            // 锁定方案初始显示
            updateLockModeSummary()
        } else {
            dataBinding.switchNotification.isEnabled = false
            dataBinding.sliderMaxBrightness.isEnabled = false
            dataBinding.sliderMinBrightness.isEnabled = false
        }
    }

    private fun updateLockModeSummary() {
        val summaries = mapOf(
            -1 to "关闭",
            DataUtil.MMKVValue.LOCK_BRIGHTNESS_READ_ONLY to "只读",
            DataUtil.MMKVValue.LOCK_BRIGHTNESS_RECEIVER to "接收广播"
        )
        val currentMode = DataUtil.getLockBrightnessMode()
        dataBinding.tvLockBrightnessValue.text = summaries[currentMode] ?: "关闭"
    }

    private fun showLockModeDialog() {
        val labels = listOf("关闭", "只读", "接收广播")
        val values = listOf(
            -1,
            DataUtil.MMKVValue.LOCK_BRIGHTNESS_READ_ONLY,
            DataUtil.MMKVValue.LOCK_BRIGHTNESS_RECEIVER
        )
        val currentMode = DataUtil.getLockBrightnessMode()
        val currentIndex = values.indexOf(currentMode).coerceAtLeast(0)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("亮度锁定方案")
            .setSingleChoiceItems(labels.toTypedArray(), currentIndex) { dialog, which ->
                val mode = values[which]
                DataUtil.saveLockBrightnessMode(mode)
                dialog.dismiss()
                updateLockModeSummary()
                when (mode) {
                    DataUtil.MMKVValue.LOCK_BRIGHTNESS_RECEIVER -> {
                        LightApplication.instance.registerScreenOnReceiver()
                        LightApplication.instance.lightViewModel.setWriteable()
                    }
                    DataUtil.MMKVValue.LOCK_BRIGHTNESS_READ_ONLY -> {
                        LightApplication.instance.lightViewModel.setReadOnly()
                        LightApplication.instance.unregisterScreenOnReceiver()
                    }
                    else -> {
                        LightApplication.instance.unregisterScreenOnReceiver()
                        LightApplication.instance.lightViewModel.setWriteable()
                    }
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun changeMin(MIN: Int): Boolean {
        if (MIN < 10) {
            Toast.makeText(context, "最小亮度不能低于10", Toast.LENGTH_SHORT).show()
            return false
        }
        val deviceState = LightApplication.instance.lightViewModel.deviceState
        val maxBrightness = deviceState.MAX_BRIGHTNESS
        if (MIN >= maxBrightness) {
            return false
        }
        val currentMax = deviceState.maxBrightnessValueFromLogic.value
        if (currentMax != null && MIN > currentMax) {
            deviceState.maxBrightnessValueFromLogic.value = MIN
            DataUtil.saveMaxBrightnessValueFromLogic(MIN)
        }
        deviceState.minBrightnessValueFromLogic.value = MIN
        DataUtil.saveMinBrightnessValueFromLogic(MIN)
        val nowBrightness = deviceState.brightness.value
        if (nowBrightness != null && nowBrightness < MIN) {
            LightApplication.instance.lightViewModel.writeBrightness(MIN)
            deviceState.brightness.postValue(MIN)
        }
        return true
    }

    private fun changeMax(MAX: Int): Boolean {
        val deviceState = LightApplication.instance.lightViewModel.deviceState
        if (MAX > deviceState.MAX_BRIGHTNESS) {
            Toast.makeText(context, "最大亮度上限不能超过系统最大亮度", Toast.LENGTH_SHORT).show()
            return false
        }
        val minBrightness = deviceState.MIN_BRIGHTNESS
        if (MAX <= minBrightness) {
            return false
        }
        val currentMin = deviceState.minBrightnessValueFromLogic.value
        if (currentMin != null && MAX < currentMin) {
            deviceState.minBrightnessValueFromLogic.value = MAX
            DataUtil.saveMinBrightnessValueFromLogic(MAX)
        }
        deviceState.maxBrightnessValueFromLogic.value = MAX
        DataUtil.saveMaxBrightnessValueFromLogic(MAX)
        val nowBrightness = deviceState.brightness.value
        if (nowBrightness != null && nowBrightness > MAX) {
            LightApplication.instance.lightViewModel.writeBrightness(MAX)
            deviceState.brightness.postValue(MAX)
        }
        return true
    }

    private fun showInputDialog(title: String, currentValue: Int, onValueSet: (Int) -> Unit) {
        context?.let { ctx ->
            val til = TextInputLayout(ctx).apply {
                boxBackgroundMode = TextInputLayout.BOX_BACKGROUND_OUTLINE
                setPadding(48, 16, 48, 0)
            }
            val input = TextInputEditText(ctx).apply {
                inputType = InputType.TYPE_CLASS_NUMBER
                setText(currentValue.toString())
            }
            til.addView(input)
            MaterialAlertDialogBuilder(ctx)
                .setTitle(title)
                .setView(til)
                .setPositiveButton("确定") { _, _ ->
                    val value = input.text.toString().toIntOrNull()
                    if (value != null) {
                        onValueSet(value)
                    } else {
                        Toast.makeText(ctx, "请输入有效的数字", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("取消") { dialog, _ -> dialog.cancel() }
                .show()
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showNotification()
        } else {
            Toast.makeText(requireContext(), "权限被拒绝，无法显示通知栏开关", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateThemeColorPreview(color: Int) {
        val drawable = dataBinding.ivThemeColorPreview.background.mutate() as? GradientDrawable
        drawable?.setColor(if (color != -1) color else ThemeColorManager.resolvePrimaryColor(requireContext()))
    }

    private fun updateThemeColorEnabled(enabled: Boolean) {
        dataBinding.constraintThemeColor.alpha = if (enabled) 1f else 0.4f
        dataBinding.constraintThemeColor.isEnabled = enabled
    }

    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                showNotification()
            }
        } else {
            showNotification()
        }
    }
}
