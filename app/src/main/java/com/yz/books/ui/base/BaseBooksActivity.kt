package com.yz.books.ui.base

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.yz.books.R
import com.yz.books.adapter.MassiveBooksAdapter
import com.yz.books.adapter.MassiveBooksOptionAdapter
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
import com.yz.books.ui.audio.bean.AudioBookDetailBean
import com.yz.books.ui.audio.bean.AudioBooksBean
import com.yz.books.ui.audio.player.AudioPlayerActivity
import com.yz.books.ui.journal.bean.JournalBooksBean
import com.yz.books.ui.massive.bean.BookCategorysBean
import com.yz.books.ui.massive.bean.BookCommentBean
import com.yz.books.ui.massive.bean.MassiveBookDetailBean
import com.yz.books.ui.massive.bean.MassiveBooksBean
import com.yz.books.ui.massive.readbook.DocTypeEnum
import com.yz.books.ui.massive.readbook.EpubReadBookActivity
import com.yz.books.ui.massive.readbook.PDFReadBookActivity
import com.yz.books.ui.video.player.VideoPlayerActivity
import com.yz.books.ui.video.player.VideoPlayerActivityx
import com.yz.books.utils.LogUtils
import com.yz.books.widget.dialog.BookCommentDialog
import com.yz.books.widget.dialog.BookSearchDialog
import com.yz.books.widget.dialog.MassiveBookDetailDialog
import com.yz.books.widget.pop.MenuPopupWindow
import kotlinx.android.synthetic.main.activity_massive_books.*

/**
 * 书基类
 *
 * @author lilin
 * @time on 2019-12-16 18:05
 */
abstract class BaseBooksActivity<T : BaseBooksViewModel> : BaseMVVMActivity<T>(), IBaseBooksView {

//region var/val

    companion object {
        //1-图书分类    2-音频分类    3-视频分类  4-期刊
        const val TYPE_MASSIVE_BOOK = BookType.TYPE_MASSIVE_BOOK
        const val TYPE_AUDIO_BOOK = BookType.TYPE_AUDIO_BOOK
        const val TYPE_VIDEO_BOOK = BookType.TYPE_VIDEO_BOOK
        const val TYPE_JOURNAL_BOOK = BookType.TYPE_JOURNAL_BOOK
    }

    /**
     * 类型
     */
    protected var mBookType = TYPE_MASSIVE_BOOK

    private var mPageNum = 1
    private var mTotalPages = 1

    private var mBookId: Int? = null

    /**
     * 分类id
     */
    private var mCategorysId: Int? = null

    private val mMenuPopupWindow by lazy(LazyThreadSafetyMode.NONE) {
        MenuPopupWindow(this)
    }

    private val mMassiveBooksOptionAdapter by lazy(LazyThreadSafetyMode.NONE) {
        MassiveBooksOptionAdapter(this, null)
    }

    private val mMassiveBooksAdapter by lazy(LazyThreadSafetyMode.NONE) {
        MassiveBooksAdapter(this, null)
    }

    val mMassiveBookDetailDialog by lazy(LazyThreadSafetyMode.NONE) {
        MassiveBookDetailDialog(
            this,
            resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        )
    }

    private val mBookCommentDialog by lazy(LazyThreadSafetyMode.NONE) {
        BookCommentDialog(this) {
            getBookComment(it)
        }
    }

    private val mBookSearchDialog by lazy(LazyThreadSafetyMode.NONE) {
        BookSearchDialog(
            this,
            resources.configuration.orientation ==
                    Configuration.ORIENTATION_PORTRAIT
        ) { pageNum, keyWords ->
            searchBook(pageNum, keyWords)
        }
    }

    private var mBookComment: BookCommentBean? = null

//endregion

//region implement methods

    override fun onDestroy() {
        super.onDestroy()
        mMenuPopupWindow.dismiss()
        mMassiveBookDetailDialog.dismissDialog()
        mBookSearchDialog.dismissDialog()
        mBookCommentDialog.dismissDialog()
    }

    //override fun providerVMClass() = MassiveBooksViewModel()

    override fun getLayoutId() = R.layout.activity_massive_books

    override fun initView() {
        initOptionRecyclerView()
        initBooksRecyclerView()
    }

