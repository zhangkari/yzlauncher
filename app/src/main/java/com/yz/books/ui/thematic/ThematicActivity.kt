package com.yz.books.ui.thematic

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Build
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.yz.books.R
import com.yz.books.adapter.ThematicAdapter
import com.yz.books.base.activity.BaseMVVMActivity
import com.yz.books.base.viewmodel.ErrorState
import com.yz.books.base.viewmodel.LoadedState
import com.yz.books.base.viewmodel.LoadingState
import com.yz.books.base.viewmodel.State
import com.yz.books.common.Constant
import com.yz.books.ext.dismissDialog
import com.yz.books.ext.setOnItemClick
import com.yz.books.ext.showToast
import com.yz.books.ext.startToActivity
import com.yz.books.ui.audio.AudioBooksModel
import com.yz.books.ui.audio.bean.AudioBookDetailBean
import com.yz.books.ui.audio.player.AudioPlayerActivity
import com.yz.books.ui.base.BookType
import com.yz.books.ui.massive.MassiveBooksModel
import com.yz.books.ui.massive.bean.MassiveBookDetailBean
import com.yz.books.ui.massive.readbook.DocTypeEnum
import com.yz.books.ui.massive.readbook.EpubReadBookActivity
import com.yz.books.ui.massive.readbook.PDFReadBookActivity
import com.yz.books.ui.thematic.bean.ThematicBean
import com.yz.books.ui.thematic.bean.ThematicDetailBean
import com.yz.books.ui.thematic.bean.ThematicType
import com.yz.books.ui.video.player.VideoPlayerActivity
import com.yz.books.ui.video.player.VideoPlayerActivityx
import com.yz.books.widget.dialog.MassiveBookDetailDialog
import com.yz.books.widget.dialog.ThematicDetailDialog
import kotlinx.android.synthetic.main.activity_thematic.*
import java.util.*

/**
 * 专题系列
 *
 * @author lilin
 * @time on 2019-12-20 09:41
 */
class ThematicActivity : BaseMVVMActivity<ThematicViewModel>() {

//region var/val

    private var mPageNum = 1
    private var mTotalPages = 1

    private val mThematicAdapter by lazy(LazyThreadSafetyMode.NONE) {
        ThematicAdapter(this, null)
    }

    private var mThematicDetailDialog: ThematicDetailDialog? = null

    private var mThematicInfo: ThematicBean.ThematicInfo? = null

    private var mThematicId: Int? = null

    private val mMassiveBookDetailDialog by lazy(LazyThreadSafetyMode.NONE) {
        MassiveBookDetailDialog(this,
            resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
    }

//endregion

//region implement methods

    override fun onDestroy() {
        super.onDestroy()
        mThematicDetailDialog?.dismissDialog()
        mMassiveBookDetailDialog.dismiss()
    }

    override fun providerVMClass() = ThematicViewModel()

    override fun getLayoutId() = R.layout.activity_thematic

    override fun initView() {
        initRecyclerView()
    }

    override fun initData() {
        mThematicId = intent?.extras?.getInt(Constant.THEMATIC_ID_KEY_EXTRA)
        getThematicList()
    }

    override fun initListener() {
        super.initListener()
        btn_back.setOnClickListener { finish() }

        mThematicAdapter.setOnItemClick { adapter, _, position ->
            val data = adapter.data[position] as ThematicBean.ThematicInfo
            mThematicDetailDialog = null
            mThematicInfo = data
            getThematicDetail(data.id, data.type, 1)
        }

        btn_previous.setOnClickListener {
            if (mPageNum > 1) {
                mPageNum--
                getThematicList()
            }
        }

        btn_next.setOnClickListener {
            if (mPageNum < mTotalPages) {
                mPageNum++
                getThematicList()
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
            is ThematicModel.ThematicListState -> {
                showThematicListData(state.thematicBean)
            }
            is ThematicModel.ThematicDetailState -> {
                showThematicDetailData(state.thematicDetailBean)
            }
            is AudioBooksModel.AudioBookDetailState -> {
                //goToAudioPlayerPage(state.audioBookDetailBean)
                setMassiveBookDetailData(state.audioBookDetailBean)
            }

            is MassiveBooksModel.MassiveBookDetailState -> {
                //goToReadBookPage(state.massiveBookDetailBean)
                setMassiveBookDetailData(state.massiveBookDetailBean)
            }
        }
    }

//endregion

//region public methods

//endregion

//region private methods

    private fun initRecyclerView() {
        recycler_view_thematic.run {
            layoutManager = if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                LinearLayoutManager(this@ThematicActivity)
            } else {
                GridLayoutManager(this@ThematicActivity, 3)
            }
            adapter = mThematicAdapter
        }
    }

    private fun getThematicList() {
        mThematicId?.let {
            mViewModel?.getThematicList(it, mPageNum)
        }
    }

    private fun getThematicDetail(categoryId: Int?, thematicType: String, pageNum: Int) {
        categoryId?.let {
            mViewModel?.getThematicDetail(categoryId, thematicType, pageNum)
        }
    }

    private fun getBookDetail(thematicType: String, bookId: Int) {
        mViewModel?.getBookDetail(thematicType, bookId)
    }

    private fun showThematicListData(thematicBean: ThematicBean?) {
        if (thematicBean == null) {
            return
        }
        mThematicAdapter.replaceData(thematicBean.specialTopicsList)

        val total = thematicBean.total
        val totalPages = if (total % 6 > 0) {
            total / 6 + 1
        } else {
            total / 6
        }
        mTotalPages = totalPages
        tv_page_info.text = "$mPageNum / $totalPages"
    }

    private fun showThematicDetailData(thematicDetailBean: ThematicDetailBean?) {
        if (mThematicDetailDialog == null) {
            mThematicDetailDialog = ThematicDetailDialog(
                this,
                resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
            )
            mThematicDetailDialog?.show()
            mThematicDetailDialog?.setThematicInfo(mThematicInfo)
        }
        mThematicDetailDialog?.setThematicDetailData(thematicDetailBean)
        mThematicDetailDialog?.getThematicDetailList { categoryId, thematicType, pageNum ->
            getThematicDetail(categoryId, thematicType ?: ThematicType.TYPE_VIDEO, pageNum)
        }
        mThematicDetailDialog?.getBookDetail {
            if (it.type == ThematicType.TYPE_VIDEO) {
                val videoInfo = "${it.resourceId},${it.resourceName}"
                if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    || Constant.LOW_SYSTEM) {
                    startToActivity<VideoPlayerActivity>(Constant.VIDEO_INFO_KEY_EXTRA to videoInfo)
                } else {
                    startToActivity<VideoPlayerActivityx>(Constant.VIDEO_INFO_KEY_EXTRA to videoInfo)
                }
                //startToActivity<VideoPlayerActivity>(Constant.VIDEO_INFO_KEY_EXTRA to videoInfo)
            } else {
                getBookDetail(it.type, it.resourceId)
            }
        }
    }

