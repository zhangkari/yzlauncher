package com.yz.books.ext

import android.os.Bundle
import android.os.Parcelable
import java.io.Serializable

/**
 * @author lilin
 * @time on 2019-12-17 10:09
 */

/**
 * 部分数据暂不支持
 */
fun Bundle.putValueInBundle(key:String, value:Any){
    when (value) {
        is Int -> putInt(key, value)
        is Byte -> putByte(key, value)
        is Char -> putChar(key, value)
        is Long -> putLong(key, value)
        is Float -> putFloat(key, value)
        is Short -> putShort(key, value)
        is Double -> putDouble(key, value)
        is Boolean -> putBoolean(key, value)
        is Bundle -> putBundle(key, value)
        is String -> putString(key, value)
        is IntArray -> putIntArray(key, value)
        is ByteArray -> putByteArray(key, value)
        is CharArray -> putCharArray(key, value)
        is LongArray -> putLongArray(key, value)
        is FloatArray -> putFloatArray(key, value)
        is Parcelable -> putParcelable(key, value)
        is ShortArray -> putShortArray(key, value)
        is DoubleArray -> putDoubleArray(key, value)
        is BooleanArray -> putBooleanArray(key, value)
        is CharSequence -> putCharSequence(key, value)
        is Serializable -> putSerializable(key, value)
    }

}