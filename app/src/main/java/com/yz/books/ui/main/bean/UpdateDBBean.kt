package com.yz.books.ui.main.bean

/**
 * @author lilin
 * @time on 2020-02-22 13:32
 */
data class UpdateDBBean(val dbUrl: String,
                        val dbName: String,
                        val fileMd5String: String,
                        val isUpdate: Int //是否需要更新0否1是
)