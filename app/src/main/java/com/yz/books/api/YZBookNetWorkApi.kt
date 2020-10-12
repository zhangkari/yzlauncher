package com.yz.books.api

import okhttp3.Interceptor

/**
 * @author lilin
 * @time on 2019-12-28 19:21
 */
class YZBookNetWorkApi : NetworkApi() {

    override fun getOnLine() = ApiConstant.HOST

    override fun getInterceptor(): Interceptor? {
        return null
    }

    inline fun <reified T> getApiService(): T {
        val clazz = T::class.java
        return getRetrofit(clazz).create(clazz)
    }

    companion object {
        @Volatile
        private var sInstance: YZBookNetWorkApi? = null

        val mInstance: YZBookNetWorkApi
            get() {
                if (sInstance == null) {
                    synchronized(YZBookNetWorkApi::class.java) {
                        if (sInstance == null) {
                            sInstance = YZBookNetWorkApi()
                        }
                    }
                }
                return sInstance!!
            }
    }

}