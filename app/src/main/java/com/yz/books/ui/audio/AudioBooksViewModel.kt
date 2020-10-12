package com.yz.books.ui.audio

import com.yz.books.ui.base.BaseBooksModel
import com.yz.books.ui.base.BaseBooksViewModel

/**
 * @author lilin
 * @time on 2019-12-16 22:21
 */
class AudioBooksViewModel : BaseBooksViewModel(), IAudioBooks {

    //val mMassiveBooksState = MutableLiveData<State>()
    //private val _mMassiveBooksState: LiveData<State>
    //    get() = mMassiveBooksState

    private val mModel by lazy(LazyThreadSafetyMode.NONE) {
        AudioBooksModel().apply {
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

    override fun getAudioBookChapters(audioId: Int, pageNum: Int) {
        mModel.getAudioBookChapters(audioId, pageNum)
    }
}