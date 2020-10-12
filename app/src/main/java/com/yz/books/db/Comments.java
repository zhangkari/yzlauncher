package com.yz.books.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author lilin
 * @time on 2020-02-22 21:05
 */

@Entity(nameInDb = "bas_comment", createInDb = false)
public class Comments {

    @Id
    @Property(nameInDb = "id")
    private Long _id;

    @Property(nameInDb = "resourceId")
    private int resourceId;

    @Property(nameInDb = "resourceType")
    private int resourceType;

    @Property(nameInDb = "userId")
    private int userId;

    @Property(nameInDb = "userName")
    private String userName;

    @Property(nameInDb = "content")
    private String content;

    @Property(nameInDb = "addTime")
    private String addTime;

    @Property(nameInDb = "parentId")
    private int parentId;

    @Property(nameInDb = "images")
    private String images;

    @Property(nameInDb = "sort")
    private int sort;

    @Property(nameInDb = "status")
    private String status;

    @Generated(hash = 1974220470)
    public Comments(Long _id, int resourceId, int resourceType, int userId,
            String userName, String content, String addTime, int parentId,
            String images, int sort, String status) {
        this._id = _id;
        this.resourceId = resourceId;
        this.resourceType = resourceType;
        this.userId = userId;
        this.userName = userName;
        this.content = content;
        this.addTime = addTime;
        this.parentId = parentId;
        this.images = images;
        this.sort = sort;
        this.status = status;
    }

    @Generated(hash = 1094291921)
    public Comments() {
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public int getResourceId() {
        return this.resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public int getResourceType() {
        return this.resourceType;
    }

    public void setResourceType(int resourceType) {
        this.resourceType = resourceType;
    }

    public int getUserId() {
        return this.userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAddTime() {
        return this.addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public int getParentId() {
        return this.parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getImages() {
        return this.images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public int getSort() {
        return this.sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
