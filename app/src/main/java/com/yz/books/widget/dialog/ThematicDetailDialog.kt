package com.yz.books.widget.dialog

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import com.yz.books.R
import com.yz.books.adapter.ThematicDetailAdapter
import com.yz.books.adapter.ThematicDetailVideoAdapter
import com.yz.books.common.Constant
import com.yz.books.ext.addFileHostUrl
import com.yz.books.ext.setOnItemClick
import com.yz.books.ui.thematic.bean.ThematicBean
import com.yz.books.ui.thematic.bean.ThematicDetailBean
import com.yz.books.ui.thematic.bean.ThematicType
import com.yz.books.utils.ImageLoaderUtils
import com.yz.books.widget.dialog.base.BaseDialog
import kotlinx.android.synthetic.main.dialog_thematic_detail.*

/**
 * @author lilin
 * @time on 2019-12-17 09:33
 */
class ThematicDetailDialog(context: Context,
                           private val portrait: Boolean) : BaseDialog(context) {

    /**
     * 听书、图书
     */
    private val mThematicDetailAdapter by lazy {
        ThematicDetailAdapter(context, null)
    }

    /**
     * 视频
     */
    private val mThematicDetailVideoAdapter by lazy {
        ThematicDetailVideoAdapter(context, null)
    }

    private var mPageNum = 1
    private var mTotalPages = 1

    private var mThematicInfo: ThematicBean.ThematicInfo? = null

    private var mLoadDetailList: ((categoryId: Int?, thematicType: String?, pageNum: Int) -> Unit)? = null

    private var mClickItem: ((ThematicDetailBean.ThematicDetailInfo) -> Unit)? = null

    override fun getLayoutId() = R.layout.dialog_thematic_detail

    override fun initView() {
        super.initView()
        showCenter()
        //initRecyclerView()

        initData()
    }

    override fun initData() {
        super.initData()

    }

    override fun initListener() {
        btn_back.setOnClickListener {
            dismiss()
        }

        btn_previous.setOnClickListener {
            if (mPageNum > 1) {
                mPageNum--
                mLoadDetailList?.invoke(mThematicInfo?.id, mThematicInfo?.type, mPageNum)
            }
        }

        btn_next.setOnClickListener {
            if (mPageNum < mTotalPages) {
                mPageNum++
                mLoadDetailList?.invoke(mThematicInfo?.id, mThematicInfo?.type, mPageNum)
            }
        }

        mThematicDetailVideoAdapter.setOnItemClick { adapter, view, position ->
            val data = adapter.data[position] as ThematicDetailBean.ThematicDetailInfo
            mClickItem?.invoke(data)
        }

        mThematicDetailAdapter.setOnItemClick { adapter, view, position ->
            val data = adapter.data[position] as ThematicDetailBean.ThematicDetailInfo
            mClickItem?.invoke(data)
        }
    }

    private fun initRecyclerView() {
        /*val manager = GridLayoutManager(context, 3)
        manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (mThematicInfo?.type == ThematicType.TYPE_VIDEO) {
                    2
                } else {
                    3
                }
            }
        }*/

        recycler_view.run {
            adapter = if (portrait) {
                if (mThematicInfo?.type == ThematicType.TYPE_VIDEO) {
                    layoutManager = GridLayoutManager(context, 2)
                    mThematicDetailVideoAdapter
                } else {
                    layoutManager = GridLayoutManager(context, 4)
                    mThematicDetailAdapter
                }
            } else {
                if (mThematicInfo?.type == ThematicType.TYPE_VIDEO) {
                    layoutManager = GridLayoutManager(context, 2)
                    mThematicDetailVideoAdapter
                } else {
                    layoutManager = GridLayoutManager(context, 3)
                    mThematicDetailAdapter
                }
            }
        }
    }

    fun setThematicInfo(thematicInfo: ThematicBean.ThematicInfo?) {
        mThematicInfo = thematicInfo

        initRecyclerView()
    }

    fun setThematicDetailData(thematicDetailBean: ThematicDetailBean?) {
        if (thematicDetailBean == null) {
            return
        }

        val pageSize = if (Constant.SCREEN_ORIENTATION_LANDSCAPE) {
            6
        } else {
            if (mThematicInfo?.type == ThematicType.TYPE_VIDEO) {
                4
            } else {
                8
            }
        }

        val total = thematicDetailBean.total
        val totalPages = if (total % pageSize > 0) {
            total / pageSize + 1
        } else {
            total / pageSize
        }
        mTotalPages = totalPages
        tv_page_info.text = "$mPageNum / $totalPages"

        with(thematicDetailBean) {
            if (mThematicInfo?.type == ThematicType.TYPE_IMAGE) {
                iv_picture.visibility = View.VISIBLE
                group_thematic.visibility = View.GONE
                if (resourcesList.isNotEmpty()) {
                    ImageLoaderUtils.withBookCover(resourcesList[0].coverImg.addFileHostUrl(),
                        iv_picture, ImageView.ScaleType.FIT_XY)
                }
            } else {
                iv_picture.visibility = View.GONE
                group_thematic.visibility = View.VISIBLE
                ImageLoaderUtils.withBookCover(mThematicInfo!!.imgUrl.addFileHostUrl(),
                    iv_thematic_picture, ImageView.ScaleType.FIT_XY)
                if (!portrait) {
                    tv_thematic_theme.text = mThematicInfo!!.title
                    tv_thematic_title.text = mThematicInfo!!.label
                    tv_thematic_desc.text = mThematicInfo!!.content
                }

                if (mThematicInfo?.type == ThematicType.TYPE_VIDEO) {
                    mThematicDetailVideoAdapter.replaceData(resourcesList)
                } else {
                    mThematicDetailAdapter.replaceData(resourcesList)
                }
            }
        }
    }

    fun getThematicDetailList(loadDetailList: (categoryId: Int?, thematicType: String?, pageNum: Int) -> Unit) {
        mLoadDetailList = loadDetailList
    }

    fun getBookDetail(clickItem: (ThematicDetailBean.ThematicDetailInfo) -> Unit) {
        mClickItem = clickItem
    }
}