package com.yz.books.api

import com.yz.books.base.bean.BaseBean
import com.yz.books.ui.activities.bean.ActivityDetail
import com.yz.books.ui.activities.bean.ActivityItem
import com.yz.books.ui.audio.bean.AudioBookChaptersBean
import com.yz.books.ui.audio.bean.AudioBookDetailBean
import com.yz.books.ui.audio.bean.AudioBooksBean
import com.yz.books.ui.downloadlist.bean.DownloadResourceBean
import com.yz.books.ui.journal.bean.JournalBookDetailBean
import com.yz.books.ui.journal.bean.JournalBooksBean
import com.yz.books.ui.main.bean.*
import com.yz.books.ui.massive.bean.*
import com.yz.books.ui.notice.bean.NoticeDetailBean
import com.yz.books.ui.notice.bean.NoticesBean
import com.yz.books.ui.thematic.bean.ThematicBean
import com.yz.books.ui.thematic.bean.ThematicDetailBean
import com.yz.books.ui.video.bean.VideoBookChaptersBean
import com.yz.books.ui.video.bean.VideoBooksBean
import retrofit2.http.*

/**
 * @author lilin
 * @time on 2019-12-28 18:50
 */
interface ApiService {

    /**
     * 获取分类
     * @param type 1  图书分类    2  音频分类    3视频分类
     */
    @FormUrlEncoded
    @POST(ApiConstant.GET_BOOK_CATEGORYS)
    suspend fun getBookCategorys(@Field("type") type: Int):
            BaseBean<BookCategorysBean?>

    /**
     * 获取图书列表
     */
    @FormUrlEncoded
    @POST(ApiConstant.GET_BOOKS_BY_CATEGORY)
    suspend fun getBooksByCategory(@FieldMap map: Map<String, Int>):
            BaseBean<MassiveBooksBean?>

    /**
     * 获取图书详情
     * @param bookId
     */
    @FormUrlEncoded
    @POST(ApiConstant.GET_BOOK_DETAIL)
    suspend fun getBookDetail(@Field("bookId") bookId: Int):
            BaseBean<MassiveBookDetailBean?>

    /**
     * 获取听书列表
     */
    @FormUrlEncoded
    @POST(ApiConstant.GET_AUDIO_BOOKS_BY_CATEGORY)
    suspend fun getAudioBooksByCategory(@FieldMap map: Map<String, Int>):
            BaseBean<AudioBooksBean?>

    /**
     * 获取听书详情
     * @param bookId
     */
    @FormUrlEncoded
    @POST(ApiConstant.GET_AUDIO_BOOK_DETAIL)
    suspend fun getAudioBookDetail(@Field("audioId") bookId: Int):
            BaseBean<AudioBookDetailBean?>

    /**
     * 获取听书目录
     */
    @FormUrlEncoded
    @POST(ApiConstant.GET_AUDIO_CHAPTER)
    suspend fun getAudioBookChapters(@FieldMap map: Map<String, Int>):
            BaseBean<AudioBookChaptersBean?>

    /**
     * 获取视频列表
     */
    @FormUrlEncoded
    @POST(ApiConstant.GET_VIDEO_BOOKS_BY_CATEGORY)
    suspend fun getVideoBooksByCategory(@FieldMap map: Map<String, Int>):
            BaseBean<VideoBooksBean?>

    /**
     * 获取视频目录
     */
    @FormUrlEncoded
    @POST(ApiConstant.GET_VIDEO_CHAPTER)
    suspend fun getVideoBookChapters(@FieldMap map: Map<String, Int>):
            BaseBean<VideoBookChaptersBean?>

    /**
     * 获取听书详情
     * @param machineCode
     */
    @FormUrlEncoded
    @POST(ApiConstant.CHECK_MACHINE_CODE)
    suspend fun checkMachineCode(@Field("machineCode") machineCode: String):
            BaseBean<MachineInfoBean?>

