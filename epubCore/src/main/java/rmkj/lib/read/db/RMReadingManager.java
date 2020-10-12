package rmkj.lib.read.db;

import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public class RMReadingManager {
	private RMReadingDBHelper DBhelper;
	private RMReadingMarkDB markDB;// 书签数据库操作类
	private RMReadingNoteDB noteDB;// 笔记数据库操作类
	private List<RMReadingMark> markList;// 当前书籍的所有书签
	private String book_key, userId;

	public RMReadingManager(Context context, String book_key, String userId) {
		this.book_key = book_key;
		this.userId = userId;
		DBhelper = new RMReadingDBHelper(context);
		markDB = new RMReadingMarkDB();
		noteDB = new RMReadingNoteDB();
		if (book_key != null) {
			markList = getMarks();
		}
	}

	public void refreshMark() {
		SQLiteDatabase mDB = DBhelper.getReadableDatabase();
		markList = markDB.getMarks(mDB, book_key, userId);
		mDB.close();
	}

	public boolean addNote(RMReadingNote note) {
		SQLiteDatabase mDB = DBhelper.getWritableDatabase();
		long row = noteDB.addNote(mDB, note);
		mDB.close();
		return row != -1;
	}

	/**
	 * 删除笔记
	 * 
	 * @param noteID
	 * @return
	 */
	public boolean deleteNote(String noteID) {
		SQLiteDatabase mDB = DBhelper.getWritableDatabase();
		long n = noteDB.deleteNote(mDB, noteID);
		mDB.close();
		return n != -1;
	}

	/**
	 * 删除某本书所有笔记
	 * 
	 * @param bookKey
	 * @return
	 */
	public boolean deleteBookAllNote(String bookKey) {
		SQLiteDatabase mDB = DBhelper.getWritableDatabase();
		long n = noteDB.deleteBookAllNote(mDB, bookKey);
		mDB.close();
		return n != -1;
	}

	/**
	 * 删除 某本书所有笔记和书签
	 * 
	 * @param bookKey
	 * @return
	 */
	public boolean deleteBookAllNoteAndMark(String bookKey, String userid) {
		SQLiteDatabase mDB = DBhelper.getWritableDatabase();
		long n = noteDB.deleteBookAllNote(mDB, bookKey);
		n = markDB.deleteBookAllMark(mDB, bookKey, userid);
		mDB.close();
		return n != -1;
	}

	/**
	 * 更新笔记
	 * 
	 * @param noteID
	 * @param noteText
	 * @return
	 */
	public boolean updateNote(String noteID, String noteText) {
		SQLiteDatabase mDB = DBhelper.getWritableDatabase();
		boolean n = noteDB.updateNote(mDB, noteID, noteText);
		mDB.close();
		return n;
	}

	/**
	 * 拿到数据库所有书的笔记
	 * 
	 * @return
	 */
	public List<RMReadingNote> getNotesInDB() {
		SQLiteDatabase mDB = DBhelper.getReadableDatabase();
		List<RMReadingNote> notes = noteDB.getNotesInDB(mDB);
		mDB.close();
		return notes;
	}

	/**
	 * 自定义sql查询
	 * 
	 * @param sql
	 * @return
	 */
	public List<RMReadingNote> queryNotes(String sql) {
		SQLiteDatabase mDB = DBhelper.getReadableDatabase();
		List<RMReadingNote> notes = noteDB.query(mDB, sql);
		mDB.close();
		return notes;
	}

	/**
	 * 拿到章节内的笔记
	 * 
	 * @param spine
	 * @return
	 */
	public List<RMReadingNote> getNotes(int spine) {
		SQLiteDatabase mDB = DBhelper.getReadableDatabase();
		List<RMReadingNote> notes = noteDB
				.getNotesInSpine(mDB, book_key, spine);
		mDB.close();
		return notes;
	}

	/**
	 * 拿到书籍所有的笔记
	 * 
	 * @param spine
	 * @return
	 */
	public List<RMReadingNote> getNotes() {
		SQLiteDatabase mDB = DBhelper.getReadableDatabase();
		List<RMReadingNote> notes = noteDB.getNotes(mDB, book_key);
		mDB.close();
		return notes;
	}
	
	public int getNoteSize(String bookkey) {
		SQLiteDatabase mDB = DBhelper.getReadableDatabase();
		List<RMReadingNote> notes = noteDB.getNotes(mDB, bookkey);
		mDB.close();
		return notes.size();
	}

	public List<RMReadingNote> getNotes(String bookkey) {
		SQLiteDatabase mDB = DBhelper.getReadableDatabase();
		List<RMReadingNote> notes = noteDB.getNotes(mDB, bookkey);
		mDB.close();
		return notes;
	}
	/**
	 * 根据Note ID 拿到笔记内容
	 * 
	 * @param noteID
	 * @return
	 */
	public RMReadingNote getNote(String noteID) {
		SQLiteDatabase mDB = DBhelper.getReadableDatabase();
		RMReadingNote note = noteDB.getNote(mDB, noteID);
		mDB.close();
		return note;
	}

	/**
	 * 拿到当前阅读书籍的所有书签
	 * 
	 * @return
	 */
	public List<RMReadingMark> getMarks() {
		SQLiteDatabase mDB = DBhelper.getReadableDatabase();
		List<RMReadingMark> list = markDB.getMarks(mDB, book_key, userId);
		mDB.close();
		return list;
	}

	/**
	 * 拿到当前阅读书籍 章节下固定页码的书签
	 * 
	 * @param spineIndex
	 * @param pageInSpine
	 * @param totalInSpine
	 * @return
	 */
	public RMReadingMark getMark(int spineIndex, int pageInSpine,
			int totalInSpine) {

		if (markList == null) {
			return null;
		}

		for (RMReadingMark mark : markList) {
			if (mark.spine == spineIndex) {
				if (totalInSpine == mark.totalInSpine) {
					if (pageInSpine == mark.pageInSpine) {
						return mark;
					}
				} else {
					float markPercent = (mark.pageInSpine * 1f)
							/ mark.totalInSpine;
					float currentPercent = (pageInSpine * 1f) / totalInSpine;
					float onePagePercent = 1f / totalInSpine;
					float fabs = Math.abs(markPercent - currentPercent);
					if (onePagePercent - fabs >= 0.000001f) {
						return mark;
					}
				}
			}
		}
		return null;
	}

	/**
	 * 删除笔记
	 */
	public boolean deleteMark(int... markID) {
		SQLiteDatabase mDB = DBhelper.getWritableDatabase();
		long num = -1;
		for (int i = 0; i < markID.length; i++) {
			long n = markDB.deleteMark(mDB, markID[i]);
			if (n != -1) {
				num++;
			}
		}
		markList = markDB.getMarks(mDB, book_key, userId);
		mDB.close();
		return num != -1;
	}

	/**
	 * 删除当前阅读 书籍所有书签
	 * 
	 * @param bookKey
	 * @return
	 */
	public boolean deleteBookAllMark(String bookKey, String userId) {
		SQLiteDatabase mDB = DBhelper.getWritableDatabase();
		long num = markDB.deleteBookAllMark(mDB, bookKey, userId);
		mDB.close();
		return num != -1;
	}

	/**
	 * 切换 （如果当前页存在书签则删除 否则添加书签）
	 * 
	 * @param spineIndex
	 * @param pageInSpine
	 * @param totalInSpine
	 * @param content
	 * @return
	 */
	public boolean toggleMark(int spineIndex, int pageInSpine,
			int totalInSpine, String content) {
		RMReadingMark mark = getMark(spineIndex, pageInSpine, totalInSpine);
		if (mark == null) {
			RMReadingMark newMark = new RMReadingMark(book_key, userId,
					spineIndex, pageInSpine, totalInSpine, content);
			SQLiteDatabase mDB = DBhelper.getWritableDatabase();
			markDB.addMark(mDB, newMark);
			markList = markDB.getMarks(mDB, book_key, userId);
			mDB.close();
			return true;
		}
		SQLiteDatabase mDB = DBhelper.getWritableDatabase();
		markDB.deleteMark(mDB, mark.id);
		markList = markDB.getMarks(mDB, book_key, userId);
		mDB.close();
		return false;
	}

	private class RMReadingDBHelper extends SQLiteOpenHelper {

		public RMReadingDBHelper(Context context) {
			super(context, "rmReading.db", null, 1);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			RMReadingMarkDB.createTable(db);
			RMReadingNoteDB.createTable(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			RMReadingMarkDB.onUpgrade(db, oldVersion, newVersion);
			RMReadingNoteDB.onUpgrade(db, oldVersion, newVersion);
		}

	}
	
	
	public List<RMReadingNote> queryLoginNotes(String sql,String loginName) {
		SQLiteDatabase mDB = DBhelper.getReadableDatabase();
		List<RMReadingNote> notes = noteDB.queryLogin(mDB, sql,loginName);
		mDB.close();
		return notes;
	}

}
