package com.dubhe.hyperlightmaster

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.gyf.barlibrary.ImmersionBar

abstract class BaseActivity : AppCompatActivity() {

    protected var statusBarColorId = R.color.white //状态栏(顶部)背景颜色ID
    protected var navigationBarColorId = R.color.white //导航栏(底部)背景颜色ID
    protected var isWhiteStatusBarIcon = false //状态栏(顶部)图标颜色,true为亮色图标,false为暗色图标
    protected var isWhiteNavigationBarIcon = false //导航栏(底部)图标颜色,true为亮色图标,false为暗色图标
    protected var isContentInvade = false //是否开启内容入侵模式(顶部元素的背景会覆盖状态栏背景)
    protected var viewTopId = 0 //顶部元素ID，内容入侵沉浸式状态栏使用
    protected var enableImmersionBar = true //是否开启沉浸式状态栏，默认开启

    protected var isViewReady = false //view是否渲染完成

    abstract fun getLayout(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(getLayout())

        val mHandler = Handler(Looper.getMainLooper())
        window.decorView.post {
            Thread.dumpStack()
            mHandler.post {
                isViewReady = true
                onViewReady()
            }
        }
        initView()
        if (enableImmersionBar) {
            initStatusBar()
        }
        initData()
    }

    abstract fun initView()
    abstract fun initData()
    open fun onViewReady() {

    }

    /**
     * 沉浸式状态栏 下列参数请在onCreate之前赋值
     *
     * statusBarColorId         状态栏(顶部)背景颜色ID
     * navigationBarColorId     导航栏(底部)背景颜色ID
     * isWhiteStatusBarIcon     状态栏(顶部)图标颜色,true为亮色图标,false为暗色图标
     * isWhiteNavigationBarIcon 导航栏(顶部)图标颜色,true为亮色图标,false为暗色图标
     * isContentInvade          是否开启内容入侵模式(顶部元素的背景会覆盖状态栏背景)
     *                          如果开启,必须要在顶部放一个View，viewTopId对应其ID
     *                          如果开启必须要传入状态栏(顶部)图标颜色
     */
    protected open fun initStatusBar() {
        if (isContentInvade) {
            ImmersionBar.with(this)
                .statusBarView(viewTopId)
                .navigationBarColor(navigationBarColorId) //导航栏颜色，不写默认黑色
                .statusBarDarkFont(!isWhiteStatusBarIcon)   //状态栏字体是深色，不写默认为亮色
                .navigationBarDarkIcon(!isWhiteNavigationBarIcon) //导航栏图标是深色，不写默认为亮色
                .flymeOSStatusBarFontColor(if (isWhiteStatusBarIcon) R.color.white else R.color.color_333)  //修改flyme OS状态栏字体颜色
                //                .keyboardEnable(true)  //解决软键盘与底部输入框冲突问题，默认为false，还有一个重载方法，可以指定软键盘mode
                //                .keyboardMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)  //单独指定软键盘模式
                //                    .setOnKeyboardListener(new OnKeyboardListener() {    //软键盘监听回调
                //                        @Override
                //                        public void onKeyboardChange(boolean isPopup, int keyboardHeight) {
                ////                        Logger.e(isPopup);  //isPopup为true，软键盘弹出，为false，软键盘关闭
                //                        }
                //                    })
                .init()
        } else {
            ImmersionBar.with(this)
                .statusBarColor(statusBarColorId)     //状态栏颜色，不写默认透明色
                .navigationBarColor(navigationBarColorId) //导航栏颜色，不写默认黑色
                .statusBarDarkFont(!isWhiteStatusBarIcon)   //状态栏字体是深色，不写默认为亮色
                .navigationBarDarkIcon(!isWhiteNavigationBarIcon) //导航栏图标是深色，不写默认为亮色
                .flymeOSStatusBarFontColor(if (isWhiteStatusBarIcon) R.color.white else R.color.color_333)  //修改flyme OS状态栏字体颜色
                .fitsSystemWindows(true)    //解决状态栏和布局重叠问题，任选其一，默认为false，当为true时一定要指定statusBarColor()，不然状态栏为透明色，还有一些重载方法
                //                .keyboardEnable(true)  //解决软键盘与底部输入框冲突问题，默认为false，还有一个重载方法，可以指定软键盘mode
                //                    .keyboardMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)  //单独指定软键盘模式
                //                    .setOnKeyboardListener(new OnKeyboardListener() {    //软键盘监听回调
                //                        @Override
                //                        public void onKeyboardChange(boolean isPopup, int keyboardHeight) {
                ////                        Logger.e(isPopup);  //isPopup为true，软键盘弹出，为false，软键盘关闭
                //                        }
                //                    })
                .init()
        }
    }

}