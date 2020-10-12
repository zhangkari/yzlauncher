package com.yz.books.ui.thematic

import com.yz.books.base.viewmodel.BaseViewModel
import com.yz.books.utils.LogUtils

/**
 * @author lilin
 * @time on 2019-12-20 09:41
 */
class ThematicViewModel : BaseViewModel(), IThematic {

    private val mModel by lazy(LazyThreadSafetyMode.NONE) {
        ThematicModel().apply {
            init(mLifecycleOwner)
            bindLiveData(_mGlobalState)
        }
    }

    override fun getThematicList(specialTopicCategoryId: Int, pageNum: Int) {
        mModel.getThematicList(specialTopicCategoryId, pageNum)
    }

    override fun getThematicDetail(categoryId: Int, thematicType: String, pageNum: Int) {
        mModel.getThematicDetail(categoryId, thematicType, pageNum)
    }

    override fun getBookDetail(thematicType: String, bookId: Int) {
        mModel.getBookDetail(thematicType, bookId)
    }
}