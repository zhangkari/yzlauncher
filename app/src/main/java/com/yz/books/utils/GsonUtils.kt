package com.yz.books.utils

import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener

/**
 * Gson工具类
 *
 * @author lilin
 * @time on 2018/11/22 下午3:09
 */
object GsonUtils {

    @Throws(Exception::class)
    fun toJson(any: Any) = Gson().toJson(any)

    @Throws(Exception::class)
    inline fun <reified T> fromJson(json: String): T {
       return Gson().fromJson(json, T::class.java)
    }

    /**
     * 判断一个字符串是否为json格式
     * @param json
     * @return
     */
    @JvmStatic
    fun isJson(json: String): Boolean {
        return try {
            when (JSONTokener(json).nextValue()) {
                is JSONObject -> {
                    JSONObject(json)
                    true
                }
                is JSONArray -> {
                    JSONArray(json)
                    true
                }
                else -> false
            }
        } catch (e: JSONException) {
            //e.printStackTrace();
            false
        }
    }
}