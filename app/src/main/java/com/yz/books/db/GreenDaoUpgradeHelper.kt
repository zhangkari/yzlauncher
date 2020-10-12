package com.yz.books.db

import android.content.Context
import com.github.yuweiguocn.library.greendao.MigrationHelper
import greendao.*
import org.greenrobot.greendao.database.Database

/**
 * @author lilin
 * @time on 2020/3/10 下午10:16
 */
class GreenDaoUpgradeHelper(
    context: Context?,
    name: String?
) : DaoMaster.DevOpenHelper(context, name) {
    override fun onUpgrade(db: Database?, oldVersion: Int, newVersion: Int) {
        MigrationHelper.migrate(db, object : MigrationHelper.ReCreateAllTableListener {
            override fun onCreateAllTables(db: Database?, ifNotExists: Boolean) {
                DaoMaster.createAllTables(db, true)
            }

            override fun onDropAllTables(db: Database?, ifExists: Boolean) {
                DaoMaster.dropAllTables(db, true)
            }
        }, ArticlesDao::class.java, ArticleDetailDao::class.java,
            JournalsDao::class.java, JournalCategoryDao::class.java, JournalDetailDao::class.java)
    }
}