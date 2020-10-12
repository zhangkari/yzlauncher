package rmkj.lib.read.db;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class RMReadingMarkDB {

	public static final String TABLE_NAME = " tb_book_mark ";

	public static void createTable(SQLiteDatabase db) {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE " + TABLE_NAME + "(");
		sb.append("id INTEGER PRIMARY KEY AUTOINCREMENT");
		/** 考虑多用户，不用bookpath为书籍关键字 */
		sb.append(",book_key TEXT NOT NULL");
		sb.append(",userId TEXT");
		sb.append(",spine INTEGER");
		sb.append(",total_spine INTEGER");
		sb.append(",page_spine INTEGER");
		sb.append(",content TEXT");
		sb.append(",create_time TEXT");
		sb.append(",object TEXT");
		sb.append(")");
		db.execSQL(sb.toString());
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		db.delete(TABLE_NAME, null, null);
		createTable(db);
	}

	public long addMark(SQLiteDatabase mDB, RMReadingMark Mark) {
		ContentValues cv = new ContentValues();
		cv.put("book_key", Mark.key);
		cv.put("userId", Mark.userId);
		cv.put("spine", Mark.spine);
		cv.put("total_spine", Mark.totalInSpine);
		cv.put("page_spine", Mark.pageInSpine);
		cv.put("object", Mark.object);
		cv.put("content", Mark.content);
		cv.put("create_time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
				Locale.CHINA).format(new Date()));
		long row = mDB.insert(TABLE_NAME, null, cv);
		return row;
	}

	public long deleteMark(SQLiteDatabase mDB, int MarkId) {
		String where = "id = ? ";
		long row = mDB.delete(TABLE_NAME, where, new String[] { MarkId + "" });
		return row;
	}

	public long deleteBookAllMark(SQLiteDatabase mDB, String bookKey,
			String userId) {
		String where = "book_key = ?";// and userId = ?
		long row = mDB.delete(TABLE_NAME, where, new String[] { bookKey });
		return row;
	}

	public RMReadingMark getMark(SQLiteDatabase mDB, int id) {
		RMReadingMark item = null;
		String sql = "SELECT * FROM " + TABLE_NAME + " where id = ?";
		Cursor c = mDB.rawQuery(sql, new String[] { id + "" });
		while (c.moveToNext()) {
			item = new RMReadingMark();
			item.id = c.getInt(c.getColumnIndex("id"));
			item.key = c.getString(c.getColumnIndex("book_key"));
			item.userId = c.getString(c.getColumnIndex("userId"));
			item.spine = c.getInt(c.getColumnIndex("spine"));
			item.totalInSpine = c.getInt(c.getColumnIndex("total_spine"));
			item.pageInSpine = c.getInt(c.getColumnIndex("page_spine"));
			item.content = c.getString(c.getColumnIndex("content"));
			item.object = c.getString(c.getColumnIndex("object"));
			item.createDate = c.getString(c.getColumnIndex("create_time"));
			break;
		}
		c.close();
		return item;
	}

	public List<RMReadingMark> getMarks(SQLiteDatabase mDB, String book_key,
			String userID) {
		List<RMReadingMark> list = new ArrayList<RMReadingMark>();
		String sql = "SELECT * FROM " + TABLE_NAME + "WHERE book_key = ? ";// and
																			// userId
																			// =
																			// ?
		Cursor c = mDB.rawQuery(sql, new String[] { book_key });
		while (c.moveToNext()) {
			RMReadingMark item = new RMReadingMark();
			item.id = c.getInt(c.getColumnIndex("id"));
			item.key = c.getString(c.getColumnIndex("book_key"));
			item.userId = c.getString(c.getColumnIndex("userId"));
			item.spine = c.getInt(c.getColumnIndex("spine"));
			item.totalInSpine = c.getInt(c.getColumnIndex("total_spine"));
			item.pageInSpine = c.getInt(c.getColumnIndex("page_spine"));
			item.content = c.getString(c.getColumnIndex("content"));
			item.object = c.getString(c.getColumnIndex("object"));
			item.createDate = c.getString(c.getColumnIndex("create_time"));
			list.add(item);
		}
		c.close();
		return list;
	}

}
