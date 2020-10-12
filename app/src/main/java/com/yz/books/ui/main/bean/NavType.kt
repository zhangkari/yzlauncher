package com.yz.books.ui.main.bean

/**
 * @author lilin
 * @time on 2020-01-03 17:09
 */
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class NavType {
    companion object {
        const val TYPE_BOOK = "book"
        const val TYPE_VIDEO = "video"
        const val TYPE_AUDIO = "audio"
        const val TYPE_JOURNAL = "journal"
        const val TYPE_NOTICE = "notice"
        const val TYPE_CLOUD = "cloud"
        const val TYPE_LOGO = "logo"
    }
}