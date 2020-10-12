package com.yz.books

import android.app.Application
import android.content.Context
import com.tencent.bugly.Bugly
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.QbSdk.PreInitCallback
import com.yz.books.db.DaoManager
import com.yz.books.utils.sp.PreferencesUtils


/**
 * @author lilin
 * @time on 2019-12-16 13:48
 */
class AppApplication : Application() {

    companion object {
        lateinit var mContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        mContext = applicationContext

        PreferencesUtils.init(this)
        DaoManager.init(this)
        initBugly()
        initX5WebView()
    }

    private fun initBugly() {
        Bugly.init(this, "ffec860af4", BuildConfig.DEBUG)
    }

    private fun initX5WebView() {
        QbSdk.initX5Environment(this, object : PreInitCallback {
            override fun onCoreInitFinished() {}
            override fun onViewInitFinished(b: Boolean) {}
        })
    }
}