    override fun initData() {

    }

    override fun afterWindowViewMeasured() {
        super.afterWindowViewMeasured()
        getUserInfo()
    }

    override fun initListener() {
        btn_back.setOnClickListener {
            finish()
        }

        tv_user_name.setOnClickListener {
            mMenuPopupWindow.showAtBottom(it)
        }

        tv_search.setOnClickListener {
            mBookSearchDialog.show()
        }

        btn_previous.setOnClickListener {
            if (mPageNum > 1) {
                mPageNum--
                getBooksList()
            }
        }

        btn_next.setOnClickListener {
            if (mPageNum < mTotalPages) {
                mPageNum++
                getBooksList()
            }
        }

        mMassiveBooksOptionAdapter.setOnItemClick { adapter, view, position ->
            mMassiveBooksOptionAdapter.setSelectedPosition(position)
            mCategorysId = mMassiveBooksOptionAdapter.data[position].categorysId

            mPageNum = 1
            mTotalPages = 1
            getBooksList()
        }

        mMassiveBooksAdapter.setOnItemClick { adapter, _, position ->
            clickBooksAdapterItem(adapter.data[position])

            val bookId = when (mMassiveBooksAdapter.mBookType) {
                BookType.TYPE_MASSIVE_BOOK -> {
                    (adapter.data[position] as MassiveBooksBean.MassiveBooksInfo).bookId
                }
                BookType.TYPE_JOURNAL_BOOK -> {
                    (adapter.data[position] as JournalBooksBean.JournalBooksInfo).journalId
                }
                BookType.TYPE_AUDIO_BOOK -> {
                    (adapter.data[position] as AudioBooksBean.AudioBooksInfo).audioId
                }
                else -> {
                    -1
                }
            }

            if (bookId == -1) {
                return@setOnItemClick
            }
            getBookDetail(bookId)
        }

        mMassiveBookDetailDialog.setOnCommentClickCallback {
            mBookCommentDialog.show()
            mBookCommentDialog.setBookCommentData(mBookComment)
        }

        mBookSearchDialog.setOnItemClick { type, item ->
            when (type) {
                BookType.TYPE_MASSIVE_BOOK -> {
                    with(item) {
                        val bookInfo = MassiveBookDetailBean.MassiveBookDetailInfo(
                            author ?: "", bookId ?: -1, bookName ?: "",
                            bookType ?: "", coverImg ?: "", path ?: "", fileMd5String ?: "",
                            "", 0, null, "", ""
                        )
                        if (bookType == DocTypeEnum.EPUB.name.toLowerCase()) {
                            startToActivity<EpubReadBookActivity>(
                                Constant.MASSIVE_BOOK_INFO_KEY_EXTRA to bookInfo
                            )
                        } else {
                            startToActivity<PDFReadBookActivity>(
                                Constant.MASSIVE_BOOK_INFO_KEY_EXTRA to bookInfo
                            )
                        }
                    }
                }
                BookType.TYPE_AUDIO_BOOK -> {
                    with(item) {
                        val data = AudioBookDetailBean.AudioBookDetailInfo(
                            author ?: "",
                            audioId ?: -1, audioName ?: "", "", coverImg ?: "", 0, null, ""
                        )
                        startToActivity<AudioPlayerActivity>(
                            Constant.AUDIO_BOOK_DETAIL_KEY_EXTRA to data
                        )
                    }
                }
                BookType.TYPE_JOURNAL_BOOK -> {
                    with(item) {
                        val bookInfo = MassiveBookDetailBean.MassiveBookDetailInfo(
                            author ?: "", journalId ?: -1, journalName ?: "",
                            bookType ?: "", coverImg ?: "", path ?: "", fileMd5String ?: "", "",
                            0, null, "", ""
                        )
                        startToActivity<PDFReadBookActivity>(
                            Constant.MASSIVE_BOOK_INFO_KEY_EXTRA to bookInfo
                        )
                    }
                }
                else -> {
                    with(item) {
                        val videoInfo = "${videoId},${videoName}"
                        if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                            || Constant.LOW_SYSTEM
                        ) {
                            startToActivity<VideoPlayerActivity>(Constant.VIDEO_INFO_KEY_EXTRA to videoInfo)
                        } else {
                            startToActivity<VideoPlayerActivityx>(Constant.VIDEO_INFO_KEY_EXTRA to videoInfo)
                        }
                    }
                }
            }
        }
    }

    override fun observerForever() = false

    override fun observerUI(state: State) {
        //LogUtils.e("state==$state")
        when (state) {
            is LoadingState -> {
                showLoading()
            }
            is LoadedState -> {
                dismissLoading()
            }
            is ErrorState -> {
                showToast(state.errorMsg)
            }
            is BaseBooksModel.UserInfoState -> {
                LogUtils.e("用户信息==${state.name}")
            }
            is BaseBooksModel.BookCategorysState -> {
                setBooksOptionData(state.bookCategorysBean?.bookCategorys)
            }
            is BaseBooksModel.SearchBookState -> {
                mBookSearchDialog.setBooksData(state.type, state.bookBean)
            }
            is BaseBooksModel.BookCommentState -> {
                mBookComment = state.bookCommentBean
            }
        }

        //2、各业务数据分发
        observerBooksUI(state)
    }

    override fun setTitle(title: String) {
        tv_title.text = title
    }

    override fun getBookCategorys(type: Int) {
        mBookType = type
        mViewModel?.getBookCategorys(type)
    }

    override fun getBooksList() {
        mMassiveBooksAdapter.replaceData(mutableListOf())
        group_page_info.visibility = View.GONE

        //LogUtils.e("mCategorysId==$mCategorysId")
        mCategorysId?.let {
            mViewModel?.getBooksList(it, mPageNum)
        }
    }

    override fun getBookDetail(bookId: Int) {
        mViewModel?.getBookDetail(bookId)

        getBookComment(1)
    }

    override fun searchBook(pageNum: Int, keyWords: String) {
        mViewModel?.searchBook(pageNum, mBookType, keyWords)
    }

    override fun getBookComment(pageNum: Int) {
        mBookId?.let {
            mViewModel?.getBookComment(pageNum, it, mBookType)
        }
    }

