package com.dubhe.hyperlightmaster.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import androidx.preference.SwitchPreferenceCompat
import com.dubhe.hyperlightmaster.BuildConfig
import com.dubhe.hyperlightmaster.LightApplication
import com.dubhe.hyperlightmaster.R
import com.dubhe.hyperlightmaster.util.DataUtil
import com.dubhe.hyperlightmaster.util.showNotification

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        // 加载 res/xml/preferences.xml 中定义的菜单
        setPreferencesFromResource(R.xml.settings, rootKey)

        // 【通知栏开关】事件处理
        val notificationSwitchPref = findPreference<SwitchPreferenceCompat>("pref_notification_bar")

        notificationSwitchPref?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue == true) {
                requestPermissions()
            }
            true
        }

//        // 【快捷图块开关】事件处理
//        val quickTilePref = findPreference<SwitchPreferenceCompat>("pref_quick_tile")
//        quickTilePref?.setDefaultValue(LightApplication.instance.lightViewModel.autoBrightness.value)
//        quickTilePref?.setOnPreferenceChangeListener { _, newValue ->
//
//            // 这里添加你要执行的逻辑
//            true
//        }
        // 【快捷图块开关】事件处理
        val prefMinMaxSetupQuickTile = findPreference<SwitchPreferenceCompat>("pref_min_max_setup_quick_tile")
        prefMinMaxSetupQuickTile?.setDefaultValue(DataUtil.getMinMaxSetupQuickTile())
        prefMinMaxSetupQuickTile?.setOnPreferenceChangeListener { _, newValue ->
            DataUtil.saveMinMaxSetupQuickTile(newValue as Boolean)
            true
        }

        // 设置“最大亮度上限”的最大值
        val maxBrightnessPref = findPreference<SeekBarPreference>("pref_max_brightness")//最大亮度设置控件
        val minBrightnessPref = findPreference<SeekBarPreference>("pref_min_brightness")//最小亮度设置控件

        // 联系作者点击事件：打开浏览器访问指定网址
        val contactAuthorPref = findPreference<Preference>("pref_contact_author")
        contactAuthorPref?.setOnPreferenceClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.coolapk.com/u/2444842"))
            startActivity(intent)
            true
        }

        // 【深色模式设置】事件处理（下拉选单）
        val darkModePref = findPreference<ListPreference>("pref_dark_mode")
        darkModePref?.setOnPreferenceChangeListener { _, newValue ->
            true
        }

        // 【当前版本】点击事件：打开浏览器访问更新页面
        val nowVersionPref = findPreference<Preference>("pref_now_version")
        nowVersionPref?.title = "当前版本：${BuildConfig.VERSION_NAME}"
        nowVersionPref?.setOnPreferenceClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/DubheBroken/HyperLightMaster/releases"))
            startActivity(intent)
            true
        }

        // 【亮度锁定方案】事件处理（下拉选单）
        val lockModePref = findPreference<ListPreference>("pref_lock_brightness_mode")
        lockModePref?.setDefaultValue(DataUtil.getLockBrightnessMode().toString())
        lockModePref?.value = DataUtil.getLockBrightnessMode().toString()
        lockModePref?.setOnPreferenceChangeListener { _, newValue ->
            (newValue as? String)?.toIntOrNull()?.let {
                DataUtil.saveLockBrightnessMode(it)
                when (it) {
                    DataUtil.MMKVValue.LOCK_BRIGHTNESS_RECEIVER -> {
                        //开启广播
                        LightApplication.instance.registerScreenOnReceiver()

                        LightApplication.instance.lightViewModel.setWriteable()
                    }

                    DataUtil.MMKVValue.LOCK_BRIGHTNESS_READ_ONLY -> {
                        //只读文件
                        LightApplication.instance.lightViewModel.setReadOnly()

                        LightApplication.instance.unregisterScreenOnReceiver()
                    }

                    else -> {
                        LightApplication.instance.unregisterScreenOnReceiver()
                        LightApplication.instance.lightViewModel.setWriteable()
                    }
                }
            }
            true
        }

        if (LightApplication.instance.isInit) {
            // 【通知栏开关】事件处理
            notificationSwitchPref?.setDefaultValue(LightApplication.instance.lightViewModel.notifyOn.value)
            LightApplication.instance.lightViewModel.notifyOn.observe(this){
                notificationSwitchPref?.isChecked = it
            }

            // 设置“最大亮度上限”的最大值
            maxBrightnessPref?.max = LightApplication.instance.lightViewModel.deviceState.MAX_BRIGHTNESS
            maxBrightnessPref?.setDefaultValue(LightApplication.instance.lightViewModel.deviceState.maxBrightnessValueFromLogic.value)

            LightApplication.instance.lightViewModel.deviceState.maxBrightnessValueFromLogic.observe(this,{
                //逻辑最大亮度变化回调
                maxBrightnessPref?.title = "最大亮度上限: $it"
                maxBrightnessPref?.value = it

                //同时更新最小亮度上限的最大值
                minBrightnessPref?.max = it
            })

            // 【最大亮度上限】事件处理：滑动更改事件
            maxBrightnessPref?.setOnPreferenceChangeListener { _, newValue ->
                changeMax(newValue as Int)
            }

            // 点击后弹出输入对话框，允许直接输入数值
            maxBrightnessPref?.setOnPreferenceClickListener {
                showInputDialogForPreference("最大亮度上限", maxBrightnessPref.value) { inputValue ->
                    changeMax(inputValue)
                }
                true
            }

            // 【最小亮度下限】事件处理：滑动更改事件
            maxBrightnessPref?.max = LightApplication.instance.lightViewModel.deviceState.MAX_BRIGHTNESS
            minBrightnessPref?.min = LightApplication.instance.lightViewModel.deviceState.MIN_BRIGHTNESS
            maxBrightnessPref?.setDefaultValue(LightApplication.instance.lightViewModel.deviceState.minBrightnessValueFromLogic.value)

            LightApplication.instance.lightViewModel.deviceState.minBrightnessValueFromLogic.observe(this,{
                //逻辑最小亮度变化回调
                minBrightnessPref?.title = "最小亮度上限: $it"
                minBrightnessPref?.value = it

                //同时更新最大亮度上限的最小值
                maxBrightnessPref?.min = it
            })

            minBrightnessPref?.setOnPreferenceChangeListener { _, newValue ->
                changeMin(newValue as Int)
            }

            // 同时点击后弹出输入对话框，允许直接输入数值
            minBrightnessPref?.setOnPreferenceClickListener {
                showInputDialogForPreference("最小亮度下限", minBrightnessPref.value) { inputValue ->
                    changeMin(inputValue)
                }
                true
            }
        } else {
            notificationSwitchPref?.isEnabled = false
            maxBrightnessPref?.isEnabled = false
            minBrightnessPref?.isEnabled = false
        }
    }

    /**
     * 改变逻辑最小亮度
     */
    private fun changeMin(MIN: Int): Boolean {
        if (MIN < 10) {
            Toast.makeText(context, "最小亮度不能低于10", Toast.LENGTH_SHORT).show()
            return false
        } else {
            LightApplication.instance.lightViewModel.deviceState.minBrightnessValueFromLogic.value = MIN
            DataUtil.saveMinBrightnessValueFromLogic(MIN)
            val nowBrightness = LightApplication.instance.lightViewModel.deviceState.brightness.value
            if (nowBrightness != null && nowBrightness < MIN) {
                //如果修改后最小亮度比当前亮度高，刷新当前亮度
                LightApplication.instance.lightViewModel.writeBrightness(MIN)
                LightApplication.instance.lightViewModel.deviceState.brightness.postValue(MIN)
            }
            return true
        }
    }

    /**
     * 改变逻辑最大亮度
     */
    private fun changeMax(MAX: Int): Boolean {
        if(MAX > LightApplication.instance.lightViewModel.deviceState.MAX_BRIGHTNESS){
            Toast.makeText(context, "最大亮度上限不能超过系统最大亮度", Toast.LENGTH_SHORT).show()
            return false
        } else {
            LightApplication.instance.lightViewModel.deviceState.maxBrightnessValueFromLogic.value =MAX
            DataUtil.saveMaxBrightnessValueFromLogic(MAX)
            val nowBrightness = LightApplication.instance.lightViewModel.deviceState.brightness.value
            if (nowBrightness != null && nowBrightness > MAX) {
                //如果修改后最大亮度比当前亮度低，刷新当前亮度
                LightApplication.instance.lightViewModel.writeBrightness(MAX)
                LightApplication.instance.lightViewModel.deviceState.brightness.postValue(MAX)
            }
            return true
        }
    }

    /**
     * 辅助方法：显示一个输入对话框供用户直接输入数值
     *
     * @param title 对话框标题
     * @param currentValue 当前数值
     * @param onValueSet 输入完成后的回调，返回用户输入的 Int 值
     */
    private fun showInputDialogForPreference(title: String, currentValue: Int, onValueSet: (Int) -> Unit) {
        context?.let { ctx ->
            val builder = AlertDialog.Builder(ctx)
            builder.setTitle(title)
            val input = EditText(ctx)
            input.inputType = InputType.TYPE_CLASS_NUMBER
            input.setText(currentValue.toString())
            builder.setView(input)
            builder.setPositiveButton("确定") { _, _ ->
                val value = input.text.toString().toIntOrNull()
                if (value != null) {
                    onValueSet(value)
                } else {
                    Toast.makeText(ctx, "请输入有效的数字", Toast.LENGTH_SHORT).show()
                }
            }
            builder.setNegativeButton("取消") { dialog, _ -> dialog.cancel() }
            builder.show()
        }
    }

    // 注册权限请求的 ActivityResultLauncher
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // 权限被授予，执行相应操作
                showNotification()
            } else {
                // 权限被拒绝，执行拒绝后的处理
                Toast.makeText(requireContext(), "权限被拒绝，无法显示通知栏开关", Toast.LENGTH_SHORT).show()
            }
        }

    //请求通知权限
    private fun requestPermissions() {
        // 针对 Android 13/14（API 33+）申请通知权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                showNotification()
            }
        } else {
            // 低于 API 33 的无需动态申请
            showNotification()
        }
    }


}
