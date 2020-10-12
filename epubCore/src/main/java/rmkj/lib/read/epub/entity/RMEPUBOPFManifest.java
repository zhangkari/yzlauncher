package rmkj.lib.read.epub.entity;

import java.util.HashMap;
import java.util.Map;

import rmkj.lib.read.util.LogUtil;

public class RMEPUBOPFManifest {
	private Map<String, RMEPUBOPFManifestItem> map = new HashMap<String, RMEPUBOPFManifestItem>();

	public RMEPUBOPFManifestItem getItem(String id) {
		return map.get(id);
	}

	public RMEPUBOPFManifestItem getItemForHref(String href) {
		for (String id : map.keySet()) {
			if (map.get(id).href.equals(href)) {
				return map.get(id);
			}
		}
		return null;
	}

	public void addItem(String id, String href, String media_type) {
		if (id == null) {
			if (LogUtil.DEBUG) {
				LogUtil.e(this, "add OPFManifest id:" + String.valueOf(id));
			}
			return;
		}
		map.put(id, new RMEPUBOPFManifestItem(href, media_type));
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (String key : map.keySet()) {
			sb.append("id:" + key);
			sb.append("\t");
			sb.append("href:" + map.get(key).href);
			sb.append("\t");
			sb.append("media_type:" + map.get(key).media_type);
			sb.append("\n");
		}
		sb.append("");
		return sb.toString();
	}
}
