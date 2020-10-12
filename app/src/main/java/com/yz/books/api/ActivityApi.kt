package com.yz.books.api

import okhttp3.Interceptor

class ActivityApi : NetworkApi() {
    override fun getOnLine() = ApiConstant.HOST_ACTIVITY
    override fun getInterceptor(): Interceptor? {
        return null
    }

    inline fun <reified T> getApiService(): T {
        val clazz = T::class.java
        return getRetrofit(clazz).create(clazz)
    }

    companion object {
        @Volatile
        private var sInstance: ActivityApi? = null

        val mInstance: ActivityApi
            get() {
                if (sInstance == null) {
                    synchronized(ActivityApi::class.java) {
                        if (sInstance == null) {
                            sInstance = ActivityApi()
                        }
                    }
                }
                return sInstance!!
            }
    }
}