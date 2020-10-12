package com.yz.books.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author lilin
 * @time on 2020/3/23 上午10:15
 */

@Entity(nameInDb = "special_topic_resource", createInDb = false)
public class ThematicDetail {
    @Id
    @Property(nameInDb = "id")
    private Long _id;

    @Property(nameInDb = "specialTopicId")
    private Integer categoryId;

    @Property(nameInDb = "type")
    private String type;

    @Property(nameInDb = "author")
    private String author;

    @Property(nameInDb = "coverImg")
    private String coverImg;

    @Property(nameInDb = "resourceName")
    private String resourceName;

    @Property(nameInDb = "resourceId")
    private Integer resourceId;

    @Generated(hash = 664504716)
    public ThematicDetail(Long _id, Integer categoryId, String type, String author,
            String coverImg, String resourceName, Integer resourceId) {
        this._id = _id;
        this.categoryId = categoryId;
        this.type = type;
        this.author = author;
        this.coverImg = coverImg;
        this.resourceName = resourceName;
        this.resourceId = resourceId;
    }

    @Generated(hash = 1967336435)
    public ThematicDetail() {
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public Integer getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCoverImg() {
        return this.coverImg;
    }

    public void setCoverImg(String coverImg) {
        this.coverImg = coverImg;
    }

    public String getResourceName() {
        return this.resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public Integer getResourceId() {
        return this.resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

}
