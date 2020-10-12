package com.yz.books.db;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;

import com.yz.books.common.Constant;
import com.yz.books.utils.FileUtils;

import java.io.File;

/**
 * @author lilin
 * @time on 2020-02-22 16:29
 */
public class GreenDaoContext extends ContextWrapper {

    public final static String dbPath = FileUtils.INSTANCE.getLocalPath() + Constant.DB_PATH;

    public GreenDaoContext(Context base) {
        super(base);
    }

    @Override
    public File getDatabasePath(String name){
        File dbDir = new File(dbPath);
        if(!dbDir.exists()){
            dbDir.mkdirs();
        }

        File dbFile = new File(dbPath, name);
        if(!dbFile.exists()){
            dbFile.mkdirs();
        }
        return dbFile;
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
        SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
        return result;
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
        SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
        return result;
    }
}
