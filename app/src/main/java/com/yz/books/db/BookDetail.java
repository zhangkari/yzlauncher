package com.yz.books.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author lilin
 * @time on 2020-02-22 15:06
 */

@Entity(nameInDb = "book_detail", createInDb = false)
public class BookDetail {

    @Id
    @Property(nameInDb = "bookId")
    private Long _id;

    @Property(nameInDb = "bookName")
    private String bookName;

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

    @Property(nameInDb = "recommend")
    private String recommend;

    @Property(nameInDb = "firstLetters")
    private String firstLetters;

    @Generated(hash = 1306038632)
    public BookDetail(Long _id, String bookName, String coverImg, String author,
            String bookType, String path, String fileMd5String, String qrCode,
            String recommend, String firstLetters) {
        this._id = _id;
        this.bookName = bookName;
        this.coverImg = coverImg;
        this.author = author;
        this.bookType = bookType;
        this.path = path;
        this.fileMd5String = fileMd5String;
        this.qrCode = qrCode;
        this.recommend = recommend;
        this.firstLetters = firstLetters;
    }

    @Generated(hash = 467010836)
    public BookDetail() {
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getBookName() {
        return this.bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
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
