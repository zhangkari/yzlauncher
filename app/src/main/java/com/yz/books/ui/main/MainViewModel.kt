package com.yz.books.ui.main

import com.yz.books.base.viewmodel.BaseViewModel
import com.yz.books.ui.manager.IManager

/**
 * @author lilin
 * @time on 2019-12-17 09:56
 */
class MainViewModel : BaseViewModel(), IMain, IManager {

    private val mModel by lazy(LazyThreadSafetyMode.NONE) {
        MainModel().apply {
            init(mLifecycleOwner)
            bindLiveData(_mGlobalState)
        }
    }

    override fun checkMachineCode(machineCode: String) {
        mModel.checkMachineCode(machineCode)
    }

    override fun updateBD(machineCode: String) {
        mModel.updateBD(machineCode)
    }

    override fun getMainResources() {
        mModel.getMainResources()
    }

    override fun checkAppUpdate() {
        mModel.checkAppUpdate()
    }

    override fun checkDownloadResource() {
        mModel.checkDownloadResource()
    }
}