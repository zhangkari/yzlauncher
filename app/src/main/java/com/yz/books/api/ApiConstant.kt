package com.yz.books.api

/**
 * @author lilin
 * @time on 2019-12-28 18:55
 */
object ApiConstant {

    const val HOST = "http://ziyuanyunying.zhongjiaoyuntu.com"//"http://yz.ngo100.com:80"//"http://yuezhi.ngo100.com"
    const val HOST_ACTIVITY = "http://huimin.ngo100.com"

    /**
     * 更新离线db数据库
     */
    const val UPDATE_DB = "/organ/yuezhi/db/update"

    /**
     * 首页资源
     */
    private const val INDEX = "/organ/yuezhi/index"
    const val GET_MAIN_RESOURCES = "$INDEX/load"

    /**
     * 登录
     */
    private const val LOGIN = "/organ/yuezhi/login"
    const val CHECK_MACHINE_CODE = "$LOGIN/checkMachineCode"

    /**
     * 获取分类
     */
    const val GET_BOOK_CATEGORYS = "/organ/yuezhi/categorys"

    /**
     * 图书
     */
    private const val BOOK = "/organ/yuezhi/book"
    const val GET_BOOKS_BY_CATEGORY = "$BOOK/bookByCategory"
    const val GET_BOOK_DETAIL = "$BOOK/bookDetailsById"

    /**
     * 听书
     */
    private const val AUDIO = "/organ/yuezhi/audio"
    const val GET_AUDIO_BOOKS_BY_CATEGORY = "$AUDIO/audioByCategory"
    const val GET_AUDIO_BOOK_DETAIL = "$AUDIO/audioDetailsById"
    const val GET_AUDIO_CHAPTER = "$AUDIO/audioChapter"

    /**
     * 视频
     */
    private const val VIDEO = "/organ/yuezhi/Video"
    const val GET_VIDEO_BOOKS_BY_CATEGORY = "$VIDEO/videoByCategory"
    const val GET_VIDEO_CHAPTER = "$VIDEO/videoChapter"

    /**
     * 期刊
     */
    private const val JOURNAL = "/organ/yuezhi/journal"
    const val GET_JOURNAL_BOOKS_BY_CATEGORY = "$JOURNAL/journalByCategory"
    const val GET_JOURNAL_BOOK_DETAIL = "$JOURNAL/journalDetailsById"

    /**
     * 公告
     */
    private const val NOTICE = "/organ/yuezhi/articles"
    const val GET_NOTICES = "$NOTICE/articlesList"
    const val GET_NOTICE_DETAIL = "$NOTICE/findDetailById"

    /**
     * 搜索
     */
    const val SEARCH_BOOK = "/organ/yuezhi/search/findLike"

    /**
     * 书评
     */
    const val GET_BOOK_COMMENT = "/organ/yuezhi/comment"

    /**
     * 获取专题列表
     */
    const val GET_THEMATIC_LIST = "/organ/yuezhi/specialtopic"
    const val GET_THEMATIC_DETAIL = "$GET_THEMATIC_LIST/getSpecialTopicResources"

    /**
     * 下载列表
     */
    const val GET_DOWNLOAD_LIST = "/organ/yuezhi/resourceDownload"
    const val CHECK_DOWNLOAD_RESOURCE = "/organ/yuezhi/resourceDownload/checkResDown"

    // 活动列表
    const val GET_ACTIVITIES = "/organ/app/activityAll";
    // 活动详情
    const val GET_ACTIVITY_DETAIL = "/organ/app/activityDetail";

    /**
     * app更新检测
     */
    const val CHECK_APP_UPDATE = "/organ/yuezhi/app/update"
}