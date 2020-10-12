package com.yz.books.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author lilin
 * @time on 2020-02-22 15:06
 */

@Entity(nameInDb = "audio_detail", createInDb = false)
public class AudioDetail {

    @Id
    @Property(nameInDb = "audioId")
    private Long _id;

    @Property(nameInDb = "audioName")
    private String audioName;

    @Property(nameInDb = "coverImg")
    private String coverImg;

    @Property(nameInDb = "author")
    private String author;

    @Property(nameInDb = "qrCode")
    private String qrCode;

    @Property(nameInDb = "chapterUrl")
    private String chapterUrl;

    @Property(nameInDb = "recommend")
    private String recommend;

    @Property(nameInDb = "firstLetters")
    private String firstLetters;

    @Generated(hash = 2054719998)
    public AudioDetail(Long _id, String audioName, String coverImg, String author,
            String qrCode, String chapterUrl, String recommend,
            String firstLetters) {
        this._id = _id;
        this.audioName = audioName;
        this.coverImg = coverImg;
        this.author = author;
        this.qrCode = qrCode;
        this.chapterUrl = chapterUrl;
        this.recommend = recommend;
        this.firstLetters = firstLetters;
    }

    @Generated(hash = 687491105)
    public AudioDetail() {
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getAudioName() {
        return this.audioName;
    }

    public void setAudioName(String audioName) {
        this.audioName = audioName;
    }

    public String getCoverImg() {
        return this.coverImg;
    }

    public void setCoverImg(String coverImg) {
        this.coverImg = coverImg;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getQrCode() {
        return this.qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getChapterUrl() {
        return this.chapterUrl;
    }

    public void setChapterUrl(String chapterUrl) {
        this.chapterUrl = chapterUrl;
    }

    public String getRecommend() {
        return this.recommend;
    }

    public void setRecommend(String recommend) {
        this.recommend = recommend;
    }

    public String getFirstLetters() {
        return this.firstLetters;
    }

    public void setFirstLetters(String firstLetters) {
        this.firstLetters = firstLetters;
    }


}
