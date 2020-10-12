package com.yz.books.ui.massive.bean

/**
 * @author lilin
 * @time on 2019-12-28 19:00
 */
data class MassiveBooksBean(val total: Int,
                            val books: MutableList<MassiveBooksInfo>) {
    data class MassiveBooksInfo(
        val bookId: Int,
        val bookName: String,
        val coverImg: String
    )
}