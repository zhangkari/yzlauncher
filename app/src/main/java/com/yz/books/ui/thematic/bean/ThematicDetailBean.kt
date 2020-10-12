package com.yz.books.ui.thematic.bean

/**
 * @author lilin
 * @time on 2020/3/16 下午11:14
 */

data class ThematicDetailBean(val total: Int,
                              val resourcesList: MutableList<ThematicDetailInfo>) {

    data class ThematicDetailInfo(val author: String,
                                  val coverImg: String,
                                  val resourceId: Int,
                                  val resourceName: String,
                                  val type: String)
}