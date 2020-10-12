package com.yz.books.ui.main.bean

/**
 * @author lilin
 * @time on 2020-01-20 10:31
 */
data class MainResourcesBean(val banners: MutableList<BannersInfo>,
                             val navs: MutableList<NavsInfo>) {

    data class BannersInfo(val adLink: String,
                           val adName: String,
                           val adPath: String,
                           val id: Int,
                           val specialTopicCategoryId: Int)

    data class NavsInfo(val id: Int,
                        val imgPath: String,
                        val type: String,
                        val url: String)
    //type = book audio Video journal notice cloud
}