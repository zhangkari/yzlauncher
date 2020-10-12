package com.yz.books.ext

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.yz.books.base.adapter.YZBaseAdapter

/**
 * @author lilin
 * @time on 2019-12-16 20:03
 */

fun YZBaseAdapter<*>.setOnItemClick(click: (adapter: BaseQuickAdapter<*,*>,
                                            view: View, position: Int) -> Unit) {

    setOnItemClickListener { adapter, view, position ->
        if (position >= adapter.data.size) {
            return@setOnItemClickListener
        }
        click(adapter, view, position)
    }
}

fun YZBaseAdapter<*>.setOnItemChildClick(click: (adapter: BaseQuickAdapter<*,*>,
                                            view: View, position: Int) -> Unit) {

    setOnItemChildClickListener { adapter, view, position ->
        if (position >= adapter.data.size) {
            return@setOnItemChildClickListener
        }
        click(adapter, view, position)
    }
}