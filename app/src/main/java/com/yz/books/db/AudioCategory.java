package com.yz.books.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author lilin
 * @time on 2020-02-22 15:02
 */

@Entity(nameInDb = "audio_category", createInDb = false)
public class AudioCategory {

    @Id
    @Property(nameInDb = "categorysId")
    private Long _id;

    @Property(nameInDb = "categorysName")
    private String categorysName;

    @Property(nameInDb = "parentId")
    private int parentId;

    @Generated(hash = 576798605)
    public AudioCategory(Long _id, String categorysName, int parentId) {
        this._id = _id;
        this.categorysName = categorysName;
        this.parentId = parentId;
    }

    @Generated(hash = 1095094235)
    public AudioCategory() {
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getCategorysName() {
        return this.categorysName;
    }

    public void setCategorysName(String categorysName) {
        this.categorysName = categorysName;
    }

    public int getParentId() {
        return this.parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

}
