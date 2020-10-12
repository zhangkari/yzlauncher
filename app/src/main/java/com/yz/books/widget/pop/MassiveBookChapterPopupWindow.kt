package com.yz.books.widget.pop

import android.app.Activity
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yz.books.R
import com.yz.books.adapter.MassiveBookChapterAdapter
import com.yz.books.ext.dp2px
import com.yz.books.ext.setOnItemClick
import com.yz.books.widget.focuslayout.FocusLayout
import kotlin.math.abs


/**
 * @author lilin
 * @time on 2019-12-16 15:41
 */
class MassiveBookChapterPopupWindow(
    context: Activity,
    val clickItem: (Int) -> Unit
) : BasePopupWindow(context) {

    private lateinit var btnClose: ImageButton
    private lateinit var btnPrevious: ImageButton
    private lateinit var btnNext: ImageButton
    private lateinit var tvPageInfo: TextView
    private lateinit var recyclerViewChapters: RecyclerView
    private lateinit var groupPageInfo: Group

    private var mTotalPages = 1
    private var mPageNum = 1

    private lateinit var mMassiveBookChapterAdapter: MassiveBookChapterAdapter

    private lateinit var mFocusLayout: FocusLayout

    override fun initView() {
        val popView =
            LayoutInflater.from(context).inflate(R.layout.view_massive_book_chapter_pop, null)
        contentView = popView

        mFocusLayout = contentView.findViewById(R.id.focusLayout);

        btnClose = popView.findViewById(R.id.btn_close)
        btnPrevious = popView.findViewById(R.id.btn_previous)
        btnNext = popView.findViewById(R.id.btn_next)
        tvPageInfo = popView.findViewById(R.id.tv_page_info)
        recyclerViewChapters = popView.findViewById(R.id.recycler_view_chapters)
        groupPageInfo = popView.findViewById(R.id.group_page_info)

        initRecyclerView()
    }

    override fun initListener() {
        btnClose.setOnClickListener { dismiss() }

        btnPrevious.setOnClickListener {
            if (mPageNum > 0) {
                mPageNum--
            }
        }

        btnNext.setOnClickListener {
            if (mPageNum < mTotalPages) {
                mPageNum++
            }
        }

        mMassiveBookChapterAdapter.setOnItemClick { adapter, view, position ->
            clickItem(position)
            dismiss()
        }
    }

    private fun initRecyclerView() {
        mMassiveBookChapterAdapter = MassiveBookChapterAdapter(context, null)
        recyclerViewChapters.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mMassiveBookChapterAdapter
        }
    }

    /**
     * 基于控件底部展示
     * @param view
     */
    fun showAtBottom(view: View) {
        contentView.viewTreeObserver.addOnGlobalFocusChangeListener(mFocusLayout)

        val xoff = abs(contentView.measuredWidth - view.width) / 2
        val yoff = abs(contentView.measuredHeight + view.height) + context.dp2px(6)
        showAsDropDown(view, -xoff, -yoff, Gravity.START)
    }

    override fun dismiss() {
        contentView.viewTreeObserver.removeOnGlobalFocusChangeListener(mFocusLayout)
        super.dismiss()
    }

    fun setChaptersData(chapters: MutableList<String>) {
        /*val total = chapters.size
        mTotalPages = if (total % 11 > 0) {
            total / 11 + 1
        } else {
            total / 11
        }
        if (mTotalPages <= 1) {
            groupPageInfo.visibility = View.GONE
        } else {
            groupPageInfo.visibility = View.VISIBLE
        }*/

        mMassiveBookChapterAdapter.replaceData(chapters)
    }

}