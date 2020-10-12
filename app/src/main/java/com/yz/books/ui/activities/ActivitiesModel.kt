package com.yz.books.ui.activities

import androidx.lifecycle.MutableLiveData
import com.yz.books.api.ActivityApi
import com.yz.books.api.ApiService
import com.yz.books.base.model.BasePagingModel
import com.yz.books.base.viewmodel.ErrorState
import com.yz.books.base.viewmodel.LoadedState
import com.yz.books.base.viewmodel.LoadingState
import com.yz.books.base.viewmodel.State
import com.yz.books.ui.activities.bean.ActivityDetail
import com.yz.books.ui.activities.bean.ActivityItem
import com.yz.books.ui.base.IActivities

/**
 * @author lilin
 * @time on 2020-01-12 16:05
 */
class ActivitiesModel : BasePagingModel(), IActivities {

    class ActivityListState(val list: MutableList<ActivityItem>?) : State()
    class ActivityDetailState(val detail: ActivityDetail?) : State()

    val apiService by lazy {
        ActivityApi.mInstance.getApiService<ApiService>()
    }

    override fun getActivities() {
        requestData<MutableList<ActivityItem>>().apply {
            loading { mBaseState.value = LoadingState() }
            loaded { mBaseState.value = LoadedState() }
            fail { code, message ->
                mBaseState.value = ErrorState(message, code)
            }
            success { data, _ ->
                mBaseState.value =
                    ActivityListState(
                        data
                    )
            }
            request {
                apiService.getActivityList()
            }
        }
    }

    override fun getActivityDetail(id: Int) {
        requestData<ActivityDetail>().apply {
            loading { mBaseState.value = LoadingState() }
            loaded { mBaseState.value = LoadedState() }
            fail { code, message ->
                mBaseState.value = ErrorState(message, code)
            }
            success { data, _ ->
                mBaseState.value =
                    ActivityDetailState(
                        data
                    )
            }
            request {
                apiService.getActivityDetail(id)
            }
        }
    }

    override fun bindLiveData(state: MutableLiveData<State>) {
        mBaseState = state
    }

//region var/val

//endregion

//region implement methods

//endregion

//region public methods

//endregion

//region private methods

//endregion
}