    /**
     * 获取数据库更新信息
     */
    @GET(ApiConstant.UPDATE_DB)
    suspend fun updateDB(@Query("apparatusCode") machineCode: String):
            BaseBean<UpdateDBBean?>

    /**
     * 获取首页资源
     */
    @GET(ApiConstant.GET_MAIN_RESOURCES)
    suspend fun getMainResources():
            BaseBean<MainResourcesBean?>

    /**
     * 获取期刊列表
     */
    @FormUrlEncoded
    @POST(ApiConstant.GET_JOURNAL_BOOKS_BY_CATEGORY)
    suspend fun getJournalBooksByCategory(@FieldMap map: Map<String, Int>):
            BaseBean<JournalBooksBean?>

    /**
     * 获取期刊详情
     * @param bookId
     */
    @FormUrlEncoded
    @POST(ApiConstant.GET_JOURNAL_BOOK_DETAIL)
    suspend fun getJournalBookDetail(@Field("journalId") bookId: Int):
            BaseBean<JournalBookDetailBean?>

    /**
     * 获取公告列表
     */
    @GET(ApiConstant.GET_NOTICES)
    suspend fun getNotices(
        @Query("page") pageNum: Int,
        @Query("rows") pageSize: Int
    ):
            BaseBean<NoticesBean?>

    /**
     * 获取公告详情
     */
    @GET(ApiConstant.GET_NOTICE_DETAIL)
    suspend fun getNoticeDetail(@Query("id") noticeId: Int):
            BaseBean<NoticeDetailBean?>

    /**
     * 搜索
     */
    @GET(ApiConstant.SEARCH_BOOK)
    suspend fun searchBook(
        @Query("page") pageNum: Int,
        @Query("rows") pageSize: Int,
        @Query("type") type: Int,
        @Query("title") keyWords: String
    ):
            BaseBean<SearchBookBean>

    /**
     * 获取图书评论
     */
    @GET(ApiConstant.GET_BOOK_COMMENT)
    suspend fun getBookComment(
        @Query("page") pageNum: Int,
        @Query("rows") pageSize: Int,
        @Query("type") type: Int,
        @Query("id") bookId: Int
    ):
            BaseBean<BookCommentBean?>

    /**
     * 获取专题列表
     */
    @GET(ApiConstant.GET_THEMATIC_LIST) //@Query("specialTopicCategoryId") specialTopicCategoryId: Int
    suspend fun getThematicList(
        @Query("page") pageNum: Int,
        @Query("rows") pageSize: Int
    ):
            BaseBean<ThematicBean?>

    /**
     * 获取专题详情
     */
    @FormUrlEncoded
    @POST(ApiConstant.GET_THEMATIC_DETAIL)
    suspend fun getThematicDetail(@FieldMap map: Map<String, Int>):
            BaseBean<ThematicDetailBean>

    /**
     * 获取下载列表
     */
    @FormUrlEncoded
    @POST(ApiConstant.GET_DOWNLOAD_LIST)
    suspend fun getDownloadList(@FieldMap map: Map<String, Int>):
            BaseBean<DownloadResourceBean?>

    /**
     * 检查app更新
     *
     * @param directionType 1横板 2竖版
     */
    @GET(ApiConstant.CHECK_APP_UPDATE)
    suspend fun checkAppUpdate(@Query("direction_type") directionType: Int):
            BaseBean<AppUpdateBean?>

    /**
     * 获取活动列表
     *
     */
    @GET(ApiConstant.GET_ACTIVITIES)
    suspend fun getActivityList():
            BaseBean<MutableList<ActivityItem>?>

    /**
     * 获取活动详情
     *
     * @param id 活动id
     */
    @GET(ApiConstant.GET_ACTIVITY_DETAIL)
    suspend fun getActivityDetail(@Query("id") id: Int):
            BaseBean<ActivityDetail>

    /**
     * 检测下载列表资源更新
     */
    @GET(ApiConstant.CHECK_DOWNLOAD_RESOURCE)
    suspend fun checkDownloadResource():
            BaseBean<MutableList<CheckDownloadResourceBean>?>
}