package com.yz.books.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author lilin
 * @time on 2020-02-22 14:50
 */
@Entity(nameInDb = "Video", createInDb = false)
public class Video {

    @Id
    @Property(nameInDb = "videoId")
    private Long _id;

    @Property(nameInDb = "videoName")
    private String videoName;

    @Property(nameInDb = "coverImg")
    private String coverImg;

    @Property(nameInDb = "description")
    private String description;

    @Property(nameInDb = "author")
    private String author;

    @Property(nameInDb = "qrCode")
    private String qrCode;

    @Property(nameInDb = "categoryId")
    private int categoryId;

    @Property(nameInDb = "firstLetters")
    private String firstLetters;

    @Generated(hash = 2126956246)
    public Video(Long _id, String videoName, String coverImg, String description,
            String author, String qrCode, int categoryId, String firstLetters) {
        this._id = _id;
        this.videoName = videoName;
        this.coverImg = coverImg;
        this.description = description;
        this.author = author;
        this.qrCode = qrCode;
        this.categoryId = categoryId;
        this.firstLetters = firstLetters;
    }

    @Generated(hash = 237528154)
    public Video() {
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getVideoName() {
        return this.videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getCoverImg() {
        return this.coverImg;
    }

    public void setCoverImg(String coverImg) {
        this.coverImg = coverImg;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public int getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getFirstLetters() {
        return this.firstLetters;
    }

    public void setFirstLetters(String firstLetters) {
        this.firstLetters = firstLetters;
    }

}
