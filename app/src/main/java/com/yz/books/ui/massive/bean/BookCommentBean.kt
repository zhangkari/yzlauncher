package com.yz.books.ui.massive.bean

/**
 * @author lilin
 * @time on 2020-02-22 21:27
 */
class BookCommentBean(val total: Int,
                      val basComments: MutableList<BookCommentInfo>) {

    class BookCommentInfo(val userName: String,
                          val images: String,
                          val content: String)
}