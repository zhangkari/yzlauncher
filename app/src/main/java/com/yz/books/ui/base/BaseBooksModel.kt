package com.yz.books.ui.base

import androidx.lifecycle.MutableLiveData
import com.yz.books.base.model.BasePagingModel
import com.yz.books.base.viewmodel.ErrorState
import com.yz.books.base.viewmodel.LoadedState
import com.yz.books.base.viewmodel.LoadingState
import com.yz.books.base.viewmodel.State
import com.yz.books.common.Constant
import com.yz.books.db.DaoHelper
import com.yz.books.ui.massive.bean.BookCategorysBean
import com.yz.books.ui.massive.bean.BookCommentBean
import com.yz.books.ui.massive.bean.SearchBookBean
import com.yz.books.utils.LogUtils
import java.util.*

/**
 * @author lilin
 * @time on 2019-12-16 22:20
 */
abstract class BaseBooksModel: BasePagingModel(), IBaseBooks {

    class UserInfoState(val name: String): State()
    class BookCategorysState(val bookCategorysBean: BookCategorysBean?): State()
    class SearchBookState(val type: Int,
                          val bookBean: SearchBookBean?): State()
    class BookCommentState(val bookCommentBean: BookCommentBean?): State()

    override fun bindLiveData(state: MutableLiveData<State>) {
        mBaseState = state
    }

    override fun getUserInfo() {
        mBaseState.value = UserInfoState("books")
    }

    override fun getBookCategorys(type: Int) {
        if (!Constant.ONLINE_VERSION) {
            val data = DaoHelper.getBookCategorys(type)
            LogUtils.e("db_BookCategorys==${data.bookCategorys.size}")
            data.bookCategorys.forEach {
                LogUtils.e("db_BookCategorys==${it.categorysName}")
            }
            mBaseState.value = BookCategorysState(data)
            return
        }

        requestData<BookCategorysBean>().apply {
            loading {
                mBaseState.value = LoadingState()
            }

            loaded {
                mBaseState.value = LoadedState()
            }

            fail { code, message ->
                //LogUtils.e("error==$code//$message")
                mBaseState.value = ErrorState(message, code)
            }

            success { data, _ ->
                //LogUtils.e("data==$data")
                mBaseState.value = BookCategorysState(data)
            }

            request {
                //LogUtils.e("request==")
                mApiService.getBookCategorys(type)
            }
        }
    }

    override fun searchBook(pageNum: Int, type: Int, keyWords: String) {
        if (!Constant.ONLINE_VERSION) {
            val data = DaoHelper.searchBook(pageNum, type, keyWords.toLowerCase(Locale.getDefault()))
            mBaseState.value = SearchBookState(type, data)
            return
        }

        requestData<SearchBookBean>().apply {
            loading {
                mBaseState.value = LoadingState()
            }

            loaded {
                mBaseState.value = LoadedState()
            }

            fail { code, message ->
                //LogUtils.e("error==$code//$message")
                mBaseState.value = ErrorState(message, code)
            }

            success { data, _ ->
                //LogUtils.e("data==$data")
                mBaseState.value = SearchBookState(type, data)
            }

            request {
                //LogUtils.e("request==")
                val pageSize = if (Constant.SCREEN_ORIENTATION_LANDSCAPE) {
                    10
                } else {
                    12
                }
                mApiService.searchBook(pageNum, pageSize, type, keyWords)
            }
        }
    }

    override fun getBookComment(pageNum: Int, bookId: Int, type: Int) {
        if (!Constant.ONLINE_VERSION) {
            val data = DaoHelper.getBookComment(pageNum, bookId, type)
            mBaseState.value = BookCommentState(data)
            return
        }

        requestData<BookCommentBean>().apply {
            loading {
                mBaseState.value = LoadingState()
            }

            loaded {
                mBaseState.value = LoadedState()
            }

            fail { code, message ->
                //LogUtils.e("error==$code//$message")
                mBaseState.value = ErrorState(message, code)
            }

            success { data, _ ->
                //LogUtils.e("data==$data")
                mBaseState.value = BookCommentState(data)
            }

            request {
                //LogUtils.e("request==")
                mApiService.getBookComment(pageNum, 6, type, bookId)
            }
        }
    }
}