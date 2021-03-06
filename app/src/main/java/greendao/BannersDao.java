package greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.yz.books.db.Banners;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "organ_ad".
*/
public class BannersDao extends AbstractDao<Banners, Long> {

    public static final String TABLENAME = "organ_ad";

    /**
     * Properties of entity Banners.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property _id = new Property(0, Long.class, "_id", true, "id");
        public final static Property AdLink = new Property(1, String.class, "adLink", false, "adLink");
        public final static Property AdName = new Property(2, String.class, "adName", false, "adName");
        public final static Property AdPath = new Property(3, String.class, "adPath", false, "adPath");
        public final static Property SpecialTopicCategoryId = new Property(4, int.class, "specialTopicCategoryId", false, "specialTopicCategoryId");
    }


    public BannersDao(DaoConfig config) {
        super(config);
    }
    
    public BannersDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Banners entity) {
        stmt.clearBindings();
 
        Long _id = entity.get_id();
        if (_id != null) {
            stmt.bindLong(1, _id);
        }
 
        String adLink = entity.getAdLink();
        if (adLink != null) {
            stmt.bindString(2, adLink);
        }
 
        String adName = entity.getAdName();
        if (adName != null) {
            stmt.bindString(3, adName);
        }
 
        String adPath = entity.getAdPath();
        if (adPath != null) {
            stmt.bindString(4, adPath);
        }
        stmt.bindLong(5, entity.getSpecialTopicCategoryId());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Banners entity) {
        stmt.clearBindings();
 
        Long _id = entity.get_id();
        if (_id != null) {
            stmt.bindLong(1, _id);
        }
 
        String adLink = entity.getAdLink();
        if (adLink != null) {
            stmt.bindString(2, adLink);
        }
 
        String adName = entity.getAdName();
        if (adName != null) {
            stmt.bindString(3, adName);
        }
 
        String adPath = entity.getAdPath();
        if (adPath != null) {
            stmt.bindString(4, adPath);
        }
        stmt.bindLong(5, entity.getSpecialTopicCategoryId());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public Banners readEntity(Cursor cursor, int offset) {
        Banners entity = new Banners( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // _id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // adLink
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // adName
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // adPath
            cursor.getInt(offset + 4) // specialTopicCategoryId
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Banners entity, int offset) {
        entity.set_id(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setAdLink(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setAdName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setAdPath(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setSpecialTopicCategoryId(cursor.getInt(offset + 4));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(Banners entity, long rowId) {
        entity.set_id(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(Banners entity) {
        if(entity != null) {
            return entity.get_id();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Banners entity) {
        return entity.get_id() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
