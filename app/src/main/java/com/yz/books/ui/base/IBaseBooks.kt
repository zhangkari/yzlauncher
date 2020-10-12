package com.yz.books.ui.base

/**
 * @author lilin
 * @time on 2019-12-19 15:47
 */
interface IBaseBooks {

    fun getUserInfo()

    /**
     * 获取分类
     * @param type 1  图书分类    2  音频分类    3视频分类
     */
    fun getBookCategorys(type: Int)

    /**
     * 获取书列表
     * @param categoryId 分类id
     * @param pageNum 当前页数
     */
    fun getBooksList(categoryId: Int, pageNum: Int)

    /**
     * 获取书详情
     * @param bookId
     */
    fun getBookDetail(bookId: Int)

    /**
     * 图书搜索
     * @param pageNum
     * @param type
     * @param keyWords
     */
    fun searchBook(pageNum: Int, type: Int, keyWords: String)

    /**
     * 获取图书评论
     * @param pageNum
     * @param bookId
     * @param type
     */
    fun getBookComment(pageNum: Int, bookId: Int, type: Int)
}