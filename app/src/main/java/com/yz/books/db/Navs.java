package com.yz.books.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author lilin
 * @time on 2020-02-22 20:32
 */

@Entity(nameInDb = "nav", createInDb = false)
public class Navs {

    @Id
    @Property(nameInDb = "id")
    private Long _id;

    @Property(nameInDb = "type")
    private String type;

    @Property(nameInDb = "imgPath")
    private String imgPath;

    @Property(nameInDb = "url")
    private String url;

    @Generated(hash = 1702490956)
    public Navs(Long _id, String type, String imgPath, String url) {
        this._id = _id;
        this.type = type;
        this.imgPath = imgPath;
        this.url = url;
    }

    @Generated(hash = 448108638)
    public Navs() {
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImgPath() {
        return this.imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
