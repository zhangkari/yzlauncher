package com.yz.books.ui.journal

import androidx.collection.ArrayMap
import com.yz.books.base.viewmodel.ErrorState
import com.yz.books.base.viewmodel.LoadedState
import com.yz.books.base.viewmodel.LoadingState
import com.yz.books.base.viewmodel.State
import com.yz.books.common.Constant
import com.yz.books.db.DaoHelper
import com.yz.books.ui.base.BaseBooksModel
import com.yz.books.ui.journal.bean.JournalBookDetailBean
import com.yz.books.ui.journal.bean.JournalBooksBean

/**
 * @author lilin
 * @time on 2019-12-16 22:20
 */
class JournalBooksModel: BaseBooksModel(), IJournalBooks {

    class JournalBooksState(val journalBooksBean: JournalBooksBean?): State()
    class JournalBookDetailState(val journalBookDetailBean: JournalBookDetailBean?): State()

    override fun getBooksList(categoryId: Int, pageNum: Int) {
        if (!Constant.ONLINE_VERSION) {
            val data = DaoHelper.getJournalBooks(categoryId, pageNum)
            mBaseState.value = JournalBooksState(data)
            return
        }

        val map = ArrayMap<String, Int>()
        map["categoryId"] = categoryId
        map["rows"] = 12
        map["page"] = pageNum

        requestData<JournalBooksBean>().apply {
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
                mBaseState.value = JournalBooksState(data)
            }

            request {
                mApiService.getJournalBooksByCategory(map)
            }
        }
    }

    override fun getBookDetail(bookId: Int) {
        if (!Constant.ONLINE_VERSION) {
            val data = DaoHelper.getJournalBookDetail(bookId)
            mBaseState.value = JournalBookDetailState(data)
            return
        }
        requestData<JournalBookDetailBean>().apply {
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
                mBaseState.value = JournalBookDetailState(data)
            }

            request {
                mApiService.getJournalBookDetail(bookId)
            }
        }
    }

}