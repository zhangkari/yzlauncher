package com.yz.books.base.interf

import androidx.annotation.LayoutRes

/**
 * @author lilin
 * @time on 2019-12-16 21:20
 */
interface IView {
    @LayoutRes
    fun getLayoutId(): Int = 0
    fun initVM()
    fun initView()
    fun initData()
    fun loadData() {}
    fun initListener()
    fun showLoading(tips: Any? = null)
    fun dismissLoading()
    /**
     * 窗口完成以后进行加载
     */
    fun afterWindowViewMeasured() {}
}