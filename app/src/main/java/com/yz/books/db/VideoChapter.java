package com.yz.books.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author lilin
 * @time on 2020-02-22 15:06
 */

@Entity(nameInDb = "video_chapter", createInDb = false)
public class VideoChapter {

    @Id
    @Property(nameInDb = "chapterId")
    private Long _id;

    @Property(nameInDb = "chapterName")
    private String chapterName;

    @Property(nameInDb = "fileMd5String")
    private String fileMd5String;

    @Property(nameInDb = "chapterUrl")
    private String chapterUrl;

    @Property(nameInDb = "videoId")
    private int videoId;

    @Generated(hash = 2121004042)
    public VideoChapter(Long _id, String chapterName, String fileMd5String,
            String chapterUrl, int videoId) {
        this._id = _id;
        this.chapterName = chapterName;
        this.fileMd5String = fileMd5String;
        this.chapterUrl = chapterUrl;
        this.videoId = videoId;
    }

    @Generated(hash = 1527021678)
    public VideoChapter() {
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getChapterName() {
        return this.chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public String getFileMd5String() {
        return this.fileMd5String;
    }

    public void setFileMd5String(String fileMd5String) {
        this.fileMd5String = fileMd5String;
    }

    public String getChapterUrl() {
        return this.chapterUrl;
    }

    public void setChapterUrl(String chapterUrl) {
        this.chapterUrl = chapterUrl;
    }

    public int getVideoId() {
        return this.videoId;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }

}
