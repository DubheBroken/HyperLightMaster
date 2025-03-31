package com.dubhe.hyperlightmaster

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.dubhe.hyperlightmaster.databinding.ActivityMainBinding
import com.dubhe.hyperlightmaster.dialog.OkDialog

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var okDialog: OkDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

//        val appBarConfiguration = AppBarConfiguration(setOf(R.id.navigation_light,
//                                      R.id.navigation_info,
//                                      R.id.navigation_setting))
//        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        okDialog = OkDialog(this@MainActivity).apply {
            this.setOkText("确定")
            this.setInfo("该软件需要root权限才能正常工作")
        }

        if (!LightApplication.instance.isInit) {
            okDialog.show()
        }
    }
}