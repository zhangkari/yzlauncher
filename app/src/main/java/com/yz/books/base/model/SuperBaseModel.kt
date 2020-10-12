package com.yz.books.base.model

import android.util.Log
import androidx.lifecycle.*
import com.yz.books.api.IApiResponse
import com.yz.books.base.viewmodel.State
import java.lang.ref.WeakReference

/**
 * @author lilin
 * @time on 2019-11-02 17:56
 */
abstract class SuperBaseModel : LifecycleObserver {

    //protected interface IBaseModelListener

    /**
     * 默认超时时间
     */
    //open fun setTimeout(): Long = 15_000L

    /**
     * 网络请求api
     */
    abstract fun <T> requestData(): IApiResponse<T>

    /**
     * 绑定LiveData
     */
    abstract fun bindLiveData(state: MutableLiveData<State>)

    protected lateinit var mBaseState: MutableLiveData<State>
    //private val mLaunchManager: MutableList<Job> = mutableListOf()
    protected var mLifecycleOwner: LifecycleOwner? = null
    private var mLifecycleOwnerReference : WeakReference<LifecycleOwner>? = null

    fun init(owner: LifecycleOwner){
        mLifecycleOwnerReference = WeakReference(owner)
        mLifecycleOwner = mLifecycleOwnerReference?.get()
        mLifecycleOwner?.lifecycle?.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy() {
        Log.e("SuperBaseModel", "onDestroy")
        onClear()
        //clearLaunchTask()
    }

    /**
     * 适当使用避免造成内存泄漏
     */
    private fun onClear() {
        if(mLifecycleOwner != null){
            mLifecycleOwner?.lifecycle?.removeObserver(this)
            mLifecycleOwner = null
            mLifecycleOwnerReference?.clear()
            mLifecycleOwnerReference = null
        }
    }

    /*private fun clearLaunchTask() {
        mLaunchManager.forEach {
            if (it.isActive || !it.isCancelled) {
                it.cancel()
            }
        }
        mLaunchManager.clear()
    }

    fun <T> requestData(result: suspend () -> BaseBean<T>,
                        loading: (() -> Unit)? = null,
                        loaded: (() -> Unit)? = null,
                        success: (result: T?, message: String) -> Unit,
                        fail: (code: Int, message: String) -> Unit): Job? {
        var job: Job? = null

        try {
            mLifecycleOwner?.apply {
                job = lifecycleScope.launchWhenStarted {
                    withContext(UI) {
                        loading?.invoke()
                    }

                    val response: BaseBean<T>? = withContext(IO) {
                        if (setTimeout() == 0L) {
                            result.invoke()
                        } else {
                            withTimeoutOrNull(setTimeout()) {
                                result.invoke()
                            }
                        }
                    }

                    if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                        withContext(UI) {
                            if (response?.code == 200) {
                                success.invoke(response.result,
                                    response.message)
                            } else {
                                fail.invoke(response?.code ?: -1,
                                    response?.message ?: "")
                            }
                        }
                    } else {
                        job?.cancel()
                    }
                }

                if (job?.isCompleted == false) {
                    mLaunchManager += job!!
                }
            }
        } catch (e: Exception) {
            //TODO 错误处理
            fail.invoke(-1, e.message ?: "")
        } finally {
            loaded?.invoke()
        }

        return job
    }*/

}