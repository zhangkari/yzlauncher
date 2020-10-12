package com.yz.books.db

import com.yz.books.common.Constant
import com.yz.books.ui.audio.bean.AudioBookChaptersBean
import com.yz.books.ui.audio.bean.AudioBookDetailBean
import com.yz.books.ui.audio.bean.AudioBooksBean
import com.yz.books.ui.base.BookType
import com.yz.books.ui.journal.bean.JournalBookDetailBean
import com.yz.books.ui.journal.bean.JournalBooksBean
import com.yz.books.ui.main.bean.MainResourcesBean
import com.yz.books.ui.massive.bean.*
import com.yz.books.ui.notice.bean.NoticeDetailBean
import com.yz.books.ui.notice.bean.NoticesBean
import com.yz.books.ui.thematic.bean.ThematicBean
import com.yz.books.ui.thematic.bean.ThematicDetailBean
import com.yz.books.ui.thematic.bean.ThematicType
import com.yz.books.ui.video.bean.VideoBookChaptersBean
import com.yz.books.ui.video.bean.VideoBooksBean
import com.yz.books.utils.LogUtils
import com.yz.books.utils.SearchUtils
import greendao.*
import org.json.JSONArray
import org.json.JSONObject

/**
 * @author lilin
 * @time on 2020-02-22 17:02
 */
class DaoHelper {

