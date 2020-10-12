package com.yz.books.ui.notice.bean

/**
 * @author lilin
 * @time on 2020-01-20 16:22
 */
data class NoticesBean(val total: Int,
                       val articles: MutableList<NoticeInfo>) {
    data class NoticeInfo(val articlesId: Int,
                          val articlesName: String)
}