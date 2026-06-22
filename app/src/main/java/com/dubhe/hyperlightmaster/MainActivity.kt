package com.dubhe.hyperlightmaster

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.dubhe.hyperlightmaster.databinding.ActivityMainBinding
import com.dubhe.hyperlightmaster.dialog.OkDialog
import com.dubhe.hyperlightmaster.util.DataUtil
import com.dubhe.hyperlightmaster.util.ThemeColorManager
import com.google.android.material.color.DynamicColors

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var okDialog: OkDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        if (DataUtil.getMonetEnabled()) {
            DynamicColors.applyIfAvailable(this)
        }
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupEdgeToEdge()

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        binding.navView.setupWithNavController(navController)

        applyThemeColorToNav()

        okDialog = OkDialog(this@MainActivity).apply {
            this.setOkText("确定")
            this.setInfo("该软件需要root权限才能正常工作")
        }

        if (!LightApplication.instance.isInit) {
            okDialog.show()
        }
    }

    private fun applyThemeColorToNav() {
        if (DataUtil.getMonetEnabled()) return
        val color = ThemeColorManager.getPrimaryColor()
        if (color != -1) {
            binding.navView.itemIconTintList = null
            val iconStates = android.content.res.ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_checked),
                    intArrayOf(-android.R.attr.state_checked)
                ),
                intArrayOf(color, 0x8A000000.toInt())
            )
            binding.navView.itemTextColor = iconStates
            binding.navView.itemIconTintList = iconStates
        }
    }

    private fun setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(0, systemBars.top, 0, 0)
            binding.navView.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }
    }
}
