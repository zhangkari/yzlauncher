package rmkj.lib.read.db;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import rmkj.lib.read.util.LogUtil;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class RMReadingNoteDB {

	public static final String TABLE_NAME = " tb_book_notes ";

	public static void createTable(SQLiteDatabase db) {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE " + TABLE_NAME + "(");
		sb.append("id TEXT PRIMARY KEY");
		sb.append(",book_key TEXT");
		sb.append(",spine INTEGER");
		sb.append(",total_in_spine INTEGER");
		sb.append(",page_in_spine INTEGER");
		sb.append(",selectText TEXT");
		sb.append(",selectionReplaceArray TEXT");
		sb.append(",noteText TEXT");
		sb.append(",create_time TEXT");
		sb.append(",object TEXT");
		sb.append(",object1 TEXT");	
		sb.append(",object2 TEXT");
		sb.append(",loginName TEXT");
		sb.append(")");
		db.execSQL(sb.toString());
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		db.delete(TABLE_NAME, null, null);
		createTable(db);
	}

	public long addNote(SQLiteDatabase mDB, RMReadingNote note) {
		ContentValues cv = new ContentValues();
		cv.put("id", note.noteID);
		cv.put("book_key", note.bookKey);
		cv.put("selectionReplaceArray", note.jsonArray);
		cv.put("selectText", note.selectText);
		cv.put("noteText", note.noteText);
		cv.put("object", note.object);
		cv.put("object1", note.object1);
		cv.put("object2", note.object2);
		cv.put("loginName", note.loginName);
		cv.put("spine", note.spine);
		cv.put("page_in_spine", note.pageInSpine);
		cv.put("total_in_spine", note.totalInSpine);
		cv.put("create_time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
				Locale.CHINA).format(new Date()));
		long row = mDB.insert(TABLE_NAME, null, cv);
		return row;
	}

	public long deleteBookAllNote(SQLiteDatabase mDB, String bookKey) {
		long n = mDB.delete(TABLE_NAME, " book_key = ? ",
				new String[] { bookKey });
		return n;
	}

	public long deleteNote(SQLiteDatabase mDB, String noteId) {
		long n = mDB.delete(TABLE_NAME, " id = ? ", new String[] { noteId });
		return n;
	}

	public RMReadingNote getNote(SQLiteDatabase mDB, String id) {
		if (LogUtil.DEBUG) {
			if (id == null) {
				LogUtil.e(this, "getNote() id is null");
			}
		}
		RMReadingNote note = null;
		String sql = "SELECT * FROM " + TABLE_NAME + " where id = ?";
		Cursor c = mDB.rawQuery(sql, new String[] { String.valueOf(id) });
		while (c.moveToNext()) {
			note = Cursor2Note(c);
			break;
		}
		c.close();
		return note;
	}

	public boolean updateNote(SQLiteDatabase mDB, String id, String noteText) {
		if (LogUtil.DEBUG) {
			if (id == null) {
				LogUtil.e(this, "getNote() id is null");
			}
		}
		String sql = "UPDATE " + TABLE_NAME + " SET noteText = ? where id = ?";
		mDB.execSQL(sql, new String[] { noteText, String.valueOf(id) });
		return true;
	}

	public List<RMReadingNote> query(SQLiteDatabase mDB, String sql) {
		List<RMReadingNote> list = new ArrayList<RMReadingNote>();
		Cursor c = mDB.rawQuery(sql, null);
		while (c.moveToNext()) {
			RMReadingNote note = Cursor2Note(c);
			list.add(note);
		}
		c.close();
		return list;
	}

	protected RMReadingNote Cursor2Note(Cursor c) {
		RMReadingNote note = new RMReadingNote();
		note.noteID = c.getString(c.getColumnIndex("id"));
		note.object = c.getString(c.getColumnIndex("object"));
		note.object1 = c.getString(c.getColumnIndex("object1"));
		note.object2 = c.getString(c.getColumnIndex("object2"));
		note.bookKey = c.getString(c.getColumnIndex("book_key"));
		note.jsonArray = c.getString(c.getColumnIndex("selectionReplaceArray"));
		note.selectText = c.getString(c.getColumnIndex("selectText"));
		note.noteText = c.getString(c.getColumnIndex("noteText"));
		note.spine = c.getInt(c.getColumnIndex("spine"));
		note.totalInSpine = c.getInt(c.getColumnIndex("total_in_spine"));
		note.pageInSpine = c.getInt(c.getColumnIndex("page_in_spine"));
		note.createDate = c.getString(c.getColumnIndex("create_time"));
		return note;
	}

	public List<RMReadingNote> getNotesInDB(SQLiteDatabase mDB) {
		List<RMReadingNote> list = new ArrayList<RMReadingNote>();
		String sql = "SELECT * FROM" + TABLE_NAME;
		Cursor c = mDB.rawQuery(sql, null);
		while (c.moveToNext()) {
			RMReadingNote note = Cursor2Note(c);
			list.add(note);
		}
		c.close();
		return list;
	}


	public List<RMReadingNote> getNotes(SQLiteDatabase mDB, String bookKey) {
		List<RMReadingNote> list = new ArrayList<RMReadingNote>();
		String sql = "SELECT * FROM" + TABLE_NAME + "WHERE book_key = ?";
		Cursor c = mDB.rawQuery(sql, new String[] { bookKey });
		while (c.moveToNext()) {
			RMReadingNote note = Cursor2Note(c);
			list.add(note);
		}
		c.close();
		return list;
	}

	public List<RMReadingNote> getNotesInSpine(SQLiteDatabase mDB,
			String boobKey, int spine) {
		List<RMReadingNote> list = new ArrayList<RMReadingNote>();
		String sql = "SELECT * FROM" + TABLE_NAME
				+ "WHERE (spine = ? AND book_key =?)";
		Cursor c = mDB.rawQuery(sql, new String[] { spine + "", boobKey });
		while (c.moveToNext()) {
			RMReadingNote note = Cursor2Note(c);
			list.add(note);
		}
		c.close();
		return list;
	}
	
	
	public List<RMReadingNote> queryLogin(SQLiteDatabase mDB, String sql,String loginName) {
		List<RMReadingNote> list = new ArrayList<RMReadingNote>();
		Cursor c = mDB.rawQuery(sql, new String[]{loginName});
		while (c.moveToNext()) {
			RMReadingNote note = Cursor2Note(c);
			list.add(note);
		}
		c.close();
		return list;
	}
}
