package com.yz.books.ui.audio.bean

/**
 * @author lilin
 * @time on 2020-01-03 10:31
 */
data class AudioBooksBean(val total: Int,
                          val audios: MutableList<AudioBooksInfo>) {
    data class AudioBooksInfo(
        val audioId: Int,
        val audioName: String,
        val coverImg: String
    )
}