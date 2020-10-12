package com.yz.books.ui.video.bean

/**
 * @author lilin
 * @time on 2020-01-03 10:31
 */
data class VideoBooksBean(val total: Int,
                          val videos: MutableList<VideoBooksInfo>) {
    data class VideoBooksInfo(
        val videoId: Int,
        val videoName: String,
        val description: String,
        val coverImg: String
    )
}