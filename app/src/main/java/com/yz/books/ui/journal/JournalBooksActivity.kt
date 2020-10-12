package com.yz.books.ui.journal

import com.yz.books.base.viewmodel.State
import com.yz.books.common.Constant
import com.yz.books.ext.startToActivity
import com.yz.books.ui.base.BaseBooksActivity
import com.yz.books.ui.base.BookType
import com.yz.books.ui.journal.bean.JournalBookDetailBean
import com.yz.books.ui.massive.bean.JournalBookParamsBean
import com.yz.books.ui.massive.bean.MassiveBookDetailBean
import com.yz.books.ui.massive.readbook.DocTypeEnum
import com.yz.books.ui.massive.readbook.PDFReadBookActivity
import com.yz.books.ui.massive.readbook.ReadJournalBookActivity
import java.util.*

/**
 * 期刊
 *
 * @author lilin
 * @time on 2019-12-19 15:34
 */
class JournalBooksActivity : BaseBooksActivity<JournalBooksViewModel>() {
//region var/val

//endregion

//region implement methods

    override fun providerVMClass() = JournalBooksViewModel()

    override fun initView() {
        super.initView()
        setTitle("期刊")
    }

    override fun initData() {
        super.initData()
        getBookCategorys(TYPE_JOURNAL_BOOK)
    }

    override fun observerForever() = false

    override fun observerBooksUI(state: State) {
        when(state) {
            is JournalBooksModel.JournalBooksState -> {
                setPageInfo(state.journalBooksBean?.total)
                setBooksData(
                    BookType.TYPE_JOURNAL_BOOK,
                    state.journalBooksBean?.journals)
            }
            is JournalBooksModel.JournalBookDetailState -> {
                setJournalBookDetailData(state.journalBookDetailBean)
            }
        }
    }


//endregion

//region public methods

//endregion

//region private methods

    private fun setJournalBookDetailData(bookDetailBean: JournalBookDetailBean?) {
        if (bookDetailBean?.journalDetails == null) {
            return
        }
        with(mMassiveBookDetailDialog) {
            dismiss()
            show()
            setBookDetailData(BookType.TYPE_JOURNAL_BOOK, bookDetailBean.journalDetails)
            setOnClickCallback {
                if (bookDetailBean.journalDetails.bookType ==
                    DocTypeEnum.PDF.name.toLowerCase(Locale.getDefault())) {
                    bookDetailBean.journalDetails.apply {
                        val bookInfo = MassiveBookDetailBean.MassiveBookDetailInfo(
                            author,
                            journalId,
                            journalName,
                            bookType,
                            coverImg,
                            path,
                            fileMd5String,
                            "",
                            0,
                            null,
                            "",
                            ""
                        )
                        startToActivity<PDFReadBookActivity>(
                            Constant.MASSIVE_BOOK_INFO_KEY_EXTRA to bookInfo
                        )
                    }
                } else {
                    bookDetailBean.journalDetails.apply {
                        val bookParams = JournalBookParamsBean("", content, coverImg, journalId,
                            journalName, true, "", fileMd5String, path, lateralReader)
                        startToActivity<ReadJournalBookActivity>(
                            Constant.MASSIVE_BOOK_INFO_KEY_EXTRA to bookParams
                        )
                    }
                }
            }
            setOnItemClickCallback {
                val data = it as MassiveBookDetailBean.MassiveBookDetailInfo
                getBookDetail(data.bookId)
                /*if (bookDetailBean.journalDetails.bookType ==
                    DocTypeEnum.PDF.name.toLowerCase(Locale.getDefault())) {
                    startToActivity<PDFReadBookActivity>(
                        Constant.MASSIVE_BOOK_INFO_KEY_EXTRA to data
                    )
                } else {
                    data.apply {
                        val bookParams = JournalBookParamsBean("", content, coverImg, bookId, bookName,
                            true, "", fileMd5String, path, lateralReader)
                        startToActivity<ReadJournalBookActivity>(
                            Constant.MASSIVE_BOOK_INFO_KEY_EXTRA to bookParams
                        )
                    }
                }*/
            }
        }
    }

//endregion
}