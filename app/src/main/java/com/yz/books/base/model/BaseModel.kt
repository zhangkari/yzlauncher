package com.yz.books.base.model

import com.yz.books.api.ApiResponse
import com.yz.books.api.ApiService
import com.yz.books.api.YZBookNetWorkApi

/**
 * 普通model
 *
 * @author lilin
 * @time on 2019-11-02 17:59
 */
abstract class BaseModel : SuperBaseModel() {

    val mApiService by lazy {
        YZBookNetWorkApi.mInstance.getApiService<ApiService>()
    }

    override fun <T> requestData() = ApiResponse<T>(mLifecycleOwner)

}