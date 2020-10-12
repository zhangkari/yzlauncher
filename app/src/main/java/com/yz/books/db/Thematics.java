package com.yz.books.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author lilin
 * @time on 2020/3/23 上午10:11
 */
@Entity(nameInDb = "special_topic", createInDb = false)
public class Thematics {
    @Id
    @Property(nameInDb = "id")
    private Long _id;

    @Property(nameInDb = "categoryId")
    private Integer categoryId;

    @Property(nameInDb = "type")
    private String type;

    @Property(nameInDb = "title")
    private String title;

    @Property(nameInDb = "label")
    private String label;

    @Property(nameInDb = "imgUrl")
    private String imgUrl;

    @Property(nameInDb = "content")
    private String content;

    @Property(nameInDb = "status")
    private Integer status;

    @Generated(hash = 76055895)
    public Thematics(Long _id, Integer categoryId, String type, String title,
            String label, String imgUrl, String content, Integer status) {
        this._id = _id;
        this.categoryId = categoryId;
        this.type = type;
        this.title = title;
        this.label = label;
        this.imgUrl = imgUrl;
        this.content = content;
        this.status = status;
    }

    @Generated(hash = 1446504447)
    public Thematics() {
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

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getImgUrl() {
        return this.imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}
