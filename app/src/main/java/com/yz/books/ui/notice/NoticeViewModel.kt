package com.yz.books.ui.notice

import com.yz.books.base.viewmodel.BaseViewModel

/**
 * @author lilin
 * @time on 2020-01-20 16:05
 */
class NoticeViewModel : BaseViewModel(), INotice {

    private val mModel by lazy(LazyThreadSafetyMode.NONE) {
        NoticeModel().apply {
            init(mLifecycleOwner)
            bindLiveData(_mGlobalState)
        }
    }

    override fun getNotices(pageNum: Int) {
        mModel.getNotices(pageNum)
    }

    override fun getNoticeDetail(noticeId: Int) {
        mModel.getNoticeDetail(noticeId)
    }

}