package com.yz.books.widget.pop

import android.app.Activity
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.yz.books.R
import com.yz.books.ext.dismissDialog
import com.yz.books.widget.dialog.CollectedMassiveBooksDialog
import com.yz.books.widget.dialog.MyCommentDialog
import com.yz.books.widget.dialog.FeedbackDialog
import kotlin.math.abs


/**
 * @author lilin
 * @time on 2019-12-16 15:41
 */
class MenuPopupWindow(context: Activity) : BasePopupWindow(context) {

    private val mCommentDialog by lazy(LazyThreadSafetyMode.NONE) {
        MyCommentDialog(context)
    }

    private val mFeedbackDialog by lazy(LazyThreadSafetyMode.NONE) {
        FeedbackDialog(context)
    }

    private val mCollectedMassiveBooksDialog by lazy(LazyThreadSafetyMode.NONE) {
        CollectedMassiveBooksDialog(context)
    }

    private lateinit var tvComment: TextView
    private lateinit var tvFeedback: TextView
    private lateinit var tvCollected: TextView

    override fun dismiss() {
        super.dismiss()
        mCommentDialog.dismissDialog()
        mFeedbackDialog.dismissDialog()
        mCollectedMassiveBooksDialog.dismissDialog()
    }

    override fun initView() {
        val popView = LayoutInflater.from(context).inflate(R.layout.view_pop, null)
        contentView = popView

        tvComment = popView.findViewById(R.id.tv_comment)
        tvFeedback = popView.findViewById(R.id.tv_help)
        tvCollected = popView.findViewById(R.id.tv_collected)
    }

    override fun initListener() {
        tvComment.setOnClickListener {
            mCommentDialog.show()
        }

        tvFeedback.setOnClickListener {
            mFeedbackDialog.show()
        }

        tvCollected.setOnClickListener {
            mCollectedMassiveBooksDialog.show()
        }
    }

    /**
     * 基于控件底部展示
     * @param view
     */
    fun showAtBottom(view: View) {
        val xoff = abs(contentView.measuredWidth - view.width) / 2
        showAsDropDown(view, -xoff, 0, Gravity.START)
    }

}