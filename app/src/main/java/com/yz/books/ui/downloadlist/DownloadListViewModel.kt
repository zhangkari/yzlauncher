package com.yz.books.ui.downloadlist

import com.yz.books.base.viewmodel.BaseViewModel

/**
 * @author lilin
 * @time on 2020/3/23 上午11:13
 */

class DownloadListViewModel : BaseViewModel(), IDownloadList {

    private val mModel by lazy(LazyThreadSafetyMode.NONE) {
        DownloadListModel().apply {
            init(mLifecycleOwner)
            bindLiveData(_mGlobalState)
        }
    }

    override fun getDownloadList(pageNum: Int) {
        mModel.getDownloadList(pageNum)
    }
}