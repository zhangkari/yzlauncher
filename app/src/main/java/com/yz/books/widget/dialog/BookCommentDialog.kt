package com.yz.books.widget.dialog

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import com.yz.books.R
import com.yz.books.adapter.BookCommentAdapter
import com.yz.books.ui.massive.bean.BookCommentBean
import com.yz.books.widget.dialog.base.BaseDialog
import kotlinx.android.synthetic.main.dialog_book_comment.*

/**
 * @author lilin
 * @time on 2019-12-17 09:33
 */
class BookCommentDialog(context: Context,
                        val callback: (Int) -> Unit) : BaseDialog(context) {

    private var mTotalPages = 1
    private var mPageNum = 1

    private val mBookCommentAdapter by lazy(LazyThreadSafetyMode.NONE) {
        BookCommentAdapter(context, null)
    }

    override fun getLayoutId() = R.layout.dialog_book_comment

    override fun initView() {
        super.initView()
        showCenter()

        recycler_view_books.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mBookCommentAdapter
        }
    }

    override fun initListener() {
        btn_back.setOnClickListener {
            dismiss()
        }

        btn_previous.setOnClickListener {
            if (mPageNum > 1) {
                mPageNum--

                callback(mPageNum)
            }
        }

        btn_next.setOnClickListener {
            if (mPageNum < mTotalPages) {
                mPageNum++

                callback(mPageNum)
            }
        }
    }

    fun setBookCommentData(bookCommentBean: BookCommentBean?) {
        mBookCommentAdapter.replaceData(bookCommentBean?.basComments ?: mutableListOf())

        val total = bookCommentBean?.total ?: 0
        val totalPages = if (total % 6 > 0) {
            total / 6 + 1
        } else {
            total / 6
        }
        tv_page_info.text = "$mPageNum / $totalPages"
        mTotalPages = totalPages
    }
}