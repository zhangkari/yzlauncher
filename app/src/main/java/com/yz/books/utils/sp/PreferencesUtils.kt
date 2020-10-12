package com.yz.books.utils.sp

import android.content.Context
import android.content.SharedPreferences
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.URLDecoder
import java.net.URLEncoder

/**
 * @author lilin
 * @time on 2020-01-14 21:06
 */
object PreferencesUtils {

    private lateinit var mPreferences: SharedPreferences

    /**
     * init在application中
     */
    fun init(context: Context) {
        mPreferences = context.getSharedPreferences(
            context.packageName + SpConstant.SHARED_NAME, Context.MODE_PRIVATE
        )
    }

    /**
     * SharedPreferences extension function, so we won't need to call edit()
    and apply()
     * ourselves on every SharedPreferences operation.
     */
    private inline fun SharedPreferences.edit(operation: SharedPreferences.Editor.() -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    private fun <T> serialize(obj: T): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val objectOutputStream = ObjectOutputStream(
            byteArrayOutputStream
        )
        objectOutputStream.writeObject(obj)
        var serStr = byteArrayOutputStream.toString("ISO-8859-1")
        serStr = URLEncoder.encode(serStr, "UTF-8")
        objectOutputStream.close()
        byteArrayOutputStream.close()
        return serStr
    }

    private fun <T> deSerialization(str: String?): T {
        val redStr = URLDecoder.decode(str, "UTF-8")
        val byteArrayInputStream = ByteArrayInputStream(
            redStr.toByteArray(charset("ISO-8859-1"))
        )
        val objectInputStream = ObjectInputStream(
            byteArrayInputStream
        )
        val obj = objectInputStream.readObject() as T
        objectInputStream.close()
        byteArrayInputStream.close()
        return obj
    }

    fun <T>putSpValue(key: String, value: T) {
        mPreferences.edit {
            when (value) {
                is Long -> putLong(key, value)
                is String -> putString(key, value)
                is Int -> putInt(key, value)
                is Boolean -> putBoolean(key, value)
                is Float -> putFloat(key, value)
                else -> putString(key, serialize(value))
            }
        }
    }

    fun <T>getSpValue(key: String, default: T): T {
        return mPreferences.run {
            val result = when (default) {
                is Long -> getLong(key, default)
                is String -> getString(key, default)
                is Int -> getInt(key, default)
                is Boolean -> getBoolean(key, default)
                is Float -> getFloat(key, default)
                else -> deSerialization(getString(key, serialize(default)))
            }
            result as T
        }
    }
}