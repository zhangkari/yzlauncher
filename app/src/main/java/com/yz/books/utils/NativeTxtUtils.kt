package com.yz.books.utils

import com.yz.books.common.Constant
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

/**
 * @author lilin
 * @time on 2020/4/22 上午11:39
 */

class NativeTxtUtils private constructor() {

    private object XMLUtilsHolder {
        val INSTANCE = NativeTxtUtils()
    }

    companion object {
        fun getInstance(): NativeTxtUtils {
            return XMLUtilsHolder.INSTANCE
        }
    }

    /**
     * @param key 关键字
     * @return String型结果
     */
    fun getStringXmlValue(key: String): String? {
        return getStringValue(key)
    }

    /**
     * 解析readNative.txt文件
     *
     * @param key 键名
     * @return 键值
     */
    private fun getStringValue(key: String): String? {
        var res: String? = null
        val file = File(Constant.READ_NATIVE_PATH)
        if (!file.exists()) {
            LogUtils.e("readNative不存在")
            return Constant.ROOT_PATH
        }
        val reader: BufferedReader
        try {
            //以行为单位读取文件内容，一次读一整行
            reader = BufferedReader(FileReader(file))
            var tempString: String
            // 一次读入一行，直到读入null为文件结束
            while (reader.readLine().also { tempString = it } != null) {
                val linesArr = tempString.split("=").toTypedArray()
                /**
                 * 如果该行有等号(=)，且至少有一个等号，则取该行等号后的内容作为值
                 */
                if (linesArr.size >= 2) {
                    val keyStr = linesArr[0].trim { it <= ' ' }
                    val value = tempString.substring(tempString.indexOf("=") + 1)
                    if (keyStr == key) {
                        res = value
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return res
    }

}