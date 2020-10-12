package com.yz.books.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

/**
 * @author lilin
 * @time on 2020-02-22 15:11
 */

@Entity(nameInDb = "articles_detail", createInDb = false)
public class ArticleDetail {

    @Id
    @Property(nameInDb = "articlesId")
    private Long _id;

    @Property(nameInDb = "articlesName")
    private String articlesName;

    @Property(nameInDb = "content")
    private String content;

    @Generated(hash = 761739792)
    public ArticleDetail(Long _id, String articlesName, String content) {
        this._id = _id;
        this.articlesName = articlesName;
        this.content = content;
    }

    @Generated(hash = 850091504)
    public ArticleDetail() {
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

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
