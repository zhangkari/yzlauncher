package com.yz.books.adapter

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseViewHolder
import com.yz.books.R
import com.yz.books.base.adapter.YZBaseAdapter
import com.yz.books.ext.dp2px
import com.yz.books.ui.audio.bean.AudioBookChaptersBean
import com.yz.books.ui.video.bean.VideoBookChaptersBean

/**
 * @author lilin
 * @time on 2019-12-16 19:53
 */
class AudioChaptersAdapter<T : Any>(val context: Context,
                           dataList: MutableList<T>?) :
    YZBaseAdapter<T>(R.layout.adapter_item_audio_chapters, dataList) {

    var mBookName = ""

    override fun handleViewData(helper: BaseViewHolder, item: T) {
        helper.convertView.isFocusable = true
        helper.itemView.isFocusable = true

        val ivPlay = helper.getView<ImageView>(R.id.iv_play)
        val tvChapterName = helper.getView<TextView>(R.id.tv_chapter_name)
        val tvDownload = helper.getView<TextView>(R.id.tv_download)

        if (item is AudioBookChaptersBean.AudioBookChapterInfo) {
            with(item as AudioBookChaptersBean.AudioBookChapterInfo) {
                helper.setText(R.id.tv_chapter_name, chapterName)
                    .setText(R.id.tv_book_name, mBookName)

                if (isPlaying) {
                    ivPlay.setImageResource(R.drawable.ic_playing)
                    tvChapterName.setTextColor(ContextCompat.getColor(context, R.color.c4D3B81))
                    tvDownload.setTextColor(ContextCompat.getColor(context, R.color.c4D3B81))
                } else {
                    ivPlay.setImageResource(R.drawable.ic_play)
                    tvChapterName.setTextColor(ContextCompat.getColor(context, R.color.c804D3B81))
                    tvDownload.setTextColor(ContextCompat.getColor(context, R.color.c804D3B81))
                }

                setDownloadTextViewIcon(tvDownload, downloaded)
            }
        } else {
            if (item is VideoBookChaptersBean.VideoBookChaptersInfo) {
                with(item as VideoBookChaptersBean.VideoBookChaptersInfo) {
                    helper.setText(R.id.tv_chapter_name, chapterName)
                        .setText(R.id.tv_book_name, mBookName)

                    if (isPlaying) {
                        ivPlay.setImageResource(R.drawable.ic_playing)
                        tvChapterName.setTextColor(ContextCompat.getColor(context, R.color.c4D3B81))
                        tvDownload.setTextColor(ContextCompat.getColor(context, R.color.c4D3B81))
                    } else {
                        ivPlay.setImageResource(R.drawable.ic_play)
                        tvChapterName.setTextColor(ContextCompat.getColor(context, R.color.c804D3B81))
                        tvDownload.setTextColor(ContextCompat.getColor(context, R.color.c804D3B81))
                    }

                    setDownloadTextViewIcon(tvDownload, downloaded)
                }
            }
        }
    }

    private fun setDownloadTextViewIcon(tvDownload: TextView, downloaded: Boolean) {
        with(tvDownload) {
            if (downloaded) {
                setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                compoundDrawablePadding = context.dp2px(0)
                text = "已下载"
            } else {
                val drawableTop = ContextCompat.getDrawable(context, R.drawable.ic_download_chapter)
                setCompoundDrawablesWithIntrinsicBounds(null, drawableTop, null, null)
                compoundDrawablePadding = context.dp2px(5)
                text = "下载"
            }
        }
    }

}