//endregion

//region public methods

    /**
     * 页码信息
     * @param total
     */
    fun setPageInfo(total: Int?, type: Int? = null) {
        val currentPages = if (type == BookType.TYPE_VIDEO_BOOK) {
            6
        } else {
            12
        }
        if (total == null || total == 0) {
            group_page_info.visibility = View.GONE
            return
        }
        val totalPages = if (total % currentPages > 0) {
            total / currentPages + 1
        } else {
            total / currentPages
        }
        mTotalPages = totalPages
        group_page_info.visibility = View.VISIBLE
        tv_page_info.text = "$mPageNum / $totalPages"
    }

    /**
     * 列表数据
     * @param bookType
     * @param list
     */
    fun <E> setBooksData(@BookType bookType: Int, list: MutableList<E>?) {
        mMassiveBooksAdapter.mBookType = bookType
        mMassiveBooksAdapter.replaceData(list ?: mutableListOf())
    }

//endregion

//region private methods

    private fun initOptionRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        } else {
            linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        }
        recycler_view_option.layoutManager = linearLayoutManager
        recycler_view_option.adapter = mMassiveBooksOptionAdapter
    }

    private fun initBooksRecyclerView() {
        val num = if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (TYPE_VIDEO_BOOK == mBookType) {
                2
            } else {
                4
            }
        } else {
            if (TYPE_VIDEO_BOOK == mBookType) {
                3
            } else {
                6
            }
        }
        recycler_view_books.layoutManager = GridLayoutManager(this, num)
        recycler_view_books.adapter = mMassiveBooksAdapter
    }

    private fun getUserInfo() {
        //mVMRouter.toTarget(VMRouterConstant.VM_GETUSERINFO)
    }

    private fun setBooksOptionData(
        list:
        MutableList<BookCategorysBean.BookCategorysInfo>?
    ) {
        mMassiveBooksOptionAdapter.replaceData(list ?: mutableListOf())

        val categorysId = if (list != null && list.isNotEmpty()) {
            list[0].categorysId
        } else {
            null
        }
        mCategorysId = categorysId
        getBooksList()
    }

//endregion
}