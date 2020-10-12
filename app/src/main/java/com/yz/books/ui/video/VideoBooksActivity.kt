package com.yz.books.ui.video

import android.content.pm.ActivityInfo
import android.os.Build
import com.yz.books.base.viewmodel.State
import com.yz.books.common.Constant
import com.yz.books.ext.startToActivity
import com.yz.books.ui.base.BaseBooksActivity
import com.yz.books.ui.base.BookType
import com.yz.books.ui.video.bean.VideoBooksBean
import com.yz.books.ui.video.player.NewVideoPlayerActivity
import com.yz.books.ui.video.player.VideoPlayerActivity
import com.yz.books.ui.video.player.VideoPlayerActivityx
import java.util.*

/**
 * 视频
 *
 * @author lilin
 * @time on 2019-12-19 15:34
 */
class VideoBooksActivity : BaseBooksActivity<VideoBooksViewModel>() {
//region var/val

//endregion

//region implement methods

    override fun providerVMClass() = VideoBooksViewModel()

    override fun initView() {
        mBookType = TYPE_VIDEO_BOOK
        super.initView()
        setTitle("视频")
    }

    override fun initData() {
        super.initData()
        getBookCategorys(TYPE_VIDEO_BOOK)
    }

    override fun observerBooksUI(state: State) {
        when(state) {
            is VideoBooksModel.VideoBooksState -> {
                setPageInfo(state.videoBooksBean?.total,
                    BookType.TYPE_VIDEO_BOOK)
                setBooksData(
                    BookType.TYPE_VIDEO_BOOK,
                    state.videoBooksBean?.videos)
            }
        }
    }

    override fun <T> clickBooksAdapterItem(data: T) {
        if (data is VideoBooksBean.VideoBooksInfo) {
            val videoInfo = "${data.videoId},${data.videoName}"
            if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                || Constant.LOW_SYSTEM) {
                startToActivity<VideoPlayerActivity>(Constant.VIDEO_INFO_KEY_EXTRA to videoInfo)
            } else {
                startToActivity<VideoPlayerActivityx>(Constant.VIDEO_INFO_KEY_EXTRA to videoInfo)
            }
        }
    }

//endregion

//region public methods

//endregion

//region private methods

//endregion
}