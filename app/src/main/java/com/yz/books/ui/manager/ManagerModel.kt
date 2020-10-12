package com.yz.books.ui.manager

import androidx.lifecycle.MutableLiveData
import com.yz.books.base.model.BaseModel
import com.yz.books.base.viewmodel.ErrorState
import com.yz.books.base.viewmodel.LoadedState
import com.yz.books.base.viewmodel.LoadingState
import com.yz.books.base.viewmodel.State
import com.yz.books.common.Constant
import com.yz.books.ui.main.bean.AppUpdateBean

/**
 * @author lilin
 * @time on 2020/5/8 下午2:40
 */

class ManagerModel : BaseModel(), IManager {

    override fun bindLiveData(state: MutableLiveData<State>) {
        mBaseState = state
    }

    class AppUpdateState(val appUpdateBean: AppUpdateBean?): State()

    override fun checkAppUpdate() {
        requestData<AppUpdateBean>().apply {
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
                mBaseState.value = AppUpdateState(data)
            }

            request {
                val directionType = if (!Constant.SCREEN_ORIENTATION_LANDSCAPE) {
                    2
                } else {
                    1
                }
                mApiService.checkAppUpdate(directionType)
            }
        }
    }
}