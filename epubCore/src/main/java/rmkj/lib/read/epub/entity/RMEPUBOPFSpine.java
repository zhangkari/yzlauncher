package rmkj.lib.read.epub.entity;

import java.util.ArrayList;
import java.util.List;

import rmkj.lib.read.util.LogUtil;

public class RMEPUBOPFSpine {

	private List<RMEPUBOPFSpineItem> list = new ArrayList<RMEPUBOPFSpineItem>();
	private String toc;
	private String page_progression_direction;
	/** 非固定有的 */
	private String orientation;

	public String getOrientation() {
		return orientation;
	}

	public void setOrientation(String orientation) {
		this.orientation = orientation;
	}

	public String getPage_progression_direction() {
		return page_progression_direction;
	}

	public void setPage_progression_direction(String page_progression_direction) {
		this.page_progression_direction = page_progression_direction;
	}

	public String getToc() {
		return toc;
	}

	public void setToc(String toc) {
		this.toc = toc;
	}

	public void addItemref(RMEPUBOPFSpineItem idref) {
		list.add(idref);
	}

	public List<RMEPUBOPFSpineItem> getList() {
		return list;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("toc:" + toc);
		sb.append("\n");
		for (RMEPUBOPFSpineItem idref : list) {
			sb.append("idref:" + idref.getItemref());
			sb.append("\n");
		}
		return sb.toString();
	}

	public boolean isSpineValid(int spineIndex) {
		if (spineIndex >= 0 && spineIndex < list.size())
			return true;
		else
			return false;
	}
}
