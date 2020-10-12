package com.yz.books.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author lilin
 * @time on 2020-02-22 15:11
 */

@Entity(nameInDb = "articles", createInDb = false)
public class Articles {

    @Id
    @Property(nameInDb = "articlesId")
    private Long _id;

    @Property(nameInDb = "articlesName")
    private String articlesName;

    @Generated(hash = 1781507973)
    public Articles(Long _id, String articlesName) {
        this._id = _id;
        this.articlesName = articlesName;
    }

    @Generated(hash = 2051751083)
    public Articles() {
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getArticlesName() {
        return this.articlesName;
    }

    public void setArticlesName(String articlesName) {
        this.articlesName = articlesName;
    }


}
