package rmkj.lib.read.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import rmkj.lib.read.epub.entity.RMEPUBSpan;
import rmkj.lib.read.js.entity.HighlightSelectionText;

public class RMReadingNote implements Serializable {
	private static final long serialVersionUID = 1232131311L;
	/** 笔记ID */
	public String noteID;
	/** 唯一确定Book的Key 可以是路径 */
	public String bookKey,userId;
	/** 选中文字 */
	public String selectText;
	/** 笔记 */
	public String noteText;
	/** 创建时间 */
	public String createDate;
	/** 需要替换和原html */
	public String jsonArray;
	/** 章节 */
	public int spine;
	/** 当前章节内所在页码 */
	public int pageInSpine;
	/** 当前章节总页码 */
	public int totalInSpine;
	/** 是否选中 */
	public boolean isCheck;
	/** 备用字段 */
	public String object;
	public String object1;
	public String object2;
	public String loginName;

	public List<RMEPUBSpan> toSpans() throws JSONException {
		JSONArray selectionReplaceArray = new JSONArray(jsonArray);
		List<RMEPUBSpan> list = new ArrayList<RMEPUBSpan>();
		for (int i = 0; i < selectionReplaceArray.length(); i++) {
			JSONObject object = selectionReplaceArray.getJSONObject(i);
			RMEPUBSpan span = new RMEPUBSpan();
			boolean isEnd = object.getBoolean("isEnd");
			String spannedText = object.getString("replaceHtml");
			String srcSelection = object.getString("srcSelection");
			String srcText = object.getString("srcHtml");
			span.isEnd = isEnd;
			span.srcSelection = srcSelection;
			span.replaceHtml = spannedText;
			span.srcHtml = srcText;
			list.add(span);
		}
		return list;
	}

	public static class Factory {

		public static RMReadingNote createFromHighlighText(
				HighlightSelectionText text) {
			if (text == null)
				return null;

			RMReadingNote note = new RMReadingNote();
			note.jsonArray = text.getSelectionReplaceArray();
			note.noteID = text.getSpanID();
			note.selectText = text.getSelectText();

			return note;
		}
	}
}
