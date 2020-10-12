package com.yz.books.ui.audio.bean

/**
 * @author lilin
 * @time on 2020-01-13 08:01
 */
data class AudioBookChaptersBean(val total: Int,
                                 val audioChapters: MutableList<AudioBookChapterInfo>) {
    data class AudioBookChapterInfo(val chapterId: Int,
                                    val chapterName: String,
                                    val chapterUrl: String,
                                    val fileMd5String: String,
                                    var isPlaying: Boolean,
                                    var downloaded: Boolean,
                                    var localPath: String)
}