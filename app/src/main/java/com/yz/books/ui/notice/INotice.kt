package com.yz.books.ui.notice

/**
 * @author lilin
 * @time on 2020-01-20 16:19
 */
interface INotice {
    /**
     * 获取公告列表
     */
    fun getNotices(pageNum: Int)

    /**
     * 获取公告详情
     */
    fun getNoticeDetail(noticeId: Int)
}