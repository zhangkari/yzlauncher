package com.yz.books.base.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup

/**
 * @author lilin
 * @time on 2019-12-16 19:49
 */
interface IYZBaseAdapter {

    //fun showEmptyView()

    fun showEmptyView(context: Context?, view: View? = null, resId: Int? = null)

    fun showEmptyView(layoutResId: Int, parentView: ViewGroup)

}