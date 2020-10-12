package com.yz.books.ui.journal.bean

/**
 * @author lilin
 * @time on 2019-12-28 19:00
 */
data class JournalBooksBean(val total: Int,
                            val journals: MutableList<JournalBooksInfo>) {
    data class JournalBooksInfo(
        val coverImg: String,
        val journalId: Int,
        val journalName: String
    )
}