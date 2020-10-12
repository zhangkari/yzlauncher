package com.yz.books.widget.dialog

import android.content.Context
import com.yz.books.R
import com.yz.books.widget.dialog.base.BaseDialog
import kotlinx.android.synthetic.main.dialog_collected_massive_books.*

/**
 * @author lilin
 * @time on 2019-12-17 09:33
 */
class CollectedMassiveBooksDialog(context: Context) : BaseDialog(context) {

    override fun getLayoutId() = R.layout.dialog_collected_massive_books

    override fun initView() {
        super.initView()
    }

    override fun initListener() {
        btn_back.setOnClickListener {
            dismiss()
        }

        btn_previous.setOnClickListener {  }

        btn_next.setOnClickListener {  }
    }

}