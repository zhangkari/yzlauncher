package com.yz.books.ui.massive.bean

import java.io.Serializable

/**
 * @author lilin
 * @time on 2019-12-29 14:56
 */
data class MassiveBookDetailBean(val bokDetails: MassiveBookDetailInfo?) : Serializable {

    data class MassiveBookDetailInfo(
        val author: String?,
        val bookId: Int,
        val bookName: String,
        val bookType: String?,
        val coverImg: String,
        val path: String?,
        val fileMd5String: String?,
        val qrCode: String,
        val isCollect: Int,
        val recommend: MutableList<RecommendBooks>?,
        val recommendText: String,
        val content: String?,
        val lateralReader: String? = null
    ) : Serializable

    data class RecommendBooks(
        val author: String?,
        val bookId: Int,
        val bookName: String,
        val bookType: String?,
        val coverImg: String,
        val path: String?,
        val fileMd5String: String?,
        val content: String?
    ) : Serializable
}