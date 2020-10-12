package com.yz.books.adapter

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseViewHolder
import com.yz.books.R
import com.yz.books.base.adapter.YZBaseAdapter
import com.yz.books.ext.addFileHostUrl
import com.yz.books.ext.setSpecifiedTextColor
import com.yz.books.ui.massive.bean.BookCommentBean
import com.yz.books.utils.ImageLoaderUtils

/**
 * @author lilin
 * @time on 2019-12-16 19:53
 */
class BookCommentAdapter(val context: Context, dataList: MutableList<BookCommentBean.BookCommentInfo>?) :
    YZBaseAdapter<BookCommentBean.BookCommentInfo>(R.layout.adapter_item_book_comment, dataList) {

    override fun handleViewData(helper: BaseViewHolder, item: BookCommentBean.BookCommentInfo) {
        helper.convertView.isFocusable = true
        helper.itemView.isFocusable = true

        val tvContent = helper.getView<TextView>(R.id.tv_content)
        val ivUserHead = helper.getView<ImageView>(R.id.iv_user_head)

        with(item) {
            val content = "$userName：$content"
            val text = "$userName："
            tvContent.setSpecifiedTextColor(content, text,
                ContextCompat.getColor(context, R.color.c4D3B81))

            ImageLoaderUtils.withUserHead(images.addFileHostUrl(), ivUserHead)
        }
    }

}