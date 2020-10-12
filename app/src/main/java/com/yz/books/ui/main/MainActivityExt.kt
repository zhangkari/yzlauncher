package com.yz.books.ui.main

import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.provider.Settings

/**
 * @author lilin
 * @time on 2020/8/30 上午9:41
 */

internal fun MainActivity.initLauncher() {
    val homeFilter = IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
    registerReceiver(mHomeReceiver, homeFilter)

    val currentHome = getHomeLauncher()
    if (isDefaultHome()) {
        return
    }
    setDefaultL()
}

internal fun MainActivity.getHomeLauncher(): String {
    val intent = Intent(Intent.ACTION_MAIN)
    intent.addCategory(Intent.CATEGORY_HOME)
    val resolveInfo = packageManager.resolveActivity(intent, 0)
    return resolveInfo.activityInfo.packageName
}

/**
 * 判断自己是否为默认桌面
 */
internal fun MainActivity.isDefaultHome(): Boolean {
    //val intent = Intent(Intent.ACTION_MAIN)// Intent.ACTION_VIEW
    return with(Intent(Intent.ACTION_MAIN)) {
        addCategory("android.intent.category.HOME")
        addCategory("android.intent.category.DEFAULT")
        val info = packageManager.resolveActivity(this,
            PackageManager.MATCH_DEFAULT_ONLY)
        //loge("isDefaultHome==$packageName//${info.activityInfo.packageName}")
        packageName == info.activityInfo.packageName
    }
}

internal fun MainActivity.setDefaultL() {
    var intent = Intent(Intent.ACTION_MAIN)
    intent.addCategory("android.intent.category.HOME")
    try {
        intent.component = ComponentName("android",
            "com.android.internal.app.ResolverActivity")
        startActivity(intent)
    } catch (e: Exception) {// 这里就是为了处置华为手机的
        try {
            intent.component = ComponentName(
                "com.huawei.android.internal.app",
                "com.huawei.android.internal.app.HwResolverActivity")// 这个类有些华为手机找不到
            startActivity(intent)
        } catch (e1: Exception) {
            e1.printStackTrace()
            try {
                startHuaweiSettingActOfDefLauncher()// 开启桌面设置
            } catch (e2: Exception) {
                e2.printStackTrace()
                intent = Intent(Settings.ACTION_APPLICATION_SETTINGS)// 还不行，就只能应用程序设置了
                startActivity(intent)
            }

        }
    }
}

internal fun MainActivity.startHuaweiSettingActOfDefLauncher() {
    val localIntentFilter = IntentFilter()
    localIntentFilter.addAction(Intent.ACTION_MAIN)// "android.intent.action.MAIN"
    localIntentFilter.addCategory(Intent.CATEGORY_HOME)// "android.intent.category.HOME"
    val localIntent3 = Intent(localIntentFilter.getAction(0))
    localIntent3.addCategory(localIntentFilter.getCategory(0))
    val localIntent4 = Intent()
    localIntent4.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    localIntent4.setClassName("com.android.settings",
        "com.android.settings.Settings\$PreferredSettingsActivity")
    localIntent4.putExtra("preferred_app_package_name", packageName)
    localIntent4.putExtra("preferred_app_class_name",
        MainActivity::class.java.name)
    localIntent4.putExtra("is_user_confirmed", true)
    localIntent4.putExtra("preferred_app_intent", localIntent3)
    localIntent4.putExtra("preferred_app_intent_filter", localIntentFilter)
    localIntent4.putExtra("preferred_app_label", "默认桌面设置")
    startActivity(localIntent4)
}