    private fun goToReadBookPage(bookDetailBean: MassiveBookDetailBean?) {
        if (bookDetailBean?.bokDetails == null) {
            showToast("数据错误！")
            return
        }
        with(bookDetailBean.bokDetails) {
            if (bookType == DocTypeEnum.EPUB.name.toLowerCase()) {
                startToActivity<EpubReadBookActivity>(
                    Constant.MASSIVE_BOOK_INFO_KEY_EXTRA to this)
            } else {
                startToActivity<PDFReadBookActivity>(
                    Constant.MASSIVE_BOOK_INFO_KEY_EXTRA to this)
            }
        }
    }

    private fun goToAudioPlayerPage(audioBookDetailBean: AudioBookDetailBean?) {
        if (audioBookDetailBean?.audioDetails == null) {
            showToast("数据错误！")
            return
        }
        with(audioBookDetailBean.audioDetails) {
            startToActivity<AudioPlayerActivity>(
                Constant.AUDIO_BOOK_DETAIL_KEY_EXTRA to this)
        }
    }

    private fun setMassiveBookDetailData(bookDetailBean: MassiveBookDetailBean?) {
        if (bookDetailBean?.bokDetails == null) {
            showToast("数据错误！")
            return
        }
        with(mMassiveBookDetailDialog) {
            show()
            setBookDetailData(BookType.TYPE_MASSIVE_BOOK, bookDetailBean.bokDetails)
            setOnClickCallback {
                //LogUtils.e("type==${DocTypeEnum.EPUB.name.toLowerCase()}")
                if (bookDetailBean.bokDetails.bookType == DocTypeEnum.EPUB.name.toLowerCase()) {
                    startToActivity<EpubReadBookActivity>(
                        Constant.MASSIVE_BOOK_INFO_KEY_EXTRA to bookDetailBean.bokDetails)
                } else {
                    startToActivity<PDFReadBookActivity>(
                        Constant.MASSIVE_BOOK_INFO_KEY_EXTRA to bookDetailBean.bokDetails)
                }
            }
            setOnItemClickCallback {
                val data = it as MassiveBookDetailBean.MassiveBookDetailInfo
                getBookDetail(ThematicType.TYPE_BOOK, data.bookId)
            }
        }
    }

    private fun setMassiveBookDetailData(bookDetailBean: AudioBookDetailBean?) {
        if (bookDetailBean?.audioDetails == null) {
            showToast("数据错误！")
            return
        }
        with(mMassiveBookDetailDialog) {
            dismiss()
            show()
            setBookDetailData(BookType.TYPE_AUDIO_BOOK, bookDetailBean.audioDetails)
            setOnClickCallback {
                startToActivity<AudioPlayerActivity>(
                    Constant.AUDIO_BOOK_DETAIL_KEY_EXTRA to bookDetailBean.audioDetails)
            }
            setOnItemClickCallback {
                val data = it as AudioBookDetailBean.AudioBookDetailInfo
                getBookDetail(ThematicType.TYPE_AUDIO, data.audioId)
            }
        }
    }

//endregion
}