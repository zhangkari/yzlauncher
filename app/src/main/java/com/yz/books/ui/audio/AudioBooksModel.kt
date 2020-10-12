package com.yz.books.ui.audio

import androidx.collection.ArrayMap
import com.yz.books.base.viewmodel.ErrorState
import com.yz.books.base.viewmodel.LoadedState
import com.yz.books.base.viewmodel.LoadingState
import com.yz.books.base.viewmodel.State
import com.yz.books.common.Constant
import com.yz.books.db.DaoHelper
import com.yz.books.ext.getApplicationContext
import com.yz.books.ext.isNetworkConnected
import com.yz.books.ui.audio.bean.AudioBookChaptersBean
import com.yz.books.ui.audio.bean.AudioBookDetailBean
import com.yz.books.ui.audio.bean.AudioBooksBean
import com.yz.books.ui.base.BaseBooksModel

/**
 * @author lilin
 * @time on 2019-12-16 22:20
 */
class AudioBooksModel: BaseBooksModel(), IAudioBooks {

    class AudioBooksState(val audioBooksBean: AudioBooksBean?): State()
    class AudioBookDetailState(val audioBookDetailBean: AudioBookDetailBean?): State()
    class AudioBookChaptersState(val pageNum: Int,
                                 val audioBookChaptersBean: AudioBookChaptersBean?): State()

    override fun getBooksList(categoryId: Int, pageNum: Int) {
        if (!Constant.ONLINE_VERSION) {
            val data = DaoHelper.getAudioBooks(categoryId, pageNum)
            mBaseState.value = AudioBooksState(data)
            return
        }

        val map = ArrayMap<String, Int>()
        map["categoryId"] = categoryId
        map["rows"] = 12
        map["page"] = pageNum

        requestData<AudioBooksBean>().apply {
            loading {
                mBaseState.value = LoadingState()
            }

            loaded {
                mBaseState.value = LoadedState()
            }

            fail { code, message ->
                mBaseState.value = ErrorState(message, code)
            }

            success { data, _ ->
                mBaseState.value = AudioBooksState(data)
            }

            request {
                mApiService.getAudioBooksByCategory(map)
            }
        }
    }

    override fun getBookDetail(bookId: Int) {
        if (!Constant.ONLINE_VERSION) {
            val data = DaoHelper.getAudioBookDetail(bookId)
            mBaseState.value = AudioBookDetailState(data)
            return
        }

        requestData<AudioBookDetailBean>().apply {
            loading {
                mBaseState.value = LoadingState()
            }

            loaded {
                mBaseState.value = LoadedState()
            }

            fail { code, message ->
                mBaseState.value = ErrorState(message, code)
            }

            success { data, _ ->
                mBaseState.value = AudioBookDetailState(data)
            }

            request {
                mApiService.getAudioBookDetail(bookId)
            }
        }
    }

    override fun getAudioBookChapters(audioId: Int, pageNum: Int) {
        if (!getApplicationContext().isNetworkConnected()) {
            val data = DaoHelper.getAudioBookChapters(audioId, pageNum)
            mBaseState.value = AudioBookChaptersState(pageNum,data)
            return
        }

        val map = ArrayMap<String, Int>()
        map["audioId"] = audioId
        map["rows"] = 6
        map["page"] = pageNum

        requestData<AudioBookChaptersBean>().apply {
            loading {
                mBaseState.value = LoadingState()
            }

            loaded {
                mBaseState.value = LoadedState()
            }

            fail { code, message ->
                mBaseState.value = ErrorState(message, code)
            }

            success { data, _ ->
                mBaseState.value = AudioBookChaptersState(pageNum,data)
            }

            request {
                mApiService.getAudioBookChapters(map)
            }
        }
    }

}