package com.yz.books.ui.journal.bean

import java.io.Serializable

/**
 * @author lilin
 * @time on 2019-12-29 14:56
 */
data class JournalBookDetailBean(val journalDetails: JournalBookDetailInfo?) : Serializable {

    data class JournalBookDetailInfo(
        val author: String,
        val journalId: Int,
        val journalName: String,
        val bookType: String,
        val coverImg: String,
        val fileMd5String: String,
        val qrCode: String,
        val path: String,
        val isCollect: Int,
        val recommend: MutableList<RecommendBooks>?,
        val recommendText: String,
        val content: String?,
        val lateralReader: String? = null
    ) : Serializable

    data class RecommendBooks(
        val author: String,
        val journalId: Int,
        val journalName: String,
        val bookType: String,
        val coverImg: String,
        val path: String,
        val fileMd5String: String,
        val content: String?,
        val lateralReader: String? = null
    ) : Serializable
}