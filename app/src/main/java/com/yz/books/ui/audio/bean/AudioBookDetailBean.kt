package com.yz.books.ui.audio.bean

import java.io.Serializable

/**
 * @author lilin
 * @time on 2019-12-29 14:56
 */
data class AudioBookDetailBean(val audioDetails: AudioBookDetailInfo?): Serializable {

    data class AudioBookDetailInfo(val author: String,
                                     val audioId: Int,
                                     val audioName: String,
                                    val qrCode: String,
                                     val coverImg: String,
                                     val isCollect: Int,
                                     val recommend: MutableList<RecommendBooks>?,
                                     val recommendText: String): Serializable

    data class RecommendBooks(val author: String,
                              val audioId: Int,
                              val audioName: String,
                              val coverImg: String): Serializable
}