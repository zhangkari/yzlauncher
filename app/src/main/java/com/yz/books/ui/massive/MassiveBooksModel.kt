package com.yz.books.ui.massive

import androidx.collection.ArrayMap
import com.yz.books.base.viewmodel.ErrorState
import com.yz.books.base.viewmodel.LoadedState
import com.yz.books.base.viewmodel.LoadingState
import com.yz.books.base.viewmodel.State
import com.yz.books.common.Constant
import com.yz.books.db.DaoHelper
import com.yz.books.ui.base.BaseBooksModel
import com.yz.books.ui.massive.bean.MassiveBookDetailBean
import com.yz.books.ui.massive.bean.MassiveBooksBean

/**
 * @author lilin
 * @time on 2019-12-16 22:20
 */
class MassiveBooksModel: BaseBooksModel(), IMassiveBooks {

    class MassiveBooksState(val massiveBooksBean: MassiveBooksBean?): State()
    class MassiveBookDetailState(val massiveBookDetailBean: MassiveBookDetailBean?): State()

    override fun getBooksList(categoryId: Int, pageNum: Int) {
        if (!Constant.ONLINE_VERSION) {
            mBaseState.value = LoadingState()

            val data = DaoHelper.getBooks(categoryId, pageNum)

            mBaseState.value = LoadedState()
            mBaseState.value = MassiveBooksState(data)
            return
        }

        val map = ArrayMap<String, Int>()
        map["categoryId"] = categoryId
        map["rows"] = 12
        map["page"] = pageNum

        requestData<MassiveBooksBean>().apply {
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
                mBaseState.value = MassiveBooksState(data)
            }

            request {
                mApiService.getBooksByCategory(map)
            }
        }
    }

    override fun getBookDetail(bookId: Int) {
        if (!Constant.ONLINE_VERSION) {
            val data = DaoHelper.getBookDetail(bookId)
            mBaseState.value = MassiveBookDetailState(data)
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
                mBaseState.value = MassiveBookDetailState(data)
            }

            request {
                mApiService.getBookDetail(bookId)
            }
        }
    }
}