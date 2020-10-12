package com.yz.books.ui.audio

import com.yz.books.base.viewmodel.State
import com.yz.books.common.Constant
import com.yz.books.ext.showToast
import com.yz.books.ext.startToActivity
import com.yz.books.ui.audio.bean.AudioBookDetailBean
import com.yz.books.ui.base.BaseBooksActivity
import com.yz.books.ui.base.BookType
import com.yz.books.ui.audio.player.AudioPlayerActivity

/**
 * 听书
 *
 * @author lilin
 * @time on 2019-12-19 15:34
 */
class AudioBooksActivity : BaseBooksActivity<AudioBooksViewModel>() {
//region var/val

//endregion

//region implement methods

    override fun providerVMClass() = AudioBooksViewModel()

    override fun initView() {
        super.initView()
        setTitle("听书")
    }

    override fun initData() {
        super.initData()
        getBookCategorys(TYPE_AUDIO_BOOK)
    }

    override fun observerBooksUI(state: State) {
        when(state) {
            is AudioBooksModel.AudioBooksState -> {
                setPageInfo(state.audioBooksBean?.total)
                setBooksData(
                    BookType.TYPE_AUDIO_BOOK,
                    state.audioBooksBean?.audios)
            }
            is AudioBooksModel.AudioBookDetailState -> {
                setMassiveBookDetailData(state.audioBookDetailBean)
            }
        }
    }

//endregion

//region public methods

//endregion

//region private methods

    private fun setMassiveBookDetailData(bookDetailBean: AudioBookDetailBean?) {
        if (bookDetailBean?.audioDetails == null) {
            showToast("数据错误！")
            return
        }
        with(mMassiveBookDetailDialog) {
            dismiss()
            show()
            setBookDetailData(BookType.TYPE_AUDIO_BOOK, bookDetailBean.audioDetails)
            setOnClickCallback {
                startToActivity<AudioPlayerActivity>(
                    Constant.AUDIO_BOOK_DETAIL_KEY_EXTRA to bookDetailBean.audioDetails)
            }
            setOnItemClickCallback {
                val data = it as AudioBookDetailBean.AudioBookDetailInfo
                getBookDetail(data.audioId)
                /*startToActivity<AudioPlayerActivity>(
                    Constant.AUDIO_BOOK_DETAIL_KEY_EXTRA to
                            it as AudioBookDetailBean.AudioBookDetailInfo)*/
            }
        }
    }

//endregion
}