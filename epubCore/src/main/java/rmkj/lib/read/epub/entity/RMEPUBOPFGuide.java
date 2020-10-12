package rmkj.lib.read.epub.entity;

import java.util.HashMap;
import java.util.Map;

public class RMEPUBOPFGuide {
	private Map<String, GuideItem> map = new HashMap<String, RMEPUBOPFGuide.GuideItem>();

	public void addGuideItem(String type, String title, String href) {
		if (type == null) {
			return;
		}
		map.put(type, new GuideItem(title, href));
	}

	public Map<String, GuideItem> getMap() {
		return map;
	}

	public class GuideItem {
		public String title;
		public String href;

		public GuideItem(String title, String href) {
			this.title = title;
			this.href = href;
		}
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (String key : map.keySet()) {
			sb.append("type:" + key);
			sb.append("\t");
			sb.append("title:" + map.get(key).title);
			sb.append("\t");
			sb.append("href:" + map.get(key).href);
		}
		return sb.toString();
	}
}
