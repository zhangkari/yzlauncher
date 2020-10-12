package com.yz.books.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.yz.books.R
import com.yz.books.ext.addFileHostUrl
import com.yz.books.ui.main.bean.MainResourcesBean
import com.yz.books.utils.ImageLoaderUtils
import com.yz.books.widget.banner.AbsLoopPagerAdapter
import com.yz.books.widget.banner.BannerView

/**
 * @author lilin
 * @time on 2019-12-16 16:34
 */
class BannerAdapter(bannerView: BannerView) :
    AbsLoopPagerAdapter(bannerView) {

    private val mBannerList = mutableListOf<MainResourcesBean.BannersInfo>()

    fun setBannerList(bannerList: MutableList<MainResourcesBean.BannersInfo>) {
        mBannerList.clear()
        mBannerList.addAll(bannerList)
        notifyDataSetChanged()
    }

    fun getBannerList() = mBannerList

    override fun getRealCount(): Int {
        return mBannerList.size
    }

    override fun getView(container: ViewGroup?, position: Int): View {
        val view = LayoutInflater.from(container?.context).
            inflate(R.layout.adapter_item_banner, container, false)

        val ivBanner = view.findViewById<ImageView>(R.id.iv_banner)

        with(mBannerList[position]) {
            val imgUrl = adPath.addFileHostUrl()
            if (imgUrl != ivBanner.tag) {
                ivBanner.tag = null
                ImageLoaderUtils.withBookCover(imgUrl, ivBanner, ImageView.ScaleType.FIT_XY)
                ivBanner.tag = imgUrl
            }
        }


        return view
    }
}