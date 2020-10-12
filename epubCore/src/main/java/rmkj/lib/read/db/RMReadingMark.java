package rmkj.lib.read.db;

import java.io.Serializable;

public class RMReadingMark implements Serializable {
	private static final long serialVersionUID = 5435685516652306883L;
	/** id */
	public int id;
	/** 关联书籍表的主键 */
	public String key, userId;
	/** 文本 */
	public String content;
	/** 创建时间 */
	public String createDate;
	/** 当前章节 */
	public int spine;
	/** 章节内的总页码 */
	public int totalInSpine;
	/** 章节内的当前页码 */
	public int pageInSpine;
	/** 是否选中 */
	public boolean isCheck = false;
	/** 备用字段 */
	public String object;

	public RMReadingMark() {
	}

	public RMReadingMark(String key, String userId, int spine, int pageInSpine,
			int totalInSpine, String content) {
		this.key = key;
		this.userId = userId;
		this.spine = spine;
		this.pageInSpine = pageInSpine;
		this.totalInSpine = totalInSpine;
		this.content = content;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("id:" + id);
		sb.append("key:" + key);
		sb.append("userId" + userId);
		sb.append("content:" + content);
		sb.append("spine:" + spine);
		sb.append("totalInSpine:" + totalInSpine);
		sb.append("pageInSpine:" + pageInSpine);
		return sb.toString();
	}
}
