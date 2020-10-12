package com.yz.books.ui.base

import com.yz.books.base.viewmodel.BaseViewModel
import com.yz.books.base.vmpath.VMPath
import com.yz.books.common.VMRouterConstant
import com.yz.books.utils.LogUtils

/**
 * @author lilin
 * @time on 2019-12-16 22:21
 */
abstract class BaseBooksViewModel : BaseViewModel(), IBaseBooks {

    //val mMassiveBooksState = MutableLiveData<State>()
    //private val _mMassiveBooksState: LiveData<State>
    //    get() = mMassiveBooksState

    abstract fun getModel(): BaseBooksModel

    @VMPath(VMRouterConstant.VM_GETUSERINFO)
    override fun getUserInfo() {
        LogUtils.e("getUserInfo==")
        getModel().getUserInfo()
    }

    override fun getBookCategorys(type: Int) {
        getModel().getBookCategorys(type)
    }

    override fun searchBook(pageNum: Int, type: Int, keyWords: String) {
        getModel().searchBook(pageNum, type, keyWords)
    }

    override fun getBookComment(pageNum: Int, bookId: Int, type: Int) {
        getModel().getBookComment(pageNum, bookId, type)
    }
}