package com.yz.books.api

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.yz.books.api.exception.ErrorStatus
import com.yz.books.api.exception.ExceptionHandle
import com.yz.books.base.bean.BaseBean
import com.yz.books.utils.LogUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

/**
 * 网络请求
 *
 * @author lilin
 * @time on 2019-11-02 20:02
 */
class ApiResponse<T> constructor(private var owner: LifecycleOwner?): IApiResponse<T> {

    override fun request(result: suspend () -> BaseBean<T?>) {
        var job: Job? = null
        var errorMsg = ""

        try {
            owner?.apply {
                job = lifecycleScope.launchWhenStarted {
                    withContext(UI) {
                        onLoading?.invoke()
                    }

                    val response: BaseBean<T?>? = try {
                        withContext(IO) {
                            withTimeoutOrNull(40_000) {
                                result.invoke()
                            }
                        }
                    } catch (e: Exception) {
                        errorMsg = ExceptionHandle.handleException(e)
                        null
                    }
                    LogUtils.e("response==$response")

                    if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                        withContext(UI) {
                            //LogUtils.e("response111==$response")
                            if (response?.code == ErrorStatus.SUCCESS) {
                                onSuccess?.invoke(response.data, response.message)
                                onLoaded?.invoke()
                            } else {
                                onFail?.invoke(response?.code ?: -1, response?.message ?: errorMsg)
                                onLoaded?.invoke()
                            }
                        }
                    } else {
                        job?.cancel()
                    }
                }
            }
        } catch (e: Exception) {
            //TODO 错误处理
            onFail?.invoke(-1, ExceptionHandle.handleException(e))
            onLoaded?.invoke()
        }
    }

    private var onLoading: (() -> Unit)? = null
    private var onLoaded: (() -> Unit)? = null
    private var onSuccess: ((data: T?, message: String) -> Unit)? = null
    private var onFail: ((code: Int, message: String) -> Unit)? = null

    override fun loading(onLoading: (() -> Unit)) {
        this.onLoading = onLoading
    }
    override fun loaded(onLoaded: (() -> Unit)) {
        this.onLoaded = onLoaded
    }

    override fun success(onSuccess: ((data: T?, message: String) -> Unit)) {
        this.onSuccess = onSuccess
    }

    override fun fail(onFail: ((code: Int, message: String) -> Unit)) {
        this.onFail = onFail
    }

}