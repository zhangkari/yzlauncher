package com.yz.books.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author lilin
 * @time on 2020-02-22 20:30
 */

@Entity(nameInDb = "organ_ad", createInDb = false)
public class Banners {

    @Id
    @Property(nameInDb = "id")
    private Long _id;

    @Property(nameInDb = "adLink")
    private String adLink;

    @Property(nameInDb = "adName")
    private String adName;

    @Property(nameInDb = "adPath")
    private String adPath;

    @Property(nameInDb = "specialTopicCategoryId")
    private int specialTopicCategoryId;

    @Generated(hash = 392325930)
    public Banners(Long _id, String adLink, String adName, String adPath,
            int specialTopicCategoryId) {
        this._id = _id;
        this.adLink = adLink;
        this.adName = adName;
        this.adPath = adPath;
        this.specialTopicCategoryId = specialTopicCategoryId;
    }

    @Generated(hash = 1920711254)
    public Banners() {
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getAdLink() {
        return this.adLink;
    }

    public void setAdLink(String adLink) {
        this.adLink = adLink;
    }

    public String getAdName() {
        return this.adName;
    }

    public void setAdName(String adName) {
        this.adName = adName;
    }

    public String getAdPath() {
        return this.adPath;
    }

    public void setAdPath(String adPath) {
        this.adPath = adPath;
    }

    public int getSpecialTopicCategoryId() {
        return this.specialTopicCategoryId;
    }

    public void setSpecialTopicCategoryId(int specialTopicCategoryId) {
        this.specialTopicCategoryId = specialTopicCategoryId;
    }

}
