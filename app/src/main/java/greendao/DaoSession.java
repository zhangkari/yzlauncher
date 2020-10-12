package greendao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.yz.books.db.ArticleDetail;
import com.yz.books.db.Articles;
import com.yz.books.db.AudioCategory;
import com.yz.books.db.AudioChapter;
import com.yz.books.db.AudioDetail;
import com.yz.books.db.Audios;
import com.yz.books.db.Banners;
import com.yz.books.db.BookCategory;
import com.yz.books.db.BookDetail;
import com.yz.books.db.Books;
import com.yz.books.db.Comments;
import com.yz.books.db.JournalCategory;
import com.yz.books.db.JournalDetail;
import com.yz.books.db.Journals;
import com.yz.books.db.Navs;
import com.yz.books.db.ThematicDetail;
import com.yz.books.db.Thematics;
import com.yz.books.db.Video;
import com.yz.books.db.VideoCategory;
import com.yz.books.db.VideoChapter;

import greendao.ArticleDetailDao;
import greendao.ArticlesDao;
import greendao.AudioCategoryDao;
import greendao.AudioChapterDao;
import greendao.AudioDetailDao;
import greendao.AudiosDao;
import greendao.BannersDao;
import greendao.BookCategoryDao;
import greendao.BookDetailDao;
import greendao.BooksDao;
import greendao.CommentsDao;
import greendao.JournalCategoryDao;
import greendao.JournalDetailDao;
import greendao.JournalsDao;
import greendao.NavsDao;
import greendao.ThematicDetailDao;
import greendao.ThematicsDao;
import greendao.VideoDao;
import greendao.VideoCategoryDao;
import greendao.VideoChapterDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig articleDetailDaoConfig;
    private final DaoConfig articlesDaoConfig;
    private final DaoConfig audioCategoryDaoConfig;
    private final DaoConfig audioChapterDaoConfig;
    private final DaoConfig audioDetailDaoConfig;
    private final DaoConfig audiosDaoConfig;
    private final DaoConfig bannersDaoConfig;
    private final DaoConfig bookCategoryDaoConfig;
    private final DaoConfig bookDetailDaoConfig;
    private final DaoConfig booksDaoConfig;
    private final DaoConfig commentsDaoConfig;
    private final DaoConfig journalCategoryDaoConfig;
    private final DaoConfig journalDetailDaoConfig;
    private final DaoConfig journalsDaoConfig;
    private final DaoConfig navsDaoConfig;
    private final DaoConfig thematicDetailDaoConfig;
    private final DaoConfig thematicsDaoConfig;
    private final DaoConfig videoDaoConfig;
    private final DaoConfig videoCategoryDaoConfig;
    private final DaoConfig videoChapterDaoConfig;

    private final ArticleDetailDao articleDetailDao;
    private final ArticlesDao articlesDao;
    private final AudioCategoryDao audioCategoryDao;
    private final AudioChapterDao audioChapterDao;
    private final AudioDetailDao audioDetailDao;
    private final AudiosDao audiosDao;
    private final BannersDao bannersDao;
    private final BookCategoryDao bookCategoryDao;
    private final BookDetailDao bookDetailDao;
    private final BooksDao booksDao;
    private final CommentsDao commentsDao;
    private final JournalCategoryDao journalCategoryDao;
    private final JournalDetailDao journalDetailDao;
    private final JournalsDao journalsDao;
    private final NavsDao navsDao;
    private final ThematicDetailDao thematicDetailDao;
    private final ThematicsDao thematicsDao;
    private final VideoDao videoDao;
    private final VideoCategoryDao videoCategoryDao;
    private final VideoChapterDao videoChapterDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        articleDetailDaoConfig = daoConfigMap.get(ArticleDetailDao.class).clone();
        articleDetailDaoConfig.initIdentityScope(type);

        articlesDaoConfig = daoConfigMap.get(ArticlesDao.class).clone();
        articlesDaoConfig.initIdentityScope(type);

        audioCategoryDaoConfig = daoConfigMap.get(AudioCategoryDao.class).clone();
        audioCategoryDaoConfig.initIdentityScope(type);

        audioChapterDaoConfig = daoConfigMap.get(AudioChapterDao.class).clone();
        audioChapterDaoConfig.initIdentityScope(type);

        audioDetailDaoConfig = daoConfigMap.get(AudioDetailDao.class).clone();
        audioDetailDaoConfig.initIdentityScope(type);

        audiosDaoConfig = daoConfigMap.get(AudiosDao.class).clone();
        audiosDaoConfig.initIdentityScope(type);

        bannersDaoConfig = daoConfigMap.get(BannersDao.class).clone();
        bannersDaoConfig.initIdentityScope(type);

        bookCategoryDaoConfig = daoConfigMap.get(BookCategoryDao.class).clone();
        bookCategoryDaoConfig.initIdentityScope(type);

        bookDetailDaoConfig = daoConfigMap.get(BookDetailDao.class).clone();
        bookDetailDaoConfig.initIdentityScope(type);

        booksDaoConfig = daoConfigMap.get(BooksDao.class).clone();
        booksDaoConfig.initIdentityScope(type);

        commentsDaoConfig = daoConfigMap.get(CommentsDao.class).clone();
        commentsDaoConfig.initIdentityScope(type);

        journalCategoryDaoConfig = daoConfigMap.get(JournalCategoryDao.class).clone();
        journalCategoryDaoConfig.initIdentityScope(type);

        journalDetailDaoConfig = daoConfigMap.get(JournalDetailDao.class).clone();
        journalDetailDaoConfig.initIdentityScope(type);

        journalsDaoConfig = daoConfigMap.get(JournalsDao.class).clone();
        journalsDaoConfig.initIdentityScope(type);

        navsDaoConfig = daoConfigMap.get(NavsDao.class).clone();
        navsDaoConfig.initIdentityScope(type);

        thematicDetailDaoConfig = daoConfigMap.get(ThematicDetailDao.class).clone();
        thematicDetailDaoConfig.initIdentityScope(type);

        thematicsDaoConfig = daoConfigMap.get(ThematicsDao.class).clone();
        thematicsDaoConfig.initIdentityScope(type);

        videoDaoConfig = daoConfigMap.get(VideoDao.class).clone();
        videoDaoConfig.initIdentityScope(type);

        videoCategoryDaoConfig = daoConfigMap.get(VideoCategoryDao.class).clone();
        videoCategoryDaoConfig.initIdentityScope(type);

        videoChapterDaoConfig = daoConfigMap.get(VideoChapterDao.class).clone();
        videoChapterDaoConfig.initIdentityScope(type);

        articleDetailDao = new ArticleDetailDao(articleDetailDaoConfig, this);
        articlesDao = new ArticlesDao(articlesDaoConfig, this);
        audioCategoryDao = new AudioCategoryDao(audioCategoryDaoConfig, this);
        audioChapterDao = new AudioChapterDao(audioChapterDaoConfig, this);
        audioDetailDao = new AudioDetailDao(audioDetailDaoConfig, this);
        audiosDao = new AudiosDao(audiosDaoConfig, this);
        bannersDao = new BannersDao(bannersDaoConfig, this);
        bookCategoryDao = new BookCategoryDao(bookCategoryDaoConfig, this);
        bookDetailDao = new BookDetailDao(bookDetailDaoConfig, this);
        booksDao = new BooksDao(booksDaoConfig, this);
        commentsDao = new CommentsDao(commentsDaoConfig, this);
        journalCategoryDao = new JournalCategoryDao(journalCategoryDaoConfig, this);
        journalDetailDao = new JournalDetailDao(journalDetailDaoConfig, this);
        journalsDao = new JournalsDao(journalsDaoConfig, this);
        navsDao = new NavsDao(navsDaoConfig, this);
        thematicDetailDao = new ThematicDetailDao(thematicDetailDaoConfig, this);
        thematicsDao = new ThematicsDao(thematicsDaoConfig, this);
        videoDao = new VideoDao(videoDaoConfig, this);
        videoCategoryDao = new VideoCategoryDao(videoCategoryDaoConfig, this);
        videoChapterDao = new VideoChapterDao(videoChapterDaoConfig, this);

        registerDao(ArticleDetail.class, articleDetailDao);
        registerDao(Articles.class, articlesDao);
        registerDao(AudioCategory.class, audioCategoryDao);
        registerDao(AudioChapter.class, audioChapterDao);
        registerDao(AudioDetail.class, audioDetailDao);
        registerDao(Audios.class, audiosDao);
        registerDao(Banners.class, bannersDao);
        registerDao(BookCategory.class, bookCategoryDao);
        registerDao(BookDetail.class, bookDetailDao);
        registerDao(Books.class, booksDao);
        registerDao(Comments.class, commentsDao);
        registerDao(JournalCategory.class, journalCategoryDao);
        registerDao(JournalDetail.class, journalDetailDao);
        registerDao(Journals.class, journalsDao);
        registerDao(Navs.class, navsDao);
        registerDao(ThematicDetail.class, thematicDetailDao);
        registerDao(Thematics.class, thematicsDao);
        registerDao(Video.class, videoDao);
        registerDao(VideoCategory.class, videoCategoryDao);
        registerDao(VideoChapter.class, videoChapterDao);
    }
    
    public void clear() {
        articleDetailDaoConfig.clearIdentityScope();
        articlesDaoConfig.clearIdentityScope();
        audioCategoryDaoConfig.clearIdentityScope();
        audioChapterDaoConfig.clearIdentityScope();
        audioDetailDaoConfig.clearIdentityScope();
        audiosDaoConfig.clearIdentityScope();
        bannersDaoConfig.clearIdentityScope();
        bookCategoryDaoConfig.clearIdentityScope();
        bookDetailDaoConfig.clearIdentityScope();
        booksDaoConfig.clearIdentityScope();
        commentsDaoConfig.clearIdentityScope();
        journalCategoryDaoConfig.clearIdentityScope();
        journalDetailDaoConfig.clearIdentityScope();
        journalsDaoConfig.clearIdentityScope();
        navsDaoConfig.clearIdentityScope();
        thematicDetailDaoConfig.clearIdentityScope();
        thematicsDaoConfig.clearIdentityScope();
        videoDaoConfig.clearIdentityScope();
        videoCategoryDaoConfig.clearIdentityScope();
        videoChapterDaoConfig.clearIdentityScope();
    }

    public ArticleDetailDao getArticleDetailDao() {
        return articleDetailDao;
    }

    public ArticlesDao getArticlesDao() {
        return articlesDao;
    }

    public AudioCategoryDao getAudioCategoryDao() {
        return audioCategoryDao;
    }

    public AudioChapterDao getAudioChapterDao() {
        return audioChapterDao;
    }

    public AudioDetailDao getAudioDetailDao() {
        return audioDetailDao;
    }

    public AudiosDao getAudiosDao() {
        return audiosDao;
    }

    public BannersDao getBannersDao() {
        return bannersDao;
    }

    public BookCategoryDao getBookCategoryDao() {
        return bookCategoryDao;
    }

    public BookDetailDao getBookDetailDao() {
        return bookDetailDao;
    }

    public BooksDao getBooksDao() {
        return booksDao;
    }

    public CommentsDao getCommentsDao() {
        return commentsDao;
    }

    public JournalCategoryDao getJournalCategoryDao() {
        return journalCategoryDao;
    }

    public JournalDetailDao getJournalDetailDao() {
        return journalDetailDao;
    }

    public JournalsDao getJournalsDao() {
        return journalsDao;
    }

    public NavsDao getNavsDao() {
        return navsDao;
    }

    public ThematicDetailDao getThematicDetailDao() {
        return thematicDetailDao;
    }

    public ThematicsDao getThematicsDao() {
        return thematicsDao;
    }

    public VideoDao getVideoDao() {
        return videoDao;
    }

    public VideoCategoryDao getVideoCategoryDao() {
        return videoCategoryDao;
    }

    public VideoChapterDao getVideoChapterDao() {
        return videoChapterDao;
    }

}
