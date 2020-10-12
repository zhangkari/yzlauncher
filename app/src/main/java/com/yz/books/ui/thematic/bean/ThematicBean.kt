package com.yz.books.ui.thematic.bean

/**
 * @author lilin
 * @time on 2020/3/15 下午4:08
 */
data class ThematicBean(val total: Int,
                        val specialTopicsList: MutableList<ThematicInfo>) {

    data class ThematicInfo(val categoryId: Int,
                            val id: Int,
                            val content: String,
                            val imgUrl: String,
                            val label: String,
                            val status: Int,
                            val title: String,
                            val type: String)
}