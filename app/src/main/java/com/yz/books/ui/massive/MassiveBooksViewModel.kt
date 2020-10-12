package com.yz.books.ui.massive

import com.yz.books.ui.base.BaseBooksViewModel

/**
 * @author lilin
 * @time on 2019-12-16 22:21
 */
class MassiveBooksViewModel : BaseBooksViewModel(), IMassiveBooks {

    //val mMassiveBooksState = MutableLiveData<State>()
    //private val _mMassiveBooksState: LiveData<State>
    //    get() = mMassiveBooksState

    private val mModel by lazy(LazyThreadSafetyMode.NONE) {
        MassiveBooksModel().apply {
            init(mLifecycleOwner)
            bindLiveData(_mGlobalState)
        }
    }

    override fun getModel() = mModel

    override fun getBooksList(categoryId: Int, pageNum: Int) {
        mModel.getBooksList(categoryId, pageNum)
    }

    override fun getBookDetail(bookId: Int) {
        mModel.getBookDetail(bookId)
    }

    /*@VMPath(VMRouterConstant.VM_GETUSERINFO)
    override fun getUserInfo() {
        mModel.getUserInfo()
    }*/

    /*@VMPath(VMRouterConstant.VM_GETBOOKCATEGORYS)
    override fun getBookCategorys(type: Int) {
        mModel.getBookCategorys(type)
    }*/
}