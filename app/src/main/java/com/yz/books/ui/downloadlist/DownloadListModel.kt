package com.yz.books.ui.downloadlist

import androidx.collection.ArrayMap
import androidx.lifecycle.MutableLiveData
import com.yz.books.base.model.BaseModel
import com.yz.books.base.viewmodel.ErrorState
import com.yz.books.base.viewmodel.LoadedState
import com.yz.books.base.viewmodel.LoadingState
import com.yz.books.base.viewmodel.State
import com.yz.books.ui.downloadlist.bean.DownloadResourceBean

/**
 * @author lilin
 * @time on 2020/3/23 上午11:12
 */

class DownloadListModel : BaseModel(), IDownloadList {

    class DownloadListState(val downloadResourceBean: DownloadResourceBean?): State()

    override fun bindLiveData(state: MutableLiveData<State>) {
        mBaseState = state
    }

    override fun getDownloadList(pageNum: Int) {
        val map = ArrayMap<String, Int>()
        map["page"] = pageNum
        map["rows"] = 200

        requestData<DownloadResourceBean>().apply {
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
                mBaseState.value = DownloadListState(data)
            }

            request {
                mApiService.getDownloadList(map)
            }
        }
    }
}