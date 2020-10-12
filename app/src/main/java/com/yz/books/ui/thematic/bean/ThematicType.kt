package com.yz.books.ui.thematic.bean

/**
 * @author lilin
 * @time on 2020-01-03 17:09
 */
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class ThematicType {
    companion object {
        const val TYPE_BOOK = "book"
        const val TYPE_VIDEO = "video"
        const val TYPE_AUDIO = "audio"
        const val TYPE_IMAGE = "image"
    }
}