package com.yz.books.ui.thematic

import androidx.collection.ArrayMap
import androidx.lifecycle.MutableLiveData
import com.yz.books.base.model.BaseModel
import com.yz.books.base.viewmodel.ErrorState
import com.yz.books.base.viewmodel.LoadedState
import com.yz.books.base.viewmodel.LoadingState
import com.yz.books.base.viewmodel.State
import com.yz.books.common.Constant
import com.yz.books.db.DaoHelper
import com.yz.books.ui.audio.AudioBooksModel
import com.yz.books.ui.audio.bean.AudioBookDetailBean
import com.yz.books.ui.massive.MassiveBooksModel
import com.yz.books.ui.massive.bean.MassiveBookDetailBean
import com.yz.books.ui.thematic.bean.ThematicBean
import com.yz.books.ui.thematic.bean.ThematicDetailBean
import com.yz.books.ui.thematic.bean.ThematicType
import com.yz.books.utils.LogUtils

/**
 * @author lilin
 * @time on 2020/3/15 下午4:00
 */

class ThematicModel : BaseModel(), IThematic {

    class ThematicListState(val thematicBean: ThematicBean?): State()
    class ThematicDetailState(val thematicDetailBean: ThematicDetailBean?): State()

    override fun bindLiveData(state: MutableLiveData<State>) {
        mBaseState = state
    }

    override fun getThematicList(specialTopicCategoryId: Int, pageNum: Int) {
        if (!Constant.ONLINE_VERSION) {
            val thematicBean = DaoHelper.getThematicList(specialTopicCategoryId, pageNum)
            mBaseState.value = ThematicListState(thematicBean)
            return
        }

        requestData<ThematicBean>().apply {
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
                mBaseState.value = ThematicListState(data)
            }

            request {
                mApiService.getThematicList(pageNum, 6)
            }
        }
    }

    override fun getThematicDetail(categoryId: Int, thematicType: String, pageNum: Int) {
        if (!Constant.ONLINE_VERSION) {
            val data = DaoHelper.getThematicDetail(categoryId, thematicType, pageNum)
            mBaseState.value = ThematicDetailState(data)
            return
        }

        val pageSize = if (Constant.SCREEN_ORIENTATION_LANDSCAPE) {
            6
        } else {
            if (thematicType == ThematicType.TYPE_VIDEO) {
                4
            } else {
                8
            }
        }

        val map = ArrayMap<String, Int>()
        map["specialTopicId"] = categoryId
        map["rows"] = pageSize
        map["page"] = pageNum

        requestData<ThematicDetailBean>().apply {
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
                mBaseState.value = ThematicDetailState(data)
            }

            request {
                mApiService.getThematicDetail(map)
            }
        }
    }

    override fun getBookDetail(thematicType: String, bookId: Int) {
        if (thematicType == ThematicType.TYPE_AUDIO) {
            if (!Constant.ONLINE_VERSION) {
                val data = DaoHelper.getAudioBookDetail(bookId)
                mBaseState.value = AudioBooksModel.AudioBookDetailState(data)
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
                    mBaseState.value = AudioBooksModel.AudioBookDetailState(data)
                }

                request {
                    mApiService.getAudioBookDetail(bookId)
                }
            }
        } else {
            if (!Constant.ONLINE_VERSION) {
                val data = DaoHelper.getBookDetail(bookId)
                mBaseState.value = MassiveBooksModel.MassiveBookDetailState(data)
                return
            }
            requestData<MassiveBookDetailBean>().apply {
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
                    mBaseState.value = MassiveBooksModel.MassiveBookDetailState(data)
                }

                request {
                    mApiService.getBookDetail(bookId)
                }
            }
        }
    }
}