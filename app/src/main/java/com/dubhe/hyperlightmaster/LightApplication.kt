package com.dubhe.hyperlightmaster

import android.app.Application
import androidx.lifecycle.ViewModelProvider

class LightApplication: Application() {

    lateinit var lightViewModel: LightViewModel

    companion object {
        @JvmField
        val TAG = LightApplication::class.simpleName
        const val DATA_STATUS_未初始化 = 0
        const val DATA_STATUS_初始化中 = 1
        const val DATA_STATUS_已初始化 = 2
        var initDataStatus = DATA_STATUS_未初始化

        lateinit var instance: LightApplication
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        lightViewModel = ViewModelProvider.AndroidViewModelFactory(this)
            .create(LightViewModel::class.java)
        lightViewModel.initData()
    }


}