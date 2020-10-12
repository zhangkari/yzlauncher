package com.yz.books.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author lilin
 * @time on 2020/3/10 下午9:23
 */

@Entity(nameInDb = "journal_details", createInDb = false)
public class JournalDetail {

    @Id
    @Property(nameInDb = "journalId")
    private Long _id;

    @Property(nameInDb = "journalName")
    private String journalName;

    @Property(nameInDb = "coverImg")
    private String coverImg;

    @Property(nameInDb = "author")
    private String author;

    @Property(nameInDb = "bookType")
    private String bookType;

    @Property(nameInDb = "path")
    private String path;

    @Property(nameInDb = "fileMd5String")
    private String fileMd5String;

    @Property(nameInDb = "qrCode")
    private String qrCode;

    @Property(nameInDb = "isCollect")
    private Integer isCollect;

    @Property(nameInDb = "recommend")
    private String recommend;

    @Property(nameInDb = "content")
    private String content;

    @Property(nameInDb = "firstLetters")
    private String firstLetters;

    @Property(nameInDb = "lateralReader")
    private String lateralReader;

    @Generated(hash = 512237303)
    public JournalDetail(Long _id, String journalName, String coverImg,
            String author, String bookType, String path, String fileMd5String,
            String qrCode, Integer isCollect, String recommend, String content,
            String firstLetters, String lateralReader) {
        this._id = _id;
        this.journalName = journalName;
        this.coverImg = coverImg;
        this.author = author;
        this.bookType = bookType;
        this.path = path;
        this.fileMd5String = fileMd5String;
        this.qrCode = qrCode;
        this.isCollect = isCollect;
        this.recommend = recommend;
        this.content = content;
        this.firstLetters = firstLetters;
        this.lateralReader = lateralReader;
    }

    @Generated(hash = 919807933)
    public JournalDetail() {
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getJournalName() {
        return this.journalName;
    }

    public void setJournalName(String journalName) {
        this.journalName = journalName;
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

    public String getBookType() {
        return this.bookType;
    }

    public void setBookType(String bookType) {
        this.bookType = bookType;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileMd5String() {
        return this.fileMd5String;
    }

    public void setFileMd5String(String fileMd5String) {
        this.fileMd5String = fileMd5String;
    }

    public String getQrCode() {
        return this.qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public Integer getIsCollect() {
        return this.isCollect;
    }

    public void setIsCollect(Integer isCollect) {
        this.isCollect = isCollect;
    }

    public String getRecommend() {
        return this.recommend;
    }

    public void setRecommend(String recommend) {
        this.recommend = recommend;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFirstLetters() {
        return this.firstLetters;
    }

    public void setFirstLetters(String firstLetters) {
        this.firstLetters = firstLetters;
    }

    public String getLateralReader() {
        return this.lateralReader;
    }

    public void setLateralReader(String lateralReader) {
        this.lateralReader = lateralReader;
    }

}
