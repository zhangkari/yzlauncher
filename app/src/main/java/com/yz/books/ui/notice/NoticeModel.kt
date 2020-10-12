package com.yz.books.ui.notice

import androidx.lifecycle.MutableLiveData
import com.yz.books.base.model.BaseModel
import com.yz.books.base.viewmodel.ErrorState
import com.yz.books.base.viewmodel.LoadedState
import com.yz.books.base.viewmodel.LoadingState
import com.yz.books.base.viewmodel.State
import com.yz.books.common.Constant
import com.yz.books.db.DaoHelper
import com.yz.books.ui.notice.bean.NoticeDetailBean
import com.yz.books.ui.notice.bean.NoticesBean

/**
 * @author lilin
 * @time on 2020-01-20 16:05
 */
class NoticeModel : BaseModel(), INotice {

    override fun bindLiveData(state: MutableLiveData<State>) {
        mBaseState = state
    }

    class NoticesState(val noticesBean: NoticesBean?): State()
    class NoticeDetailState(val noticeDetailBean: NoticeDetailBean?): State()

    override fun getNotices(pageNum: Int) {
        if (!Constant.ONLINE_VERSION) {
            val data = DaoHelper.getNotices(pageNum)
            mBaseState.value = NoticesState(data)
            return
        }

        requestData<NoticesBean>().apply {
            loading {
                mBaseState.value = LoadingState()
            }

            loaded {
                mBaseState.value = LoadedState()
            }

            fail { code, message ->
                mBaseState.value = ErrorState(message, code)
            }

            success { data, _ ->
                mBaseState.value = NoticesState(data)
            }

            request {
                mApiService.getNotices(pageNum, 10)
            }
        }
    }

    override fun getNoticeDetail(noticeId: Int) {
        if (!Constant.ONLINE_VERSION) {
            val data = DaoHelper.getNoticeDetail(noticeId)
            mBaseState.value = NoticeDetailState(data)
            return
        }

        requestData<NoticeDetailBean>().apply {
            loading {
                mBaseState.value = LoadingState()
            }

            loaded {
                mBaseState.value = LoadedState()
            }

            fail { code, message ->
                mBaseState.value = ErrorState(message, code)
            }

            success { data, _ ->
                mBaseState.value = NoticeDetailState(data)
            }

            request {
                mApiService.getNoticeDetail(noticeId)
            }
        }
    }
}