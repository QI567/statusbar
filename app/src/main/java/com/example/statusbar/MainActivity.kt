package com.example.statusbar

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.FitWindowsLinearLayout
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        StatusBarUtil.setRootViewFitsSystemWindows(this,true);
        //设置状态栏透明
        StatusBarUtil.setTranslucentStatus(this);
        //一般的手机的状态栏文字和图标都是白色的, 可如果你的应用也是纯白色的, 或导致状态栏文字看不清
        //所以如果你是这种情况,请使用以下代码, 设置状态使用深色文字图标风格, 否则你可以选择性注释掉这个if内容
        if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) {
            //如果不支持设置深色风格 为了兼容总不能让状态栏白白的看不清, 于是设置一个状态栏颜色为半透明,
            //这样半透明+白=灰, 状态栏的文字能看得清
            StatusBarUtil.setStatusBarColor(this,0x55000000);
        }
        findViewById<View>(R.id.text).setOnClickListener {
            val rect = Rect()
            it.getGlobalVisibleRect(rect)
            val positions = IntArray(2)
            it.getLocationInWindow(positions)
            val decorViewHeight = window.decorView.height
            val metrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(metrics)
            Log.d("position", "top:${rect.top},position:{${positions[0]},${positions[1]}},decorViewHeight:$decorViewHeight,metrics:${metrics.heightPixels},statusHeight:${getStatusHeight(this)},contentView:${getContentView(this)?.height?:0},navigator:${getNavigationBarHeight(this)}")
        }
    }

    /**
     * 获得状态栏的高度
     *
     * @param context
     * @return
     */
    fun getStatusHeight(context: Context): Int {
        var statusHeight = -1
        try {
            val clazz = Class.forName("com.android.internal.R\$dimen")
            val `object` = clazz.newInstance()
            val height = Integer.parseInt(clazz.getField("status_bar_height")
                .get(`object`).toString())
            statusHeight = context.resources.getDimensionPixelSize(height)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return statusHeight
    }

    /**
     * 获取底部导航的高度
     */
    fun getNavigationBarHeight(context: Context): Int {
        val resources = context.resources
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return resources.getDimensionPixelSize(resourceId)
    }

    fun getContentView(activity: Activity): View? {
        val decorView = activity.window.decorView as ViewGroup
        val childView1 = decorView.getChildAt(0) as ViewGroup
        val childView2 = childView1.getChildAt(1) as ViewGroup
        val fitWindowsLinearLayout = childView2.getChildAt(0) as FitWindowsLinearLayout
        return fitWindowsLinearLayout.getChildAt(1)
    }
}

