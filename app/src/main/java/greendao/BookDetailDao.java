package greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.yz.books.db.BookDetail;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "book_detail".
*/
public class BookDetailDao extends AbstractDao<BookDetail, Long> {

    public static final String TABLENAME = "book_detail";

    /**
     * Properties of entity BookDetail.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property _id = new Property(0, Long.class, "_id", true, "bookId");
        public final static Property BookName = new Property(1, String.class, "bookName", false, "bookName");
        public final static Property CoverImg = new Property(2, String.class, "coverImg", false, "coverImg");
        public final static Property Author = new Property(3, String.class, "author", false, "author");
        public final static Property BookType = new Property(4, String.class, "bookType", false, "bookType");
        public final static Property Path = new Property(5, String.class, "path", false, "path");
        public final static Property FileMd5String = new Property(6, String.class, "fileMd5String", false, "fileMd5String");
        public final static Property QrCode = new Property(7, String.class, "qrCode", false, "qrCode");
        public final static Property Recommend = new Property(8, String.class, "recommend", false, "recommend");
        public final static Property FirstLetters = new Property(9, String.class, "firstLetters", false, "firstLetters");
    }


    public BookDetailDao(DaoConfig config) {
        super(config);
    }
    
    public BookDetailDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, BookDetail entity) {
        stmt.clearBindings();
 
        Long _id = entity.get_id();
        if (_id != null) {
            stmt.bindLong(1, _id);
        }
 
        String bookName = entity.getBookName();
        if (bookName != null) {
            stmt.bindString(2, bookName);
        }
 
        String coverImg = entity.getCoverImg();
        if (coverImg != null) {
            stmt.bindString(3, coverImg);
        }
 
        String author = entity.getAuthor();
        if (author != null) {
            stmt.bindString(4, author);
        }
 
        String bookType = entity.getBookType();
        if (bookType != null) {
            stmt.bindString(5, bookType);
        }
 
        String path = entity.getPath();
        if (path != null) {
            stmt.bindString(6, path);
        }
 
        String fileMd5String = entity.getFileMd5String();
        if (fileMd5String != null) {
            stmt.bindString(7, fileMd5String);
        }
 
        String qrCode = entity.getQrCode();
        if (qrCode != null) {
            stmt.bindString(8, qrCode);
        }
 
        String recommend = entity.getRecommend();
        if (recommend != null) {
            stmt.bindString(9, recommend);
        }
 
        String firstLetters = entity.getFirstLetters();
        if (firstLetters != null) {
            stmt.bindString(10, firstLetters);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, BookDetail entity) {
        stmt.clearBindings();
 
        Long _id = entity.get_id();
        if (_id != null) {
            stmt.bindLong(1, _id);
        }
 
        String bookName = entity.getBookName();
        if (bookName != null) {
            stmt.bindString(2, bookName);
        }
 
        String coverImg = entity.getCoverImg();
        if (coverImg != null) {
            stmt.bindString(3, coverImg);
        }
 
        String author = entity.getAuthor();
        if (author != null) {
            stmt.bindString(4, author);
        }
 
        String bookType = entity.getBookType();
        if (bookType != null) {
            stmt.bindString(5, bookType);
        }
 
        String path = entity.getPath();
        if (path != null) {
            stmt.bindString(6, path);
        }
 
        String fileMd5String = entity.getFileMd5String();
        if (fileMd5String != null) {
            stmt.bindString(7, fileMd5String);
        }
 
        String qrCode = entity.getQrCode();
        if (qrCode != null) {
            stmt.bindString(8, qrCode);
        }
 
        String recommend = entity.getRecommend();
        if (recommend != null) {
            stmt.bindString(9, recommend);
        }
 
        String firstLetters = entity.getFirstLetters();
        if (firstLetters != null) {
            stmt.bindString(10, firstLetters);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public BookDetail readEntity(Cursor cursor, int offset) {
        BookDetail entity = new BookDetail( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // _id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // bookName
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // coverImg
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // author
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // bookType
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // path
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // fileMd5String
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // qrCode
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // recommend
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9) // firstLetters
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, BookDetail entity, int offset) {
        entity.set_id(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setBookName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setCoverImg(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setAuthor(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setBookType(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setPath(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setFileMd5String(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setQrCode(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setRecommend(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setFirstLetters(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(BookDetail entity, long rowId) {
        entity.set_id(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(BookDetail entity) {
        if(entity != null) {
            return entity.get_id();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(BookDetail entity) {
        return entity.get_id() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}