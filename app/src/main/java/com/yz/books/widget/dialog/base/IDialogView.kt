package com.yz.books.widget.dialog.base

import androidx.annotation.LayoutRes

/**
 * @author lilin
 * @time on 2019-12-17 09:31
 */
interface IDialogView {

    /**
     * 是否底部弹出
     */
    fun isBottomDialog() = false

    @LayoutRes
    fun getLayoutId(): Int = 0

    fun initView()
    fun initData() {}
    fun initListener()
}