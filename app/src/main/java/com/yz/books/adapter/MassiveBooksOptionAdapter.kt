package com.yz.books.adapter

import android.content.Context
import com.chad.library.adapter.base.BaseViewHolder
import com.yz.books.R
import com.yz.books.base.adapter.YZBaseAdapter
import com.yz.books.ui.massive.bean.BookCategorysBean

/**
 * @author lilin
 * @time on 2019-12-16 19:53
 */
class MassiveBooksOptionAdapter(context: Context, dataList: MutableList<BookCategorysBean.BookCategorysInfo>?) :
    YZBaseAdapter<BookCategorysBean.BookCategorysInfo>(R.layout.adapter_item_massive_books_option, dataList) {

    override fun handleViewData(helper: BaseViewHolder, item: BookCategorysBean.BookCategorysInfo) {
        helper.itemView.isFocusable = true
        helper.convertView.isFocusable = true

        helper.itemView.isSelected = helper.layoutPosition == mPosition

        helper.setGone(R.id.view_line, helper.itemView.isSelected)

        helper.setText(R.id.tv_option_name, item.categorysName)
    }

    private var mPosition = 0

    fun setSelectedPosition(position: Int) {
        mPosition = position
        notifyDataSetChanged()
    }

}