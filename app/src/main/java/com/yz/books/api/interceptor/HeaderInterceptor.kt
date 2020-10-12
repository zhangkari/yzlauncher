package com.yz.books.api.interceptor

import com.yz.books.utils.LogUtils
import com.yz.books.utils.sp.PreferencesUtils
import com.yz.books.utils.sp.SpConstant
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okio.Buffer
import java.io.IOException

/**
 * @author lilin
 * @time on 2019-12-28 20:32
 */
class HeaderInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        try {
            val originalRequest = chain.request()

            return originalRequest.run {

                val requestBuilder = initHeader()

                //initUrlParams(requestBuilder)
                //initBody(requestBuilder)

                val response = chain.proceed(requestBuilder.build())
                /*val responseBody = response.peekBody((1024 * 1024).toLong())
                val body = responseBody.string()
                LogUtils.e("intercept==${body}")*/
                response
            }
            //return chain.proceed(request)
        } catch (e: Throwable) {
            if (e is IOException) {
                throw e
            } else {
                throw IOException(e)
            }
        }
    }

    private fun Request.initHeader(): Request.Builder {
        val method = this.method
        if(method == "GET") {
            return initHeaderParams()
        }

        //post
        return initHeaderParams()
            .header("Content-Type", "application/x-www-form-urlencoded")

        /*return newBuilder()
            //.header("Content-Type", "application/json;charset=UTF-8")
            //.method(method(), body())
            .post(bodyToString(body).toRequestBody("application/json;charset=UTF-8".toMediaTypeOrNull()))*///post提交json格式数据
    }

    /**
     * header中设置公共参数
     */
    private fun Request.initHeaderParams(): Request.Builder {
        return newBuilder()
            .header("machineCode", PreferencesUtils.getSpValue(SpConstant.MACHINE_CODE,""))
            .header("organId", PreferencesUtils.getSpValue(SpConstant.ORGAN_ID,""))
    }

    private fun bodyToString(request: RequestBody?): String {
        try {
            val buffer = Buffer()
            if (request != null) {
                request.writeTo(buffer)
            } else {
                return ""
            }
            return buffer.readUtf8()
        } catch (e: IOException) {
            return "did not work"
        }
    }
}