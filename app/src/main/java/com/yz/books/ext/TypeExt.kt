package com.yz.books.ext

import com.yz.books.api.ApiConstant
import com.yz.books.common.Constant
import com.yz.books.utils.FileUtils

/**
 * @author lilin
 * @time on 2020-01-20 14:55
 */

/**
 * 文件图片下载地址前缀
 */
fun String.addFileHostUrl(): String {
    if (this.startsWith("http") || this.startsWith("https")) {
        return this
    }
    if (!Constant.ONLINE_VERSION && !this.endsWith(".zip")  && !this.endsWith(".apk")) {//&& !getApplicationContext().isNetworkConnected()
        var path = FileUtils.getLocalPath() + this
        if (path.contains("//")) {
            path = path.replace("//", "/")
        }
        return path
    }
    return ApiConstant.HOST + this
}