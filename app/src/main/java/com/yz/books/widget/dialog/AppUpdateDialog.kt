package com.yz.books.widget.dialog

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import com.yz.books.R
import com.yz.books.adapter.BookCommentAdapter
import com.yz.books.ui.massive.bean.BookCommentBean
import com.yz.books.widget.dialog.base.BaseDialog
import kotlinx.android.synthetic.main.dialog_app_update.*
import kotlinx.android.synthetic.main.dialog_book_comment.*

/**
 * @author lilin
 * @time on 2019-12-17 09:33
 */
class AppUpdateDialog(context: Context,
                      val callback: (Boolean) -> Unit) : BaseDialog(context) {

    override fun getLayoutId() = R.layout.dialog_app_update

    override fun initView() {
        super.initView()
        showCenter()
    }

    override fun initListener() {
        btn_cancel.setOnClickListener {
            callback(false)
            dismiss()
        }

        btn_sure.setOnClickListener {
            callback(true)
            dismiss()
        }
    }

    fun showUpdateDesc(desc: String) {
        tv_app_update_desc.text = "$desc\n\n确定更新app？"
    }
}