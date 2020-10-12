package com.yz.books.widget.dialog

import android.content.Context
import com.yz.books.R
import com.yz.books.widget.dialog.base.BaseDialog
import kotlinx.android.synthetic.main.dialog_tips.*

/**
 * @author lilin
 * @time on 2019-12-17 09:33
 */
class TipsDialog(context: Context, val sureTap: (Boolean) -> Unit) : BaseDialog(context) {

    override fun getLayoutId() = R.layout.dialog_tips

    override fun initView() {
        super.initView()
        showCenter()
    }

    override fun initListener() {
        btn_cancel.setOnClickListener {
            sureTap(false)
            dismiss()
        }

        btn_sure.setOnClickListener {
            sureTap(true)
            dismiss()
        }
    }

    fun showDesc(desc: String) {
        tv_desc.text = desc
    }

}