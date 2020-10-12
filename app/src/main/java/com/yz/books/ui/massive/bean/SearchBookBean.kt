package com.yz.books.ui.massive.bean

/**
 * @author lilin
 * @time on 2020-02-04 09:45
 */
data class SearchBookBean(val total: Int,
                          val result: MutableList<SearchBookBeanInfo>) {
    data class SearchBookBeanInfo(val author: String?,
                                  val bookId: Int?,
                                  val bookName: String?,
                                  val bookType: String?,
                                  val coverImg: String?,
                                  val path: String?,
                                  val fileMd5String: String?,
                                  val audioId: Int?,
                                  val audioName: String?,
                                  val journalId: Int?,
                                  val journalName: String?,
                                  val videoId: Int?,
                                  val videoName: String?,
                                  val description: String?)
}
