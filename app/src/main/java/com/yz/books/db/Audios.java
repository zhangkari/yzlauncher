package com.yz.books.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author lilin
 * @time on 2020-02-22 14:50
 */
@Entity(nameInDb = "audios", createInDb = false)
public class Audios {

    @Id
    @Property(nameInDb = "audioId")
    private Long _id;

    @Property(nameInDb = "audioName")
    private String audioName;

    @Property(nameInDb = "coverImg")
    private String coverImg;

    @Property(nameInDb = "categoryId")
    private int categoryId;

    @Generated(hash = 633171444)
    public Audios(Long _id, String audioName, String coverImg, int categoryId) {
        this._id = _id;
        this.audioName = audioName;
        this.coverImg = coverImg;
        this.categoryId = categoryId;
    }

    @Generated(hash = 845444237)
    public Audios() {
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

    public int getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

}
