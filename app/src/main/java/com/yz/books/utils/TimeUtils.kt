package com.yz.books.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * @author lilin
 * @time on 2019-12-16 16:58
 */
object TimeUtils {

    const val PATTERN_HMS = "HH:mm:ss"

    /**
     * 获取当前时
     * @param pattern 格式
     * @param time
     */
    fun getCurrentTime(pattern: String, time: Long): String {
        val sdf = SimpleDateFormat(pattern, Locale.CHINA)
        return sdf.format(Date(time))
    }

}