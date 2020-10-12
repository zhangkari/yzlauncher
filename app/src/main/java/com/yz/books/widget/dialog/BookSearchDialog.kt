package com.yz.books.widget.dialog

import android.content.Context
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.view.KeyEvent
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.yz.books.R
import com.yz.books.adapter.SearchBookAdapter
import com.yz.books.common.Constant
import com.yz.books.ext.setOnItemClick
import com.yz.books.ext.textContent
import com.yz.books.ui.base.BookType
import com.yz.books.ui.massive.bean.SearchBookBean
import com.yz.books.utils.textWatcher
import com.yz.books.widget.dialog.base.BaseDialog
import kotlinx.android.synthetic.main.dialog_book_search.*

/**
 * @author lilin
 * @time on 2019-12-17 09:33
 */
class BookSearchDialog(
    context: Context,
    private val portrait: Boolean,
    val callback: (Int, String) -> Unit
) : BaseDialog(context) {

    private val mMassiveBookDetailAdapter by lazy(LazyThreadSafetyMode.NONE) {
        SearchBookAdapter(context, null)
    }

    private val mKeyWords: String
        get() = et_search.textContent

    private var mTotalPages = 1
    private var mPageNum = 1

    override fun getLayoutId() = R.layout.dialog_book_search

    override fun initView() {
        super.initView()
        showCenter()
        initRecyclerView()
        initKeyboardView()

        et_search.requestFocus()
    }

    override fun initListener() {
        btn_back.setOnClickListener {
            dismiss()
        }

        iv_clear.setOnClickListener {
            et_search.setText("")
            //   view_keyboard.visibility = View.VISIBLE
            group_search_result.visibility = View.GONE
            tv_search_count.visibility = View.GONE
        }

        et_search.setOnClickListener {
            //   view_keyboard.visibility = View.VISIBLE
            group_search_result.visibility = View.GONE
            tv_search_count.visibility = View.GONE
        }

        et_search.textWatcher {
            afterTextChanged {
                val editable = et_search.text
                iv_clear.visibility = if (editable?.isEmpty() == true) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
            }
        }

        iv_search.setOnClickListener {
            if (mKeyWords.isEmpty()) {
                //view_keyboard.visibility = View.VISIBLE
                group_search_result.visibility = View.GONE
                tv_search_count.visibility = View.GONE
                return@setOnClickListener
            }
            mPageNum = 1
            mTotalPages = 1
            callback(mPageNum, mKeyWords)
        }

        btn_previous.setOnClickListener {
            if (mPageNum > 1) {
                mPageNum--

                callback(mPageNum, mKeyWords)
            }
        }

        btn_next.setOnClickListener {
            if (mPageNum < mTotalPages) {
                mPageNum++

                callback(mPageNum, mKeyWords)
            }
        }

        mMassiveBookDetailAdapter.setOnItemClick { adapter, view, position ->
            val data = mMassiveBookDetailAdapter.data[position]
            mItemClickListener?.invoke(mMassiveBookDetailAdapter.mBookType, data)
        }
    }

    private fun initKeyboardView() {
        /*
        val pinyin26KB = Keyboard(context, R.xml.pinyin_26) // 字母键盘
        val numberKB = Keyboard(context, R.xml.number) // 数字键盘

        view_keyboard.isPreviewEnabled = false
        view_keyboard.keyboard = pinyin26KB
        view_keyboard.setOnKeyboardActionListener(object : KeyboardView.OnKeyboardActionListener {
            override fun swipeRight() {

            }

            override fun onPress(primaryCode: Int) {

            }

            override fun onRelease(primaryCode: Int) {

            }

            override fun swipeLeft() {

            }

            override fun swipeUp() {

            }

            override fun swipeDown() {

            }

            override fun onKey(primaryCode: Int, keyCodes: IntArray?) {
                val editable = et_search.text
                val start = et_search.selectionStart

                when(primaryCode) {
                    Keyboard.KEYCODE_SHIFT -> { // 设置shift状态然后刷新页面
                        pinyin26KB.isShifted = !pinyin26KB.isShifted
                        view_keyboard.invalidateAllKeys()
                    }
                    Keyboard.KEYCODE_DELETE -> { // 点击删除键，长按连续删除
                        if (editable != null && editable.isNotEmpty() && start > 0) {
                            editable.delete(start - 1, start)
                        }
                    }
                    -10 -> { // 自定义code，切换到拼音键盘
                        view_keyboard.keyboard = pinyin26KB
                    }
                    -11 -> { // 自定义code，切换到数字键盘
                        view_keyboard.keyboard = numberKB
                    }

                    else -> {
                        if (primaryCode >= 97 && primaryCode <= 97 + 26) {// 按下字母键
                            val text = if (pinyin26KB.isShifted) {
                                (primaryCode - 32).toChar().toString()
                            } else {
                                primaryCode.toChar().toString()
                            }
                            editable?.insert(start, text)
                        } else {// 其他code值，转字符在输入框中显示
                            editable?.insert(start, primaryCode.toChar().toString())
                        }
                    }
                }
            }

            override fun onText(text: CharSequence?) {

            }

        })


         */
    }

    private fun initRecyclerView() {
        with(recycler_view_books) {
            layoutManager = if (portrait) {
                GridLayoutManager(context, 4)
            } else {
                GridLayoutManager(context, 5)
            }
            adapter = mMassiveBookDetailAdapter
        }
    }

    private fun setPageInfo(total: Int) {
        val pageSize = if (Constant.SCREEN_ORIENTATION_LANDSCAPE) {
            10
        } else {
            12
        }
        val totalPages = if (total % pageSize > 0) {
            total / pageSize + 1
        } else {
            total / pageSize
        }
        tv_page_info.text = "$mPageNum / $totalPages"
        mTotalPages = totalPages
    }

    fun setBooksData(
        @BookType bookType: Int,
        bookBean: SearchBookBean?
    ) {
        mMassiveBookDetailAdapter.mBookType = bookType
        mMassiveBookDetailAdapter.replaceData(bookBean?.result ?: mutableListOf())

        val totalCount = bookBean?.total ?: 0
        setPageInfo(totalCount)

        tv_search_count.text = "为您找到相关内容${totalCount}条"
        //view_keyboard.visibility = View.GONE
        tv_search_count.visibility = View.VISIBLE
        group_search_result.visibility = View.VISIBLE
    }

    private var mItemClickListener: ((Int, SearchBookBean.SearchBookBeanInfo) -> Unit)? = null

    fun setOnItemClick(itemClickListener: (Int, SearchBookBean.SearchBookBeanInfo) -> Unit) {
        mItemClickListener = itemClickListener
    }
}