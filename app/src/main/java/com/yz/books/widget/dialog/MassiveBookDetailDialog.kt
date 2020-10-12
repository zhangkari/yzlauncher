package com.yz.books.widget.dialog

import android.content.Context
import android.view.KeyEvent
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import com.gofun.zxing.encode.CodeCreator
import com.yz.books.R
import com.yz.books.adapter.MassiveBookDetailAdapter
import com.yz.books.common.Constant
import com.yz.books.ext.addFileHostUrl
import com.yz.books.ext.setCollectStatus
import com.yz.books.ext.setOnItemClick
import com.yz.books.ext.showToast
import com.yz.books.ui.audio.bean.AudioBookDetailBean
import com.yz.books.ui.base.BookType
import com.yz.books.ui.journal.bean.JournalBookDetailBean
import com.yz.books.ui.massive.bean.MassiveBookDetailBean
import com.yz.books.utils.ImageLoaderUtils
import com.yz.books.widget.dialog.base.BaseDialog
import kotlinx.android.synthetic.main.dialog_massive_book_detail.*

/**
 * @author lilin
 * @time on 2019-12-17 09:33
 */
class MassiveBookDetailDialog(
    context: Context,
    private val portrait: Boolean
) : BaseDialog(context) {

    private val mMassiveBookDetailAdapter by lazy(LazyThreadSafetyMode.NONE) {
        MassiveBookDetailAdapter(context, null)
    }

    override fun getLayoutId() = R.layout.dialog_massive_book_detail

    override fun initView() {
        super.initView()
        showCenter()

        initRecyclerView()
    }

    override fun initListener() {
        btn_back.setOnClickListener {
            dismiss()
        }

        btn_done.setOnClickListener {
            mDoneCallback?.invoke()
        }

        tv_collect.setOnClickListener {
            context.showToast("请用手机扫码右侧二维码")
        }

        tv_comment.setOnClickListener {
            mCommentCallback?.invoke()
        }

        mMassiveBookDetailAdapter.setOnItemClick { adapter, view, position ->
            if (mMassiveBookDetailAdapter.mBookType == BookType.TYPE_MASSIVE_BOOK) {
                val data = adapter.data[position] as MassiveBookDetailBean.RecommendBooks
                with(data) {
                    val bookInfo = MassiveBookDetailBean.MassiveBookDetailInfo(
                        author, bookId, bookName, bookType, coverImg, path, fileMd5String,
                        "", 0, null, "", ""
                    )
                    mItemCallback?.invoke(bookInfo)
                }
            } else if (mMassiveBookDetailAdapter.mBookType == BookType.TYPE_JOURNAL_BOOK) {
                val data = adapter.data[position] as JournalBookDetailBean.RecommendBooks
                with(data) {
                    val bookInfo = MassiveBookDetailBean.MassiveBookDetailInfo(
                        author, journalId, journalName, bookType, coverImg, path, fileMd5String,
                        "", 0, null, "", "", lateralReader
                    )
                    mItemCallback?.invoke(bookInfo)
                }
            } else {
                val data = adapter.data[position] as AudioBookDetailBean.RecommendBooks
                with(data) {
                    val bookInfo = AudioBookDetailBean.AudioBookDetailInfo(
                        author,
                        audioId, audioName, "", coverImg, 0, null, ""
                    )
                    mItemCallback?.invoke(bookInfo)
                }
            }
        }
    }

    private fun initRecyclerView() {
        with(recycler_view_books) {
            layoutManager = if (portrait) {
                GridLayoutManager(context, 4)
            } else {
                GridLayoutManager(context, 6)
            }
            adapter = mMassiveBookDetailAdapter
        }
    }

    private var mItemCallback: ((Any) -> Unit)? = null

    fun setOnItemClickCallback(itemCallback: (Any) -> Unit) {
        mItemCallback = itemCallback
    }

    private var mDoneCallback: (() -> Unit)? = null

    fun setOnClickCallback(doneCallback: () -> Unit) {
        mDoneCallback = doneCallback
    }

    private var mCommentCallback: (() -> Unit)? = null

    fun setOnCommentClickCallback(commentCallback: () -> Unit) {
        mCommentCallback = commentCallback
    }

    fun <T> setBookDetailData(
        @BookType bookType: Int,
        bookDetailInfo: T
    ) {
        mMassiveBookDetailAdapter.mBookType = bookType

        if (bookType == BookType.TYPE_MASSIVE_BOOK) {
            btn_done.setBackgroundResource(R.drawable.ic_read)
            with(bookDetailInfo as MassiveBookDetailBean.MassiveBookDetailInfo) {
                ImageLoaderUtils.withBookCover(
                    coverImg.addFileHostUrl(),
                    iv_book_img,
                    ImageView.ScaleType.FIT_XY
                )
                tv_book_name.text = "名 称：$bookName"
                tv_user_name.text = "作 者：$author"
                tv_collect.setCollectStatus(isCollect)
                mMassiveBookDetailAdapter.replaceData(recommend ?: mutableListOf())
                showQrCodeImg(qrCode)
            }
        } else if (bookType == BookType.TYPE_AUDIO_BOOK) {
            btn_done.setBackgroundResource(R.drawable.ic_play_audio)
            with(bookDetailInfo as AudioBookDetailBean.AudioBookDetailInfo) {
                ImageLoaderUtils.withBookCover(
                    coverImg.addFileHostUrl(),
                    iv_book_img,
                    ImageView.ScaleType.FIT_XY
                )
                tv_book_name.text = "名 称：$audioName"
                tv_user_name.text = "作 者：$author"
                tv_collect.setCollectStatus(isCollect)
                mMassiveBookDetailAdapter.replaceData(recommend ?: mutableListOf())
                showQrCodeImg(qrCode)
            }
        } else if (bookType == BookType.TYPE_JOURNAL_BOOK) {
            btn_done.setBackgroundResource(R.drawable.ic_read)
            with(bookDetailInfo as JournalBookDetailBean.JournalBookDetailInfo) {
                ImageLoaderUtils.withBookCover(
                    coverImg.addFileHostUrl(),
                    iv_book_img,
                    ImageView.ScaleType.FIT_XY
                )
                tv_book_name.text = "名 称：$journalName"
                tv_user_name.text = "作 者：$author"
                tv_collect.setCollectStatus(isCollect)
                mMassiveBookDetailAdapter.replaceData(recommend ?: mutableListOf())
                showQrCodeImg(qrCode)
            }
        }
    }

    /**
     * 二维码
     * @param qrCode
     */
    private fun showQrCodeImg(qrCode: String) {
        val size = if (Constant.LOW_SYSTEM) {
            if (Constant.SCREEN_ORIENTATION_LANDSCAPE) {
                150
            } else {
                50
            }
        } else {
            150
        }
        val qrCodeBitmap = CodeCreator.createQRCode(qrCode, size, size, null)
        iv_qrcode.setImageBitmap(qrCodeBitmap)
    }
}