package rmkj.lib.read.epub.entity;

/**
 * NCX 文件
 * 
 * @author zsx
 * 
 */
public class RMEPUBNCXNavPoint {
	private String id;
	private String playOrder;
	private String text;
	private String src;
	private int level;
	private String srcChapter;
	private String srcA;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPlayOrder() {
		return playOrder;
	}

	public void setPlayOrder(String playOrder) {
		this.playOrder = playOrder;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
		if (src.contains("#")) {
			srcChapter = src.substring(0, src.lastIndexOf("#"));
			srcA = src.substring(src.lastIndexOf("#") + 1);
		} else {
			srcChapter = src;
		}
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getSrcForChapter() {
		return srcChapter;
	}

	public String getSrcForA() {
		return srcA;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("id:" + id);
		sb.append("\tplayOrder:" + playOrder);
		sb.append("\ttext:" + text);
		sb.append("\tsrc:" + src);
		sb.append("\tlevel:" + level);
		return sb.toString();
	}
}
