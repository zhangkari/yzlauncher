package com.yz.books.ext

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import com.yz.books.AppApplication
import com.yz.books.utils.ToastUtils
import java.io.File

/**
 * @author lilin
 * @time on 2020-01-16 09:27
 */

/**
 * 获取全局context
 */
fun getApplicationContext(): Context = AppApplication.mContext

fun Context.showToast(content: String?) {
    ToastUtils.showToast(this, content)
}

/**
 * dp转px
 */
fun Context.dp2px(value: Float) = (value * resources.displayMetrics.density + 0.5f).toInt()

fun Context.dp2px(value: Int) = (value * resources.displayMetrics.density + 0.5f).toInt()

val Context.screenWidth
    get() = resources.displayMetrics.widthPixels

val Context.screenHeight
    get() = resources.displayMetrics.heightPixels

fun Context.openFile(file: File) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//判读版本是否在7.0以上
            val apkUri = FileProvider.getUriForFile(this, "$packageName.fileProvider", file)
            val install = Intent(Intent.ACTION_VIEW)
            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)//添加这一句表示对目标应用临时授权该Uri所代表的文件
            install.setDataAndType(apkUri, "application/vnd.android.package-archive")
            startActivity(install)
        } else {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(Uri.parse("file://$file"), "application/vnd.android.package-archive")
            startActivity(intent)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * 判断当前是否有网络连接
 * @return
 */
fun Context.isNetworkConnected(): Boolean {
    val connectivityManager =
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = connectivityManager.activeNetworkInfo
    return if (networkInfo != null && networkInfo.isAvailable) {   // 判断网络连接是否打开
        networkInfo.isConnected
    } else {
        false
    }
}
