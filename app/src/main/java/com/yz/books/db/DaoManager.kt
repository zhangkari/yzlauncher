package com.yz.books.db

import android.content.Context
import com.yz.books.BuildConfig
import greendao.DaoMaster
import greendao.DaoSession
import org.greenrobot.greendao.query.QueryBuilder

/**
 * @author lilin
 * @time on 2020-02-22 16:01
 */
object DaoManager {

    private var DB_NAME = "yzbook.db"

    private lateinit var mContext: Context

    private var mHelper: DaoMaster.DevOpenHelper? = null
    private var mDaoMaster: DaoMaster? = null
    private var mDaoSession: DaoSession? = null

    fun init(context: Context) {
        mContext = context
        setDebug()
    }

    fun setDBName(dbName: String?) {
        DB_NAME = dbName ?: "yzbook.db"
    }

    /**
     * 判断是否有存在数据库，如果没有则创建
     */
    private fun getDaoMaster(): DaoMaster {
        if (mDaoMaster == null) {
            val helper = DaoMaster.DevOpenHelper(GreenDaoContext(mContext), DB_NAME)
            mHelper = helper
            mDaoMaster = DaoMaster(helper.readableDatabase)
        }
        return mDaoMaster!!
    }

    /**
     * 完成对数据库的添加、删除、修改、查询操作，仅仅是一个接口
     */
    @Synchronized
    fun getDaoSession(): DaoSession {
        if (mDaoSession == null) {
            if (mDaoMaster == null) {
                mDaoMaster = getDaoMaster()
            }
            mDaoSession = mDaoMaster!!.newSession()
        }

        return mDaoSession!!
    }

    /**
     * 关闭所有的操作，数据库开启后，使用完毕要关闭
     */
    fun closeConnection() {
        closeHelper()
        closeDaoSession()
    }

    /**
     * 打开输出日志
     */
    private fun setDebug() {
        QueryBuilder.LOG_SQL = BuildConfig.DEBUG
        QueryBuilder.LOG_VALUES = BuildConfig.DEBUG
    }

    private fun closeHelper() {
        if (mHelper != null) {
            mHelper!!.close()
            mHelper = null
        }
    }

    private fun closeDaoSession() {
        if (mDaoSession != null) {
            mDaoSession!!.clear()
            mDaoSession = null
        }
        if (mDaoMaster != null) {
            mDaoMaster = null
        }
    }
}