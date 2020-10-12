package com.yz.books.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author lilin
 * @time on 2020/3/10 下午9:20
 */
@Entity(nameInDb = "journals", createInDb = false)
public class Journals {
    @Id
    @Property(nameInDb = "journalId")
    private Long _id;

    @Property(nameInDb = "journalName")
    private String journalName;

    @Property(nameInDb = "coverImg")
    private String coverImg;

    @Property(nameInDb = "categoryId")
    private int categoryId;

    @Generated(hash = 2032559101)
    public Journals(Long _id, String journalName, String coverImg, int categoryId) {
        this._id = _id;
        this.journalName = journalName;
        this.coverImg = coverImg;
        this.categoryId = categoryId;
    }

    @Generated(hash = 909242729)
    public Journals() {
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

    public int getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

}
