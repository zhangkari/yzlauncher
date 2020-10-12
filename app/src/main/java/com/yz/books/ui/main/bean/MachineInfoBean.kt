package com.yz.books.ui.main.bean

/**
 * @author lilin
 * @time on 2020-01-14 20:53
 */
data class MachineInfoBean(val id: Int,
                           val createTime: String,
                           val deadlineTime: String,
                           val lastHeartTime: String,
                           val notes: String,
                           val organId: String,
                           val resourceListId: String,
                           val sceneId: String,
                           val status: Int,//0是正常1是禁用
                           val title: String,
                           val updateTime: String,
                           val downloadUpdateTime: Long?) {

    companion object {
        const val AUTHORITY_NORMAL = 0
    }
}