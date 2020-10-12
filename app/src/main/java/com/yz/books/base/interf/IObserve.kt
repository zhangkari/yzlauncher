package com.yz.books.base.interf

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.yz.books.base.viewmodel.State

/**
 * @author lilin
 * @time on 2019-12-16 21:21
 */
interface IObserve {

    /**
     * state状态监听
     * @param state
     */
    fun observerUI(state: State)

    /**
     * 是否关注生命周期
     */
    fun observerForever(): Boolean

    /**
     * 创建观察者
     */
    fun createObserve()

    /**
     * 这个方法添加的observer会受到owner生命周期的影响，在owner处于active状态时，有数据变化，会通知，
     * 当owner处于inactive状态，不会通知，并且当owner的生命周期状态时DESTROYED时，自动removeObserver
     */
    /*fun handleObserve(liveData: LiveData<State>?,
                      //lifecycleOwner: LifecycleOwner,
                      observer: (State) -> Unit) {
        liveData?.observe(this, Observer {
            if (it == null) {
                return@Observer
            }
            observer(it)
        })
    }*/

    /**
     * 不存在生命周期概念，只要有数据变化，LiveData都会通知，并且不会自动remove
     */
    /*fun handleObserveForever(liveData: LiveData<State>?,
                             observerCallback: (Observer<State>) -> Unit,
                             observe: (State) -> Unit) {
        val observer = Observer<State> {
            if (it == null) {
                return@Observer
            }
            observe(it)
        }
        liveData?.observeForever(observer)

        observerCallback(observer)
    }

    fun removeObserveForever(liveData: LiveData<State>?,
                             observer: Observer<State>?) {
        observer?.let {
            liveData?.removeObserver(it)
        }
    }*/
}