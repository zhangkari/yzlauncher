package rmkj.lib.read.epub.entity;

/**
 * NCX文件 head
 * 
 * @author zsx
 * 
 */
public class RMEPUBNCXHead {
	private String uid;
	private String depth;
	private String totalPageCount;// 默认0
	private String maxPageNumber;// 默认0

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getDepth() {
		return depth;
	}

	public void setDepth(String depth) {
		this.depth = depth;
	}

	public String getTotalPageCount() {
		return totalPageCount;
	}

	public void setTotalPageCount(String totalPageCount) {
		this.totalPageCount = totalPageCount;
	}

	public String getMaxPageNumber() {
		return maxPageNumber;
	}

	public void setMaxPageNumber(String maxPageNumber) {
		this.maxPageNumber = maxPageNumber;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("uid:" + String.valueOf(uid));
		sb.append("\t");
		sb.append("depth:" + String.valueOf(depth));
		sb.append("\t");
		sb.append("totalPageCount:" + String.valueOf(totalPageCount));
		sb.append("\t");
		sb.append("maxPageNumber:" + String.valueOf(maxPageNumber));
		return sb.toString();
	}
}
