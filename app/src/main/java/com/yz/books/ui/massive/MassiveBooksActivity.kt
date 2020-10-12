package com.yz.books.ui.massive

import com.yz.books.base.viewmodel.State
import com.yz.books.common.Constant
import com.yz.books.ext.showToast
import com.yz.books.ext.startToActivity
import com.yz.books.ui.base.BaseBooksActivity
import com.yz.books.ui.base.BookType
import com.yz.books.ui.massive.bean.MassiveBookDetailBean
import com.yz.books.ui.massive.readbook.DocTypeEnum
import com.yz.books.ui.massive.readbook.EpubReadBookActivity
import com.yz.books.ui.massive.readbook.PDFReadBookActivity

/**
 * 图书
 *
 * @author lilin
 * @time on 2019-12-16 18:05
 */
class MassiveBooksActivity : BaseBooksActivity<MassiveBooksViewModel>() {

//region var/val


//endregion

//region implement methods

    override fun providerVMClass() = MassiveBooksViewModel()

    override fun initView() {
        super.initView()
        setTitle("图书")
        //startToActivity<EpubReadBookActivity>()
    }

    override fun initData() {
        super.initData()
        getBookCategorys(TYPE_MASSIVE_BOOK)
    }

    override fun observerBooksUI(state: State) {
        when (state) {
            is MassiveBooksModel.MassiveBooksState -> {
                setPageInfo(state.massiveBooksBean?.total)
                setBooksData(
                    BookType.TYPE_MASSIVE_BOOK,
                    state.massiveBooksBean?.books
                )
            }
            is MassiveBooksModel.MassiveBookDetailState -> {
                setMassiveBookDetailData(state.massiveBookDetailBean)
            }
        }
    }

//endregion

//region public methods

//endregion

//region private methods

    private fun setMassiveBookDetailData(bookDetailBean: MassiveBookDetailBean?) {
        if (bookDetailBean?.bokDetails == null) {
            showToast("数据错误！")
            return
        }
        with(mMassiveBookDetailDialog) {
            show()
            setBookDetailData(BookType.TYPE_MASSIVE_BOOK, bookDetailBean.bokDetails)
            setOnClickCallback {
                //LogUtils.e("type==${DocTypeEnum.EPUB.name.toLowerCase()}")
                if (bookDetailBean.bokDetails.bookType == DocTypeEnum.EPUB.name.toLowerCase()) {
                    startToActivity<EpubReadBookActivity>(
                        Constant.MASSIVE_BOOK_INFO_KEY_EXTRA to bookDetailBean.bokDetails
                    )
                } else {
                    startToActivity<PDFReadBookActivity>(
                        Constant.MASSIVE_BOOK_INFO_KEY_EXTRA to bookDetailBean.bokDetails
                    )
                }
            }
            setOnItemClickCallback {
                val data = it as MassiveBookDetailBean.MassiveBookDetailInfo
                getBookDetail(data.bookId)
                /*if (data.bookType == DocTypeEnum.EPUB.name.toLowerCase()) {
                    startToActivity<EpubReadBookActivity>(
                        Constant.MASSIVE_BOOK_INFO_KEY_EXTRA to data
                    )
                } else {
                    startToActivity<PDFReadBookActivity>(
                        Constant.MASSIVE_BOOK_INFO_KEY_EXTRA to data
                    )
                }*/
            }
        }
    }

//endregion
}