package com.yz.books.adapter

import android.content.Context
import android.widget.ImageView
import com.chad.library.adapter.base.BaseViewHolder
import com.yz.books.R
import com.yz.books.base.adapter.YZBaseAdapter
import com.yz.books.ext.addFileHostUrl
import com.yz.books.ui.thematic.bean.ThematicDetailBean
import com.yz.books.utils.ImageLoaderUtils

/**
 * @author lilin
 * @time on 2019-12-16 19:53
 */
class ThematicDetailAdapter(context: Context, dataList: MutableList<ThematicDetailBean.ThematicDetailInfo>?) :
    YZBaseAdapter<ThematicDetailBean.ThematicDetailInfo>(R.layout.adapter_item_thematic_detail, dataList) {

    override fun handleViewData(helper: BaseViewHolder, item: ThematicDetailBean.ThematicDetailInfo) {
        helper.itemView.isFocusable = true
        helper.convertView.isFocusable = true

        val ivBookImg = helper.getView<ImageView>(R.id.iv_book_img)

        with(item) {
            helper.setText(R.id.tv_book_name, resourceName)
            ImageLoaderUtils.withBookCover(coverImg.addFileHostUrl(), ivBookImg, ImageView.ScaleType.FIT_XY)
        }
    }

}