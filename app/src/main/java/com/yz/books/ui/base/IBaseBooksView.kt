package com.yz.books.ui.base

import com.yz.books.base.viewmodel.State

/**
 * @author lilin
 * @time on 2019-12-19 15:47
 */
interface IBaseBooksView {
    /**
     * 标题
     * @param title
     */
    fun setTitle(title: String)

    /**
     * UI状态
     * @param state
     */
    fun observerBooksUI(state: State)

    /**
     * 获取分类
     * @param type
     */
    fun getBookCategorys(type: Int)

    /**
     * 获取列表
     */
    fun getBooksList()

    /**
     * 获取详情
     * @param bookId
     */
    fun getBookDetail(bookId: Int)

    /**
     * 图书搜索
     * @param keyWords
     */
    fun searchBook(pageNum: Int, keyWords: String)

    /**
     * 图书评论
     */
    fun getBookComment(pageNum: Int)

    /**
     * 点击列表项
     */
    fun <T>clickBooksAdapterItem(data: T) {}
}