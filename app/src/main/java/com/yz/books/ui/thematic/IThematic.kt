package com.yz.books.ui.thematic

import retrofit2.http.FieldMap

/**
 * @author lilin
 * @time on 2020/3/15 下午3:58
 */

interface IThematic {

    /**
     * 获取专题列表
     */
    fun getThematicList(specialTopicCategoryId: Int, pageNum: Int)

    /**
     * 获取专题详情
     */
    fun getThematicDetail(categoryId: Int, thematicType: String, pageNum: Int)

    /**
     * 获取书详情
     * @param thematicType
     * @param bookId
     */
    fun getBookDetail(thematicType: String, bookId: Int)
}