package com.yz.books.adapter

import android.content.Context
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseViewHolder
import com.yz.books.R
import com.yz.books.base.adapter.YZBaseAdapter
import com.yz.books.ui.notice.bean.NoticesBean

/**
 * @author lilin
 * @time on 2019-12-16 19:53
 */
class NoticeAdapter(val context: Context, dataList: MutableList<NoticesBean.NoticeInfo>?) :
    YZBaseAdapter<NoticesBean.NoticeInfo>(R.layout.adapter_item_notice, dataList) {

    override fun handleViewData(helper: BaseViewHolder, item: NoticesBean.NoticeInfo) {
        helper.itemView.isFocusable = true
        helper.convertView.isFocusable = true

        val tvName = helper.getView<TextView>(R.id.tv_desc)
        if (helper.layoutPosition % 2 == 0) {
            tvName.setBackgroundColor(ContextCompat.getColor(context, R.color.cC4C2E9))
        } else {
            tvName.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
        }

        helper.setText(R.id.tv_desc, item.articlesName)
    }

}