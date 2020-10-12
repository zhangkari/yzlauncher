package com.yz.books.api

import androidx.collection.ArrayMap
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.yz.books.BuildConfig
import com.yz.books.api.interceptor.HeaderInterceptor
import com.yz.books.api.interceptor.LoggingInterceptor
import com.yz.books.utils.sp.PreferencesUtils
import com.yz.books.utils.sp.SpConstant
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * @author lilin
 * @time on 2019-12-28 19:08
 */
abstract class NetworkApi {

    private val mRetrofitArray = ArrayMap<String, Retrofit>()
    private var mBaseUrl = "http://yuezhi.ngo100.com"
    private var mOkHttpClient: OkHttpClient? = null

    init {
        mBaseUrl = getOnLine()
    }

    /**
     * 获取retrofit对象
     * @param service api接口
     */
    fun getRetrofit(service: Class<*>): Retrofit {
        var retrofit = mRetrofitArray[mBaseUrl + service.name]

        return retrofit ?: initRetrofit().also {
            //mRetrofitArray[mBaseUrl + service.name] = it
            retrofit = it
        }
    }

    private fun initRetrofit(): Retrofit {
        return Retrofit.Builder().apply {
            baseUrl(mBaseUrl)
            client(getOkHttpClient())
            addCallAdapterFactory(CoroutineCallAdapterFactory())
            addConverterFactory(GsonConverterFactory.create())
        }.build()
    }

    private val loggingInterceptor: LoggingInterceptor
        get() {
            return LoggingInterceptor.Builder()
                .loggable(BuildConfig.DEBUG)
                .request()
                .requestTag("Request")
                .response()
                .responseTag("Response")
                .hideVerticalLine()// 隐藏竖线边框
                .build()
        }

    private fun getOkHttpClient(): OkHttpClient {
        if (mOkHttpClient == null) {
            OkHttpClient.Builder().apply {
                if (getInterceptor() != null) {
                    addInterceptor(getInterceptor()!!)
                }
                if (BuildConfig.DEBUG) {
                    //addInterceptor(loggingInterceptor) //日志,所有的请求响应度看到
                }
                val logging = HttpLoggingInterceptor()
                if (BuildConfig.DEBUG) {
                    logging.level = HttpLoggingInterceptor.Level.BODY
                } else {
                    logging.level = HttpLoggingInterceptor.Level.BASIC
                }
                addInterceptor(logging)
                addInterceptor(HeaderInterceptor())
                addInterceptor(addQueryParameterInterceptor())
                //okHttpClientBuilder.addInterceptor(CommonRequestInterceptor(mINetworkRequiredInfo))
                //addInterceptor(CommonResponseInterceptor())
                connectTimeout(40L, TimeUnit.SECONDS)
                readTimeout(40L, TimeUnit.SECONDS)
                writeTimeout(40L, TimeUnit.SECONDS)
                mOkHttpClient = build()
            }
        }
        return mOkHttpClient!!
    }

    /**
     * 设置公共参数
     */
    private fun addQueryParameterInterceptor(): Interceptor {
        return Interceptor { chain ->
            val originalRequest = chain.request()
            val request: Request
            val modifiedUrl = originalRequest.url.newBuilder()
                // Provide your custom parameter here
                .addQueryParameter("organId", PreferencesUtils.getSpValue(SpConstant.ORGAN_ID, ""))
                .build()
            request = originalRequest.newBuilder().url(modifiedUrl).build()
            chain.proceed(request)
        }
    }

    protected abstract fun getOnLine(): String

    protected abstract fun getInterceptor(): Interceptor?
}