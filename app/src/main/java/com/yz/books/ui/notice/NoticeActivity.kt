package com.yz.books.ui.notice

import android.content.res.AssetManager
import android.content.res.Configuration
import android.content.res.Resources
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.yz.books.R
import com.yz.books.adapter.NoticeAdapter
import com.yz.books.base.activity.BaseMVVMActivity
import com.yz.books.base.viewmodel.ErrorState
import com.yz.books.base.viewmodel.LoadedState
import com.yz.books.base.viewmodel.LoadingState
import com.yz.books.base.viewmodel.State
import com.yz.books.common.Constant
import com.yz.books.ext.addFileHostUrl
import com.yz.books.ext.setOnItemClick
import com.yz.books.ext.showToast
import com.yz.books.ui.notice.bean.NoticeDetailBean
import com.yz.books.ui.notice.bean.NoticesBean
import com.yz.books.utils.FileUtils
import com.yz.books.utils.HtmlUtils
import com.yz.books.utils.LogUtils
import kotlinx.android.synthetic.main.activity_notice.*

/**
 * 公告
 *
 * @author lilin
 * @time on 2020-01-20 16:06
 */
class NoticeActivity : BaseMVVMActivity<NoticeViewModel>() {

//region var/val

    private var mPageNum = 1
    private var mTotalPages = 1

    private val mNoticeAdapter by lazy(LazyThreadSafetyMode.NONE) {
        NoticeAdapter(this, null)
    }

//endregion

//region implement methods

    override fun getAssets(): AssetManager {
        return resources.assets
    }

    override fun providerVMClass() = NoticeViewModel()

    override fun getLayoutId() = R.layout.activity_notice

    override fun initView() {
        web_view.setBackgroundColor(0)
        web_view.background.alpha = 0
        initRecyclerView()
    }

    override fun initData() {
        getNotices()
    }

    override fun initListener() {
        super.initListener()
        btn_back.setOnClickListener {
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                if (tv_notice_title.visibility == View.VISIBLE) {
                    tv_notice_title.visibility = View.GONE
                    web_view.visibility = View.GONE
                    recycler_view_notice.visibility = View.VISIBLE
                    return@setOnClickListener
                }
            }
            finish()
        }

        mNoticeAdapter.setOnItemClick { adapter, view, position ->
            val data = adapter.data[position] as NoticesBean.NoticeInfo
            getNoticeDetail(data.articlesId)
        }

        btn_previous.setOnClickListener {
            if (mPageNum > 1) {
                mPageNum--
                getNotices()
            }
        }

        btn_next.setOnClickListener {
            if (mPageNum < mTotalPages) {
                mPageNum++
                getNotices()
            }
        }
    }

    override fun observerForever() = false

    override fun observerUI(state: State) {
        when(state) {
            is LoadingState -> {
                showLoading()
            }
            is LoadedState -> {
                dismissLoading()
            }
            is ErrorState -> {
                showToast(state.errorMsg)
            }

            is NoticeModel.NoticesState -> {
                showNoticesData(state.noticesBean)
            }

            is NoticeModel.NoticeDetailState -> {
                showNoticeDetailData(state.noticeDetailBean)
            }
        }
    }

//endregion

//region public methods

//endregion

//region private methods

    private fun initRecyclerView() {
        recycler_view_notice.apply {
            layoutManager = LinearLayoutManager(this@NoticeActivity)
            adapter = mNoticeAdapter
        }
    }

    private fun getNotices() {
        mViewModel?.getNotices(mPageNum)
    }

    private fun getNoticeDetail(noticeId: Int) {
        mViewModel?.getNoticeDetail(noticeId)
    }

    private fun showNoticesData(noticesBean: NoticesBean?) {
        val total = noticesBean?.total ?: 0
        val totalPages = if (total % 10 > 0) {
            total / 10 + 1
        } else {
            total / 10
        }
        mTotalPages = totalPages
        tv_page_info.text = "$mPageNum / $totalPages"

        val list = noticesBean?.articles
        mNoticeAdapter.replaceData(list ?: mutableListOf())

        if (!Constant.SCREEN_ORIENTATION_LANDSCAPE) {
            return
        }

        if (list != null && !list.isNullOrEmpty()) {
            getNoticeDetail(list[0].articlesId)
        }
    }

    private fun showNoticeDetailData(noticeDetailBean: NoticeDetailBean?) {
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            recycler_view_notice.visibility = View.GONE
            tv_notice_title.visibility = View.VISIBLE
            web_view.visibility = View.VISIBLE
        }

        tv_notice_title.text = noticeDetailBean?.articlesName
        LogUtils.e("html==${noticeDetailBean?.content}")
        val content = noticeDetailBean?.content
        /*if (content.contains("<img") && !Constant.ONLINE_VERSION) {
            content = HtmlUtils.getAbsSource(content, imgPath)
            LogUtils.e("html-replace==$content")
        }*/

        //val data = "<img src=\"upload/image/1591269599416502a.jpg\" alt=\"\">"
        val imgPath = if (!Constant.ONLINE_VERSION) {
            "file://"+ FileUtils.getLocalPath()
        } else {
            null
        }
        web_view.loadDataWithBaseURL(imgPath, content, "text/html", "UTF-8", null)
    }

//endregion
}