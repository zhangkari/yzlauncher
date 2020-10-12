package com.yz.books.ui.main.bean

/**
 * @author lilin
 * @time on 2020/5/8 下午1:56
 */

data class AppUpdateBean(
    val downUrl: String,
    val fileMd5String: String,
    val isUpdate: Int,
    val updateInfo: String,
    val versionCode: Int
)