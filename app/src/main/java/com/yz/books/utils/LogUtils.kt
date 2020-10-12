package com.yz.books.utils

import android.util.Log
import com.yz.books.BuildConfig

/**
 * @author lilin
 * @time on 2019-12-19 16:28
 */
object LogUtils {

    fun e(content: String?) {
        if (BuildConfig.DEBUG) {
            Log.e("YZBOOKS", content ?: "")
        }
    }
    fun d(content: String?) {
        if (BuildConfig.DEBUG) {
            Log.d("YZBOOKS", content ?: "")
        }
    }
}