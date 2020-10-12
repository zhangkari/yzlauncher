package com.yz.books.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author lilin
 * @time on 2020-02-22 14:50
 */
@Entity(nameInDb = "books", createInDb = false)
public class Books {

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

    @Property(nameInDb = "categoryId")
    private int categoryId;

    @Generated(hash = 1213361445)
    public Books(Long _id, String bookName, String coverImg, String author,
            String bookType, int categoryId) {
        this._id = _id;
        this.bookName = bookName;
        this.coverImg = coverImg;
        this.author = author;
        this.bookType = bookType;
        this.categoryId = categoryId;
    }

    @Generated(hash = 2016280518)
    public Books() {
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

    public int getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

}
