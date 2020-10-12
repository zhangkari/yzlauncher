package com.yz.books.adapter

import android.content.Context
import com.chad.library.adapter.base.BaseViewHolder
import com.yz.books.R
import com.yz.books.base.adapter.YZBaseAdapter

/**
 * @author lilin
 * @time on 2019-12-16 19:53
 */
class CollectedMassiveBooksAdapter(context: Context, dataList: MutableList<String>?) :
    YZBaseAdapter<String>(R.layout.adapter_item_collected_massive_books, dataList) {

    override fun handleViewData(helper: BaseViewHolder, item: String) {

    }

}