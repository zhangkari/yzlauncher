package com.yz.books.adapter

import android.content.Context
import android.widget.ImageView
import com.chad.library.adapter.base.BaseViewHolder
import com.yz.books.R
import com.yz.books.base.adapter.YZBaseAdapter
import com.yz.books.ext.addFileHostUrl
import com.yz.books.ui.audio.bean.AudioBookDetailBean
import com.yz.books.ui.base.BookType
import com.yz.books.ui.journal.bean.JournalBookDetailBean
import com.yz.books.ui.massive.bean.MassiveBookDetailBean
import com.yz.books.ui.video.bean.VideoBooksBean
import com.yz.books.utils.ImageLoaderUtils

/**
 * @author lilin
 * @time on 2019-12-16 19:53
 */
class MassiveBookDetailAdapter(context: Context,
                               dataList: MutableList<Any>?) :
    YZBaseAdapter<Any>(R.layout.adapter_item_massive_book_detail, dataList) {

    var mBookType = BookType.TYPE_MASSIVE_BOOK

    override fun handleViewData(helper: BaseViewHolder, item: Any) {
        helper.convertView.isFocusable = true
        helper.itemView.isFocusable = true

        val ivBookImg = helper.getView<ImageView>(R.id.iv_book_img)

        when (mBookType) {
            BookType.TYPE_MASSIVE_BOOK -> {
                with(item as MassiveBookDetailBean.RecommendBooks) {
                    helper.setText(R.id.tv_book_name, bookName)
                    ImageLoaderUtils.withBookCover(coverImg.addFileHostUrl(), ivBookImg, ImageView.ScaleType.FIT_XY)
                }
            }
            BookType.TYPE_AUDIO_BOOK -> {
                with(item as AudioBookDetailBean.RecommendBooks) {
                    helper.setText(R.id.tv_book_name, audioName)
                    ImageLoaderUtils.withBookCover(coverImg.addFileHostUrl(), ivBookImg, ImageView.ScaleType.FIT_XY)
                }
            }
            BookType.TYPE_JOURNAL_BOOK -> {
                with(item as JournalBookDetailBean.RecommendBooks) {
                    helper.setText(R.id.tv_book_name, journalName)
                    ImageLoaderUtils.withBookCover(coverImg.addFileHostUrl(), ivBookImg, ImageView.ScaleType.FIT_XY)
                }
            }
            else -> {
                with(item as VideoBooksBean.VideoBooksInfo) {
                    helper.setText(R.id.tv_book_name, videoName)
                    ImageLoaderUtils.withBookCover(coverImg.addFileHostUrl(), ivBookImg, ImageView.ScaleType.FIT_XY)
                }
            }
        }
    }

}