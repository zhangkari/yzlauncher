package com.yz.books.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author lilin
 * @time on 2020-02-22 15:06
 */

@Entity(nameInDb = "audio_chapter", createInDb = false)
public class AudioChapter {

    @Id
    @Property(nameInDb = "chapterId")
    private Long _id;

    @Property(nameInDb = "chapterName")
    private String chapterName;

    @Property(nameInDb = "fileMd5String")
    private String fileMd5String;

    @Property(nameInDb = "chapterUrl")
    private String chapterUrl;

    @Property(nameInDb = "audioId")
    private int audioId;

    @Generated(hash = 1573352286)
    public AudioChapter(Long _id, String chapterName, String fileMd5String,
            String chapterUrl, int audioId) {
        this._id = _id;
        this.chapterName = chapterName;
        this.fileMd5String = fileMd5String;
        this.chapterUrl = chapterUrl;
        this.audioId = audioId;
    }

    @Generated(hash = 769032942)
    public AudioChapter() {
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

    public int getAudioId() {
        return this.audioId;
    }

    public void setAudioId(int audioId) {
        this.audioId = audioId;
    }


}
