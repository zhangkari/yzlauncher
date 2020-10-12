package com.yz.books.ext

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager

/**
 * @author lilin
 * @time on 2019-12-17 10:06
 */

inline fun <reified T > Activity.startToActivity(vararg extras: Pair<String, Any>){
    val intent = Intent(this, T::class.java)
    if (extras.isNotEmpty()) {
        val bundle = Bundle()
        extras.forEach { (key,value) ->
            bundle.putValueInBundle(key, value)
        }
        intent.putExtras(bundle)
    }
    startActivity(intent)
}

/**
 * 隐藏虚拟键位
 */
fun Activity.hideVirtualKey() {
    //保持布局状态
    var uiOptions = android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            //布局位于状态栏下方
            android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
            //全屏
            android.view.View.SYSTEM_UI_FLAG_FULLSCREEN or
            //隐藏导航栏
            android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    uiOptions = if (android.os.Build.VERSION.SDK_INT >= 19) {
        uiOptions or 0x00001000
    } else {
        uiOptions or android.view.View.SYSTEM_UI_FLAG_LOW_PROFILE
    }
    window.decorView.systemUiVisibility = uiOptions
}

/**
 * 自动隐藏软键盘
 * //https://blog.csdn.net/qq_27485935/article/details/63681745
 */
fun Activity.hideSoftInput() {
    val view = currentFocus
    if (view != null) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }
}