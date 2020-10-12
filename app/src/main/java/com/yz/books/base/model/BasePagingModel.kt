package com.yz.books.base.model

import com.yz.books.api.ApiResponse
import com.yz.books.api.ApiService
import com.yz.books.api.YZBookNetWorkApi

/**
 * 带加载更多的model
 *
 * @author lilin
 * @time on 2019-11-02 17:59
 */
abstract class BasePagingModel : SuperBaseModel() {

    /**
     * 刷新
     */
    protected var mIsRefresh = true
    /**
     * 初始页数
     */
    protected var mPageNum = 1

    /**
     * 刷新
     */
    //protected abstract fun refresh()

    /**
     * 加载
     */
    //protected abstract fun load()

    val mApiService by lazy {
        YZBookNetWorkApi.mInstance.getApiService<ApiService>()
    }

    override fun <T> requestData() = ApiResponse<T>(mLifecycleOwner)
}