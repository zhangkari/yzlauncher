package com.yz.books.ui.massive.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @author lilin
 * @time on 2020/5/5 下午9:36
 */

@Parcelize
data class JournalBookParamsBean(
    var relative_path: String,
    val content: String?,
    val coverImg: String,
    val journalId: Int,
    val journalName: String,
    val success: Boolean,
    val message: String,
    val fileMd5String: String?,
    val path: String?,
    val lateralReader: String?
): Parcelable