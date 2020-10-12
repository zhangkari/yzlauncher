package com.yz.books.ui.massive.bean

/**
 * @author lilin
 * @time on 2019-12-28 19:00
 */
data class BookCategorysBean(val bookCategorys: MutableList<BookCategorysInfo>) {
    data class BookCategorysInfo(val categorysId: Int,
                                 val categorysName: String)
}