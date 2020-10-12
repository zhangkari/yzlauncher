package com.yz.books.ui.main

import androidx.lifecycle.MutableLiveData
import com.yz.books.base.model.BaseModel
import com.yz.books.base.viewmodel.ErrorState
import com.yz.books.base.viewmodel.LoadedState
import com.yz.books.base.viewmodel.LoadingState
import com.yz.books.base.viewmodel.State
import com.yz.books.common.Constant
import com.yz.books.db.DaoHelper
import com.yz.books.ui.main.bean.*
import com.yz.books.ui.manager.IManager

/**
 * @author lilin
 * @time on 2020-01-14 20:48
 */
class MainModel : BaseModel(), IMain, IManager {

    override fun bindLiveData(state: MutableLiveData<State>) {
        mBaseState = state
    }

    class MachineCodeState(val machineInfoBean: MachineInfoBean?): State()
    class MainResourcesState(val mainResourcesBean: MainResourcesBean?): State()
    class UpdateDBState(val updateDBBean: UpdateDBBean?): State()
    class AppUpdateState(val appUpdateBean: AppUpdateBean?): State()
    class DownloadResourceState(val downloadResourceList: MutableList<CheckDownloadResourceBean>?): State()

    override fun checkMachineCode(machineCode: String) {
        requestData<MachineInfoBean>().apply {
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
                mBaseState.value = MachineCodeState(data)
            }

            request {
                mApiService.checkMachineCode(machineCode)
            }
        }
    }

    override fun updateBD(machineCode: String) {
        requestData<UpdateDBBean>().apply {
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
                mBaseState.value = UpdateDBState(data)
            }

            request {
                mApiService.updateDB(machineCode)
            }
        }
    }

    override fun getMainResources() {
        //LogUtils.e("getMainResources==")
        if (!Constant.ONLINE_VERSION) {
            val data = DaoHelper.getMainResources()
            mBaseState.value = MainResourcesState(data)
            return
        }

        requestData<MainResourcesBean>().apply {
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
                mBaseState.value = MainResourcesState(data)
            }

            request {
                mApiService.getMainResources()
            }
        }
    }

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

    override fun checkDownloadResource() {
        requestData<MutableList<CheckDownloadResourceBean>>().apply {
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
                mBaseState.value = DownloadResourceState(data)
            }

            request {
                mApiService.checkDownloadResource()
            }
        }
    }
}