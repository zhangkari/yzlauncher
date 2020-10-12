package com.yz.books.adapter

import android.content.Context
import android.widget.ImageView
import com.chad.library.adapter.base.BaseViewHolder
import com.yz.books.R
import com.yz.books.base.adapter.YZBaseAdapter
import com.yz.books.ext.addFileHostUrl
import com.yz.books.ui.thematic.bean.ThematicBean
import com.yz.books.utils.ImageLoaderUtils

/**
 * @author lilin
 * @time on 2019-12-16 19:53
 */
class ThematicAdapter(context: Context, dataList: MutableList<ThematicBean.ThematicInfo>?) :
    YZBaseAdapter<ThematicBean.ThematicInfo>(R.layout.adapter_item_thematic, dataList) {

    override fun handleViewData(helper: BaseViewHolder, item: ThematicBean.ThematicInfo) {
        helper.convertView.isFocusable = true
        helper.itemView.isFocusable = true

        val ivImg = helper.getView<ImageView>(R.id.iv_img)

        ImageLoaderUtils.withBookCover(
            item.imgUrl.addFileHostUrl(),
            ivImg,
            ImageView.ScaleType.FIT_XY
        )
    }

}