package com.yz.books.ui.video.bean

/**
 * @author lilin
 * @time on 2020-01-12 19:53
 */
data class VideoBookChaptersBean(val total: Int,
                                 val videoChapter: MutableList<VideoBookChaptersInfo>) {
    data class VideoBookChaptersInfo(val chapterId: Int,
                                     val chapterName: String,
                                     val chapterUrl: String,
                                     val fileMd5String: String,
                                     var isPlaying: Boolean,
                                     var downloaded: Boolean,
                                     var localPath: String)
}