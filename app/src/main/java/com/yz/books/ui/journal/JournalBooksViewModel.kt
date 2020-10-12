package com.yz.books.ui.journal

import com.yz.books.ui.base.BaseBooksViewModel

/**
 * @author lilin
 * @time on 2019-12-16 22:21
 */
class JournalBooksViewModel : BaseBooksViewModel(), IJournalBooks {

    //val mMassiveBooksState = MutableLiveData<State>()
    //private val _mMassiveBooksState: LiveData<State>
    //    get() = mMassiveBooksState

    private val mModel by lazy(LazyThreadSafetyMode.NONE) {
        JournalBooksModel().apply {
            init(mLifecycleOwner)
            bindLiveData(mGlobalState)
        }
    }

    override fun getModel() = mModel

    override fun getBooksList(categoryId: Int, pageNum: Int) {
        mModel.getBooksList(categoryId, pageNum)
    }

    override fun getBookDetail(bookId: Int) {
        mModel.getBookDetail(bookId)
    }
}