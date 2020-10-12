package com.yz.books.base.activity

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.yz.books.base.interf.IObserve
import com.yz.books.base.viewmodel.BaseViewModel
import com.yz.books.base.viewmodel.State
import com.yz.books.base.vmpath.VMRouter

/**
 * @author lilin
 * @time on 2019-12-16 21:35
 */
abstract class BaseMVVMActivity<VM : BaseViewModel> : IObserve, BaseActivity() {

//region var/val

    protected var mViewModel: VM? = null
    private lateinit var mObserver: Observer<State>
    lateinit var mVMRouter: VMRouter

    //private val

//endregion

//region implement methods

    override fun onDestroy() {
        mViewModel?.let {
            if (::mObserver.isInitialized) {
                it.mGlobalState.removeObserver(mObserver)
            }
            lifecycle.removeObserver(it)
        }
        super.onDestroy()
    }

    override fun initVM() {
        createVM()
        createObserve()
    }

    override fun createObserve() {
        if (observerForever()) {
            handleObserveForever()
        } else {
            handleObserve()
        }
    }

    override fun initListener() {

    }

//endregion

//region public methods

    /**
     * [BaseViewModel]的实现类
     */
    open fun providerVMClass(): VM? = null

//endregion

//region private methods

    /**
     * 构建ViewModel
     */
    private fun createVM() {
        val vmClazz = providerVMClass() ?: return
        mViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(application)
        ).get(vmClazz::class.java)
            .apply {
                mVMRouter = VMRouter(this)
                initLifecycleOwner(this@BaseMVVMActivity)
                lifecycle.addObserver(this)
            }
    }

    /**
     * 这个方法添加的observer会受到owner生命周期的影响，在owner处于active状态时，有数据变化，会通知，
     * 当owner处于inactive状态，不会通知，并且当owner的生命周期状态时DESTROYED时，自动removeObserver
     */
    private fun handleObserve() {
        //LogUtils.e("handleObserve==${mViewModel?.mGlobalState}")
        mViewModel?.mGlobalState?.observe(this, Observer {
            //LogUtils.e("base-state==$it")
            if (it == null) {
                return@Observer
            }
            observerUI(it)
        })
    }

    /**
     * 不存在生命周期概念，只要有数据变化，LiveData都会通知，并且不会自动remove
     */
    private fun handleObserveForever() {
        mObserver = Observer {
            if (it == null) {
                return@Observer
            }
            observerUI(it)
        }
        mViewModel?.mGlobalState?.observeForever(mObserver)
    }

//endregion
}