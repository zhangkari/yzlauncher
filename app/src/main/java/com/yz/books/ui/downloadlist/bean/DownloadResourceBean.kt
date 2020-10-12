package com.yz.books.ui.downloadlist.bean

/**
 * @author lilin
 * @time on 2020/3/23 上午11:37
 */

data class DownloadResourceBean(
    val total: Int,
    val list: MutableList<ResourceInfo>
) {

    data class ResourceInfo(
        val id: Int,
        val resourceId: Int,
        val resourceName: String,
        val resourcePath: String,
        val resourceType: String,
        val type: String,
        val coverImg: String,
        val fileMd5String: String,
        val createTime: String,
        val updateTime: String,
        var progress: Int,
        var started: Boolean,
        var completed: Boolean
    )
}