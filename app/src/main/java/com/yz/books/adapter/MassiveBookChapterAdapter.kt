package com.yz.books.adapter

import android.content.Context
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseViewHolder
import com.yz.books.R
import com.yz.books.base.adapter.YZBaseAdapter

/**
 * @author lilin
 * @time on 2019-12-16 19:53
 */
class MassiveBookChapterAdapter(val context: Context, dataList: MutableList<String>?) :
    YZBaseAdapter<String>(R.layout.adapter_item_massive_book_chapter, dataList) {

    override fun handleViewData(helper: BaseViewHolder, item: String) {
        helper.itemView.isFocusable = true
        helper.convertView.isFocusable = true

        val tvName = helper.getView<TextView>(R.id.tv_name)
        if (helper.layoutPosition % 2 == 0) {
            tvName.setBackgroundColor(ContextCompat.getColor(context, R.color.cC4C2E9))
        } else {
            tvName.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
        }

        helper.setText(R.id.tv_name, item)
    }

}