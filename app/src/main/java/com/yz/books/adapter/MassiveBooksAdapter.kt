package com.yz.books.adapter

import android.content.Context
import android.widget.ImageView
import com.chad.library.adapter.base.BaseViewHolder
import com.yz.books.R
import com.yz.books.base.adapter.YZBaseAdapter
import com.yz.books.ext.addFileHostUrl
import com.yz.books.ui.audio.bean.AudioBooksBean
import com.yz.books.ui.base.BookType
import com.yz.books.ui.journal.bean.JournalBooksBean
import com.yz.books.ui.massive.bean.MassiveBooksBean
import com.yz.books.ui.video.bean.VideoBooksBean
import com.yz.books.utils.ImageLoaderUtils

/**
 * @author lilin
 * @time on 2019-12-16 19:53
 */
class MassiveBooksAdapter(context: Context, dataList: MutableList<Any>?) :
    YZBaseAdapter<Any>(R.layout.adapter_item_massive_books, dataList) {

    /**
     * 书类型
     */
    var mBookType = BookType.TYPE_MASSIVE_BOOK

    override fun handleViewData(helper: BaseViewHolder, item: Any) {
        helper.convertView.isFocusable = true
        helper.itemView.isFocusable = true

        val ivBookImg = helper.getView<ImageView>(R.id.iv_book_img)

        if (mBookType == BookType.TYPE_MASSIVE_BOOK) {
            with(item as MassiveBooksBean.MassiveBooksInfo) {
                ImageLoaderUtils.withBookCover(
                    coverImg.addFileHostUrl(),
                    ivBookImg,
                    ImageView.ScaleType.FIT_XY
                )
                helper.setText(R.id.tv_book_name, bookName)
            }
        } else if (mBookType == BookType.TYPE_AUDIO_BOOK) {
            with(item as AudioBooksBean.AudioBooksInfo) {
                ImageLoaderUtils.withBookCover(
                    coverImg.addFileHostUrl(),
                    ivBookImg,
                    ImageView.ScaleType.FIT_XY
                )
                helper.setText(R.id.tv_book_name, audioName)
            }
        } else if (mBookType == BookType.TYPE_VIDEO_BOOK) {
            with(item as VideoBooksBean.VideoBooksInfo) {
                ImageLoaderUtils.withBookCover(
                    coverImg.addFileHostUrl(),
                    ivBookImg,
                    ImageView.ScaleType.FIT_XY
                )
                helper.setText(R.id.tv_book_name, videoName)
            }
        } else if (mBookType == BookType.TYPE_JOURNAL_BOOK) {
            with(item as JournalBooksBean.JournalBooksInfo) {
                //LogUtils.e("期刊封面==${coverImg.addFileHostUrl()}//$coverImg")
                ImageLoaderUtils.withBookCover(
                    coverImg.addFileHostUrl(),
                    ivBookImg,
                    ImageView.ScaleType.FIT_XY
                )
                helper.setText(R.id.tv_book_name, journalName)
            }
        }
    }

}