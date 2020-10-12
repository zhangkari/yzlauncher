package com.yz.books.api

import com.yz.books.base.bean.BaseBean

/**
 * @author lilin
 * @time on 2019-12-16 09:53
 */
interface IApiResponse<T> {

    /**
     * 加载中
     * @param onLoading
     */
    fun loading(onLoading: (() -> Unit))

    /**
     * 加载完成
     * @param onLoaded
     */
    fun loaded(onLoaded: (() -> Unit))

    /**
     * 成功
     * @param onSuccess
     */
    fun success(onSuccess: ((result: T?, message: String) -> Unit))

    /**
     * 失败
     * @param onFail
     */
    fun fail(onFail: ((code: Int, message: String) -> Unit))

    /**
     * 接口请求
     * @param result
     */
    fun request(result: suspend () -> BaseBean<T?>)
}