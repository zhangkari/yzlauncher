package com.yz.books.ui.base

/**
 * @author lilin
 * @time on 2020-01-03 17:09
 */
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class BookType {
    companion object {
        //1-图书分类    2-视频分类    3-音频分类  4-期刊
        const val TYPE_MASSIVE_BOOK = 1
        const val TYPE_VIDEO_BOOK = 2
        const val TYPE_AUDIO_BOOK = 3
        const val TYPE_JOURNAL_BOOK = 4
    }
}