    companion object {

        /**
         * 获取首页信息
         */
        fun getMainResources(): MainResourcesBean {
            val banners = mutableListOf<MainResourcesBean.BannersInfo>()
            val navs = mutableListOf<MainResourcesBean.NavsInfo>()

            try {
                val bannerList = DaoManager.getDaoSession().bannersDao.loadAll()
                bannerList.forEach {
                    banners.add(
                        MainResourcesBean.BannersInfo(it.adLink, it.adName, it.adPath, it._id.toInt(), it.specialTopicCategoryId)
                    )
                }

                val navList = DaoManager.getDaoSession().navsDao.loadAll()
                navList.forEach {
                    navs.add(
                        MainResourcesBean.NavsInfo(it._id.toInt(), it.imgPath, it.type, it.url)
                    )
                }

                LogUtils.e("MainResources==${bannerList.size}//${navList.size}")
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return MainResourcesBean(banners, navs)
        }

        /**
         * 查询分类
         */
        fun getBookCategorys(type: Int): BookCategorysBean {
            val bookCategoryList = mutableListOf<BookCategorysBean.BookCategorysInfo>()

            try {
                when (type) {
                    BookType.TYPE_MASSIVE_BOOK -> {
                        val list = DaoManager.getDaoSession().bookCategoryDao.loadAll()
                        list.forEach {
                            bookCategoryList.add(
                                BookCategorysBean.BookCategorysInfo(it._id.toInt(), it.categorysName))
                        }
                    }
                    BookType.TYPE_AUDIO_BOOK -> {
                        val list = DaoManager.getDaoSession().audioCategoryDao.loadAll()
                        list.forEach {
                            bookCategoryList.add(
                                BookCategorysBean.BookCategorysInfo(it._id.toInt(), it.categorysName))
                        }
                    }
                    BookType.TYPE_VIDEO_BOOK -> {
                        val list = DaoManager.getDaoSession().videoCategoryDao.loadAll()
                        list.forEach {
                            bookCategoryList.add(
                                BookCategorysBean.BookCategorysInfo(it._id.toInt(), it.categorysName))
                        }
                    }
                    else -> {
                        val list = DaoManager.getDaoSession().journalCategoryDao.loadAll()
                        list.forEach {
                            bookCategoryList.add(
                                BookCategorysBean.BookCategorysInfo(it._id.toInt(), it.categorysName))
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return BookCategorysBean(bookCategoryList)
        }

        /**
         * 获取图书列表
         */
        fun getBooks(categoryId: Int, pageNum: Int): MassiveBooksBean {
            val bookList = mutableListOf<MassiveBooksBean.MassiveBooksInfo>()
            var totalSize = 0

            try {
                val queryBuilder = if (categoryId == 0) {
                    DaoManager.getDaoSession().booksDao.queryBuilder()
                } else {
                    DaoManager.getDaoSession().booksDao.queryBuilder()
                        .where(BooksDao.Properties.CategoryId.eq(categoryId))
                }

                totalSize = queryBuilder.count().toInt()

                val list = queryBuilder
                    .offset((pageNum - 1) * 12)
                    .limit(12)
                    .list()
                list.forEach {
                    bookList.add(
                        MassiveBooksBean.MassiveBooksInfo(it._id.toInt(), it.bookName, it.coverImg))
                }

                LogUtils.e("books==$totalSize//${list.size}")
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return MassiveBooksBean(totalSize, bookList)
        }

        /**
         * 图书详情
         */
        fun getBookDetail(bookId: Int): MassiveBookDetailBean? {
            var mMassiveBookDetailBean: MassiveBookDetailBean? = null

            try {
                val data = DaoManager.getDaoSession().bookDetailDao.load(bookId.toLong()) ?: return null

                data.apply {
                    val recommendBooList = mutableListOf<MassiveBookDetailBean.RecommendBooks>()
                    if (recommend != null && recommend.isNotEmpty()) {
                        val jsonArray = JSONArray(recommend)
                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray[i] as JSONObject
                            val author = jsonObject.optString("author")
                            val _bookId = jsonObject.optInt("bookId")
                            val bookName = jsonObject.optString("bookName")
                            val bookType = jsonObject.optString("bookType")
                            val coverImg = jsonObject.optString("coverImg")
                            val path = jsonObject.optString("path")
                            val fileMd5String = jsonObject.optString("fileMd5String")
                            val recommendBook = MassiveBookDetailBean.RecommendBooks(author, _bookId, bookName, bookType,
                                coverImg, path, fileMd5String, "")
                            recommendBooList.add(recommendBook)
                        }
                    }

                    val massiveBooInfo = MassiveBookDetailBean.MassiveBookDetailInfo(
                        author,bookId,bookName,bookType,coverImg,path,fileMd5String,qrCode,0,
                        recommendBooList, "", "")

                    mMassiveBookDetailBean = MassiveBookDetailBean(massiveBooInfo)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return mMassiveBookDetailBean
        }

        /**
         * 获取视频图书列表
         */
        fun getVideoBooks(categoryId: Int, pageNum: Int): VideoBooksBean {
            val bookList = mutableListOf<VideoBooksBean.VideoBooksInfo>()
            var totalSize = 0

            try {
                val queryBuilder = if (categoryId == 0) {
                    DaoManager.getDaoSession().videoDao.queryBuilder()
                } else {
                    DaoManager.getDaoSession().videoDao.queryBuilder()
                        .where(VideoDao.Properties.CategoryId.eq(categoryId))
                }

                totalSize = queryBuilder.count().toInt()

                val list = queryBuilder
                    .offset((pageNum - 1) * 6)
                    .limit(6)
                    .list()
                list.forEach {
                    bookList.add(
                        VideoBooksBean.VideoBooksInfo(it._id.toInt(), it.videoName, it.description, it.coverImg))
                }

                LogUtils.e("videobooks==$totalSize//${list.size}")
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return VideoBooksBean(totalSize, bookList)
        }

        /**
         * 获取视频图书章节
         */
        fun getVideoBookChapters(videoId: Int, pageNum: Int): VideoBookChaptersBean {
            val chapterList = mutableListOf<VideoBookChaptersBean.VideoBookChaptersInfo>()
            var totalSize = 0

            try {
                val queryBuilder = DaoManager.getDaoSession().videoChapterDao.queryBuilder()
                    .where(VideoChapterDao.Properties.VideoId.eq(videoId))

                totalSize = queryBuilder.count().toInt()

                val list = queryBuilder
                    .offset((pageNum - 1) *6)
                    .limit(6)
                    .list()
                list.forEach {
                    chapterList.add(
                        VideoBookChaptersBean.VideoBookChaptersInfo(it._id.toInt(), it.chapterName, it.chapterUrl,
                            it.fileMd5String, false, false, "")
                    )
                }

                LogUtils.e("videobookschapters==$totalSize//${list.size}")
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return VideoBookChaptersBean(totalSize, chapterList)
        }

        /**
         * 获取音频图书列表
         */
        fun getAudioBooks(categoryId: Int, pageNum: Int): AudioBooksBean {
            val bookList = mutableListOf<AudioBooksBean.AudioBooksInfo>()
            var totalSize = 0

            try {
                val queryBuilder = if (categoryId == 0) {
                    DaoManager.getDaoSession().audiosDao.queryBuilder()
                } else {
                    DaoManager.getDaoSession().audiosDao.queryBuilder()
                        .where(AudiosDao.Properties.CategoryId.eq(categoryId))
                }

                totalSize = queryBuilder.count().toInt()

                val list = queryBuilder
                    .offset((pageNum - 1) *12)
                    .limit(12)
                    .list()
                list.forEach {
                    bookList.add(
                        AudioBooksBean.AudioBooksInfo(it._id.toInt(), it.audioName, it.coverImg))
                }

                LogUtils.e("audiobooks==$totalSize//${list.size}")
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return AudioBooksBean(totalSize, bookList)
        }

        /**
         * 获取音频图书详情
         */
        fun getAudioBookDetail(bookId: Int): AudioBookDetailBean? {
            var mAudioBookDetailBean: AudioBookDetailBean? = null

            try {
                val data = DaoManager.getDaoSession().audioDetailDao.load(bookId.toLong())
                data.apply {
                    val recommendBooList = mutableListOf<AudioBookDetailBean.RecommendBooks>()
                    if (recommend.isNotEmpty()) {
                        val jsonArray = JSONArray(recommend)
                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray[i] as JSONObject
                            val author = jsonObject.optString("author")
                            val audioId = jsonObject.optInt("audioId")
                            val audioName = jsonObject.optString("audioName")
                            val coverImg = jsonObject.optString("coverImg")
                            val recommendBook = AudioBookDetailBean.RecommendBooks(author, audioId, audioName, coverImg)
                            recommendBooList.add(recommendBook)
                        }
                    }

                    val audioBookInfo = AudioBookDetailBean.AudioBookDetailInfo(
                        author,bookId,audioName,qrCode,coverImg,0,recommendBooList, "")

                    mAudioBookDetailBean = AudioBookDetailBean(audioBookInfo)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return mAudioBookDetailBean
        }

        /**
         * 获取音频图书章节
         */
        fun getAudioBookChapters(audioId: Int, pageNum: Int): AudioBookChaptersBean {
            val chapterList = mutableListOf<AudioBookChaptersBean.AudioBookChapterInfo>()
            var totalSize = 0

            try {
                val queryBuilder = DaoManager.getDaoSession().audioChapterDao.queryBuilder()
                    .where(AudioChapterDao.Properties.AudioId.eq(audioId))

                totalSize = queryBuilder.count().toInt()

                val list = queryBuilder
                    .offset((pageNum - 1) * 12)
                    .limit(12)
                    .list()
                list.forEach {
                    chapterList.add(
                        AudioBookChaptersBean.AudioBookChapterInfo(it._id.toInt(),
                            it.chapterName, it.chapterUrl,
                            it.fileMd5String, false, false, "")
                    )
                }

                LogUtils.e("audiobookschapters==$totalSize//${list.size}")
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return AudioBookChaptersBean(totalSize, chapterList)
        }

        /**
         * 获取期刊列表
         */
        fun getJournalBooks(categoryId: Int, pageNum: Int): JournalBooksBean {
            val bookList = mutableListOf<JournalBooksBean.JournalBooksInfo>()
            var totalSize = 0

            try {
                val queryBuilder = if (categoryId == 0) {
                    DaoManager.getDaoSession().journalsDao.queryBuilder()
                } else {
                    DaoManager.getDaoSession().journalsDao.queryBuilder()
                        .where(JournalsDao.Properties.CategoryId.eq(categoryId))
                }

                totalSize = queryBuilder.count().toInt()

                val list = queryBuilder
                    .offset((pageNum - 1) * 12)
                    .limit(12)
                    .list()
                list.forEach {
                    bookList.add(
                        JournalBooksBean.JournalBooksInfo(it.coverImg, it._id.toInt(), it.journalName))
                }

                LogUtils.e("journalbooks==$totalSize//${list.size}")
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return JournalBooksBean(totalSize, bookList)
        }

        /**
         * 期刊详情
         */
        fun getJournalBookDetail(bookId: Int): JournalBookDetailBean? {
            var mMassiveBookDetailBean: JournalBookDetailBean? = null

            try {
                val data = DaoManager.getDaoSession().journalDetailDao.load(bookId.toLong()) ?: return null

                data.apply {
                    val recommendBooList = mutableListOf<JournalBookDetailBean.RecommendBooks>()
                    if (recommend != null && recommend.isNotEmpty()) {
                        val jsonArray = JSONArray(recommend)
                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray[i] as JSONObject
                            val author = jsonObject.optString("author")
                            val _bookId = jsonObject.optInt("journalId")
                            val bookName = jsonObject.optString("journalName")
                            val bookType = jsonObject.optString("bookType")
                            val coverImg = jsonObject.optString("coverImg")
                            val path = jsonObject.optString("path")
                            val fileMd5String = jsonObject.optString("fileMd5String")
                            val content = jsonObject.optString("content")
                            val lateralReader = jsonObject.optString("lateralReader")
                            val recommendBook = JournalBookDetailBean.RecommendBooks(author, _bookId, bookName, bookType,
                                coverImg, path, fileMd5String, content, lateralReader)
                            recommendBooList.add(recommendBook)
                        }
                    }

                    val massiveBooInfo = JournalBookDetailBean.JournalBookDetailInfo(
                        author,bookId,journalName,bookType,coverImg,fileMd5String,qrCode,path,0,
                        recommendBooList, "", content, lateralReader)

                    mMassiveBookDetailBean = JournalBookDetailBean(massiveBooInfo)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return mMassiveBookDetailBean
        }

        /**
         * 公告列表
         */
        fun getNotices(pageNum: Int): NoticesBean {
            val bookList = mutableListOf<NoticesBean.NoticeInfo>()
            var totalSize = 0

            try {
                val articlesDao = DaoManager.getDaoSession().articlesDao

                totalSize = articlesDao.count().toInt()

                val list = articlesDao.queryBuilder()
                    .offset((pageNum - 1) * 10)
                    .limit(10)
                    .list()
                list.forEach {
                    bookList.add(
                        NoticesBean.NoticeInfo(it._id.toInt(), it.articlesName))
                }

                LogUtils.e("notices==$totalSize//${list.size}")
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return NoticesBean(totalSize, bookList)
        }

        /**
         * 获取公告详情
         */
        fun getNoticeDetail(noticeId: Int): NoticeDetailBean? {
            var data: ArticleDetail? = null

            try {
                data = DaoManager.getDaoSession().articleDetailDao.load(noticeId.toLong())
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (data == null) {
                return null
            }
            return NoticeDetailBean(data.articlesName, data.content)
        }

        /**
         * 获取评论
         */
        fun getBookComment(pageNum: Int, bookId: Int, type: Int): BookCommentBean {
            val commentList = mutableListOf<BookCommentBean.BookCommentInfo>()
            var totalSize = 0

            try {
                val queryBuilder = DaoManager.getDaoSession().commentsDao.queryBuilder()
                    .where(CommentsDao.Properties.ResourceId.eq(bookId),
                        CommentsDao.Properties.ResourceType.eq(type))

                totalSize = queryBuilder.count().toInt()

                val list = queryBuilder
                    .offset((pageNum - 1) * 6)
                    .limit(6)
                    .list()
                list.forEach {
                    commentList.add(
                        BookCommentBean.BookCommentInfo(it.userName, it.images, it.content)
                    )
                }

                LogUtils.e("bookcomment==$totalSize//${list.size}")
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return BookCommentBean(totalSize, commentList)
        }

        fun searchBook(pageNum: Int, type: Int, keyWords: String): SearchBookBean {
            val bookList = mutableListOf<SearchBookBean.SearchBookBeanInfo>()

            val pageSize = if (Constant.SCREEN_ORIENTATION_LANDSCAPE) {
                10
            } else {
                12
            }

            return when (type) {
                BookType.TYPE_MASSIVE_BOOK -> {
                    val queryBuilder = DaoManager.getDaoSession().bookDetailDao.queryBuilder()
                        .where(BookDetailDao.Properties.FirstLetters.like("%$keyWords%"))

                    val totalSize = queryBuilder.count().toInt()

                    val list = queryBuilder
                        .offset((pageNum - 1) * pageSize)
                        .limit(pageSize)
                        .list()
                    list.forEach {
                        bookList.add(
                            SearchBookBean.SearchBookBeanInfo(
                                it.author ?: "", it._id.toInt(), it.bookName ?: "",
                                it.bookType ?: "", it.coverImg ?: "", it.path ?: "", it.fileMd5String ?: "",
                                -1, "", -1, "", -1, "", ""))
                    }
                    SearchBookBean(totalSize, bookList)
                }
                BookType.TYPE_AUDIO_BOOK -> {
                    val queryBuilder = DaoManager.getDaoSession().audioDetailDao.queryBuilder()
                        .where(AudioDetailDao.Properties.FirstLetters.like("%$keyWords%"))

                    val totalSize = queryBuilder.count().toInt()

                    val list = queryBuilder
                        .offset((pageNum - 1) * pageSize)
                        .limit(pageSize)
                        .list()
                    list.forEach {
                        bookList.add(
                            SearchBookBean.SearchBookBeanInfo(
                                it.author, -1, "", "", it.coverImg ?: "", "", "",
                                it._id.toInt(), it.audioName, -1, "", -1, "", ""))
                    }
                    SearchBookBean(totalSize, bookList)
                }
                BookType.TYPE_VIDEO_BOOK -> {
                    val queryBuilder = DaoManager.getDaoSession().videoDao.queryBuilder()
                        .where(VideoDao.Properties.FirstLetters.like("%$keyWords%"))

                    val totalSize = queryBuilder.count().toInt()

                    val list = queryBuilder
                        .offset((pageNum - 1) * pageSize)
                        .limit(pageSize)
                        .list()
                    list.forEach {
                        bookList.add(
                            SearchBookBean.SearchBookBeanInfo(
                                it.author, -1, "", "", it.coverImg ?: "", "", "",
                                -1, "", -1, "", it._id.toInt(), it.videoName, ""))
                    }
                    SearchBookBean(totalSize, bookList)
                }
                else -> {
                    val queryBuilder = DaoManager.getDaoSession().journalDetailDao.queryBuilder()
                        .where(JournalDetailDao.Properties.FirstLetters.like("%$keyWords%"))

                    val totalSize = queryBuilder.count().toInt()

                    val list = queryBuilder
                        .offset((pageNum - 1) * pageSize)
                        .limit(pageSize)
                        .list()
                    list.forEach {
                        bookList.add(
                            SearchBookBean.SearchBookBeanInfo(
                                it.author ?: "", -1, "",
                                it.bookType ?: "", it.coverImg ?: "", it.path ?: "", it.fileMd5String ?: "",
                                -1, "", it._id.toInt(), it.journalName ?: "", -1, "", ""))
                    }
                    SearchBookBean(totalSize, bookList)
                }
            }
        }

        fun getThematicList(specialTopicCategoryId: Int, pageNum: Int): ThematicBean {
            val thematicInfoList = mutableListOf<ThematicBean.ThematicInfo>()
            var totalSize = 0

            try {
                val queryBuilder = DaoManager.getDaoSession().thematicsDao.queryBuilder()
                    //.where(ThematicsDao.Properties.CategoryId.eq(specialTopicCategoryId))

                totalSize = queryBuilder.count().toInt()

                val list = queryBuilder
                    .offset((pageNum - 1) * 6)
                    .limit(6)
                    .list()
                list.forEach {
                    thematicInfoList.add(
                        ThematicBean.ThematicInfo(it.categoryId, it._id.toInt(), it.content, it.imgUrl,
                        it.label, it.status, it.title, it.type))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return ThematicBean(totalSize, thematicInfoList)
        }

        fun getThematicDetail(categoryId: Int, thematicType: String, pageNum: Int): ThematicDetailBean {
            val thematicDetailInfoList = mutableListOf<ThematicDetailBean.ThematicDetailInfo>()
            var totalSize = 0

            try {
                val queryBuilder = DaoManager.getDaoSession().thematicDetailDao.queryBuilder()
                    .where(ThematicDetailDao.Properties.CategoryId.eq(categoryId))

                totalSize = queryBuilder.count().toInt()

                val pageSize = if (Constant.SCREEN_ORIENTATION_LANDSCAPE) {
                    6
                } else {
                    if (thematicType == ThematicType.TYPE_VIDEO) {
                        4
                    } else {
                        8
                    }
                }

                val list = queryBuilder
                    .offset((pageNum - 1) * pageSize)
                    .limit(pageSize)
                    .list()
                list.forEach {
                    thematicDetailInfoList.add(
                        ThematicDetailBean.ThematicDetailInfo(it.author, it.coverImg, it.resourceId, it.resourceName,
                        it.type)
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return ThematicDetailBean(totalSize, thematicDetailInfoList)
        }
    }
}