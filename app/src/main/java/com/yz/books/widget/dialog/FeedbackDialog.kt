package com.yz.books.widget.dialog

import android.content.Context
import com.yz.books.R
import com.yz.books.ext.textContent
import com.yz.books.utils.textWatcher
import com.yz.books.widget.dialog.base.BaseDialog
import kotlinx.android.synthetic.main.dialog_feedback.*

/**
 * @author lilin
 * @time on 2019-12-17 09:33
 */
class FeedbackDialog(context: Context) : BaseDialog(context) {

    private var mContent: String
        get() = et_feedback.textContent
        set(value) = et_feedback.setText(value)

    override fun getLayoutId() = R.layout.dialog_feedback

    override fun initView() {
        super.initView()
        btn_sure.isEnabled = false
    }

    override fun initListener() {
        btn_cancel.setOnClickListener {
            dismiss()
        }

        btn_sure.setOnClickListener {
            dismiss()
        }

        et_feedback.textWatcher {
            afterTextChanged {
                btn_sure.isEnabled = mContent.isNotEmpty()
            }
        }
    }

    override fun show() {
        super.show()
        mContent = ""
    }

}