package com.dubhe.hyperlightmaster.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import androidx.recyclerview.widget.RecyclerView
import com.dubhe.hyperlightmaster.LightApplication
import com.dubhe.hyperlightmaster.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        // 加载 res/xml/preferences.xml 中定义的菜单
        setPreferencesFromResource(R.xml.settings, rootKey)

        // 示例：通过代码设置“最大亮度上限”的最大值
        val maxBrightnessPref = findPreference<SeekBarPreference>("pref_max_brightness")
        maxBrightnessPref?.max = LightApplication.instance.lightViewModel.MAX_BRIGHTNESS

        // “最小亮度下限”已在 XML 中设置最小值为10

        // 联系作者点击事件：打开浏览器访问指定网址
        val contactAuthorPref = findPreference<Preference>("pref_contact_author")
        contactAuthorPref?.setOnPreferenceClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.coolapk.com/u/2444842"))
            startActivity(intent)
            true
        }

        // 检查更新点击事件：打开浏览器访问更新页面，同时可动态更新 summary（例如当前版本号）
        val checkUpdatePref = findPreference<Preference>("pref_check_update")
        checkUpdatePref?.setOnPreferenceClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/DubheBroken/HyperLightMaster/releases"))
            startActivity(intent)
            true
        }
    }

    override fun onCreateRecyclerView(
        inflater: LayoutInflater,
        parent: ViewGroup,
        savedInstanceState: Bundle?
    ): RecyclerView {
        val recyclerView = super.onCreateRecyclerView(inflater, parent, savedInstanceState)
        recyclerView.setPadding(0, 0, 0, 0) // 去掉左右上下的内边距
        recyclerView.clipToPadding = false
        return recyclerView
    }

}
