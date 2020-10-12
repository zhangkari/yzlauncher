package com.yz.books.base.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.yz.books.R

/**
 * @author lilin
 * @time on 2019-12-16 18:24
 */
abstract class YZBaseAdapter<T: Any>(layoutId: Int,
                                     dataList: MutableList<T>?) :
    BaseQuickAdapter<T, BaseViewHolder>(layoutId, dataList), IYZBaseAdapter {

    abstract fun handleViewData(helper: BaseViewHolder, item: T)

    override fun convert(helper: BaseViewHolder, item: T) {
        handleViewData(helper, item)
    }

    override fun showEmptyView(context: Context?,
                               view: View?,
                               resId: Int?) {
        val viewEmpty = LayoutInflater.from(context).inflate(R.layout.view_empty, null)
        val tvTips = viewEmpty.findViewById<TextView>(R.id.tv_empty_tips)
        resId?.let {
            tvTips.setText(it)
        }

        emptyView = view ?: viewEmpty

    }

    override fun showEmptyView(layoutResId: Int,
                               parentView: ViewGroup) {
        setEmptyView(layoutResId, parentView)
    }
}