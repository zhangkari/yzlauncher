package com.yz.books.api.exception

import com.google.gson.JsonParseException
import com.yz.books.utils.LogUtils
import kotlinx.coroutines.CancellationException
import org.json.JSONException
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.ParseException

/**
 * @author lilin
 * @time on 2020-01-20 17:44
 */
class ExceptionHandle {

    companion object {
        private var errorCode = ErrorStatus.UNKNOWN_ERROR
        private var errorMsg = "请求失败，请稍后重试"

        fun handleException(e: Throwable): String {
            LogUtils.e("接口异常==$e")
            //e.printStackTrace()
            if (e is SocketTimeoutException) {//网络超时
                LogUtils.e("网络连接异常: " + e.message)
                errorMsg = "网络连接异常"
                errorCode = ErrorStatus.NETWORK_ERROR
            } else if (e is ConnectException) { //均视为网络错误
                LogUtils.e("网络连接异常: " + e.message)
                errorMsg = "网络连接异常"
                errorCode = ErrorStatus.NETWORK_ERROR
            } else if (e is JsonParseException
                || e is JSONException
                || e is ParseException
            ) {   //均视为解析错误
                LogUtils.e("数据解析异常: " + e.message)
                errorMsg = "数据解析异常"
                errorCode = ErrorStatus.SERVER_ERROR
            } else if(e is HttpException) {
                errorMsg = convertStatusCode(e)
                errorCode = e.code()
            }else if (e is RuntimeException) {//服务器返回的错误信息
                errorMsg = e.message.toString()
                errorCode = ErrorStatus.SERVER_ERROR
            } else if (e is UnknownHostException) {
                LogUtils.e("网络连接异常: " + e.message)
                errorMsg = "网络连接异常"
                errorCode = ErrorStatus.NETWORK_ERROR
            } else if (e is IllegalArgumentException) {
                errorMsg = "参数错误"
                errorCode = ErrorStatus.SERVER_ERROR
            } else if (e is CancellationException) { // 协程取消
                errorMsg = ""
            } else {//未知错误
                try {
                    LogUtils.e("错误: " + e.message)
                } catch (e1: Exception) {
                    LogUtils.e("未知错误Debug调试 ")
                }
                errorMsg = e.message ?: "未知错误，可能抛锚了吧~"
                //errorMsg = "未知错误，可能抛锚了吧~"
                errorCode = ErrorStatus.UNKNOWN_ERROR
            }
            return errorMsg
        }

        private fun convertStatusCode(httpException: HttpException): String {
            return when {
                httpException.code() == 500 -> "服务器发生错误"
                httpException.code() == 404 -> "请求地址不存在"
                httpException.code() == 403 -> "请求被服务器拒绝"
                httpException.code() == 401 -> ""//"未授权"
                httpException.code() == 307 -> "请求被重定向到其他页面"
                else -> httpException.message()
            }
        }
    }
}