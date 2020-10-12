package com.yz.books.ui.manager

import com.yz.books.base.viewmodel.BaseViewModel

/**
 * @author lilin
 * @time on 2020/4/25 下午8:46
 */

class ManagerViewModel : BaseViewModel(), IManager {

    private val mModel by lazy(LazyThreadSafetyMode.NONE) {
        ManagerModel().apply {
            init(mLifecycleOwner)
            bindLiveData(_mGlobalState)
        }
    }

    override fun checkAppUpdate() {
        mModel.checkAppUpdate()
    }
}