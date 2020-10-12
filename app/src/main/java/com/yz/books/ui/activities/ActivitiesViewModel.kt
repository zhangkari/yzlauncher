package com.yz.books.ui.activities

import com.yz.books.base.viewmodel.BaseViewModel
import com.yz.books.ui.base.IActivities

/**
 * @author lilin
 * @time on 2020-01-12 16:05
 */
class ActivitiesViewModel : BaseViewModel(), IActivities {

    private val model by lazy(LazyThreadSafetyMode.NONE) {
        ActivitiesModel().apply {
            init(mLifecycleOwner)
            bindLiveData(_mGlobalState)
        }
    }

    override fun getActivities() {
        model.getActivities()
    }

    override fun getActivityDetail(id: Int) {
        model.getActivityDetail(id)
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