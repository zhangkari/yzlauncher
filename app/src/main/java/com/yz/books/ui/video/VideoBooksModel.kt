package com.yz.books.ui.video

import androidx.collection.ArrayMap
import com.yz.books.base.viewmodel.ErrorState
import com.yz.books.base.viewmodel.LoadedState
import com.yz.books.base.viewmodel.LoadingState
import com.yz.books.base.viewmodel.State
import com.yz.books.common.Constant
import com.yz.books.db.DaoHelper
import com.yz.books.ext.getApplicationContext
import com.yz.books.ext.isNetworkConnected
import com.yz.books.ui.base.BaseBooksModel
import com.yz.books.ui.video.bean.VideoBookChaptersBean
import com.yz.books.ui.video.bean.VideoBooksBean

/**
 * @author lilin
 * @time on 2019-12-16 22:20
 */
class VideoBooksModel: BaseBooksModel(), IVideoBooks {

    class VideoBooksState(val videoBooksBean: VideoBooksBean?): State()
    class VideoBookChaptersState(val pageNum: Int,
                                 val videoBookChaptersBean: VideoBookChaptersBean?): State()

    override fun getBooksList(categoryId: Int, pageNum: Int) {
        if (!Constant.ONLINE_VERSION) {
            val data = DaoHelper.getVideoBooks(categoryId, pageNum)
            mBaseState.value = VideoBooksState(data)
            return
        }

        val map = ArrayMap<String, Int>()
        map["categoryId"] = categoryId
        map["rows"] = 6
        map["page"] = pageNum

        requestData<VideoBooksBean>().apply {
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
                mBaseState.value = VideoBooksState(data)
            }

            request {
                mApiService.getVideoBooksByCategory(map)
            }
        }
    }

    override fun getBookDetail(bookId: Int) {

    }

    override fun getVideoBookChapters(videoId: Int, pageNum: Int) {
        if (!getApplicationContext().isNetworkConnected()) {
            val data = DaoHelper.getVideoBookChapters(videoId, pageNum)
            mBaseState.value = VideoBookChaptersState(pageNum, data)
            return
        }

        val map = ArrayMap<String, Int>()
        map["videoId"] = videoId
        map["rows"] = 6
        map["page"] = pageNum

        requestData<VideoBookChaptersBean>().apply {
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
                mBaseState.value = VideoBookChaptersState(pageNum, data)
            }

            request {
                mApiService.getVideoBookChapters(map)
            }
        }
    }

}