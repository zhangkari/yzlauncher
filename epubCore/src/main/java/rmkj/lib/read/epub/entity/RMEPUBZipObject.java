package rmkj.lib.read.epub.entity;

import java.io.File;

import rmkj.lib.read.util.LogUtil;

public class RMEPUBZipObject extends RMEPUBObject {

	/**
	 * 拿到封面
	 */
	public String getCover() {
		String cover = opfManager.getMetadata().getMeta().get("cover");
		if (cover == null) {
			return null;
		}
		if (cover.contains(".jpg")) {
			return cover;
		}
		RMEPUBOPFManifestItem item = opfManager.getManifest().getItem(cover);
		if (item == null) {
			return null;
		}
		File f = new File(opfFolder, item.href);
		if (!f.exists()) {
			if (LogUtil.DEBUG) {
				LogUtil.e(this, "error: cover path:" + f.getAbsolutePath());
			}
			return null;
		}
		return item.href;
	}

	/**
	 * 返回spine所在路径
	 */
	public String getSpineFile(int spineIndex) {
		RMEPUBOPFManifestItem spineItem = getSpineItem(spineIndex);
		if (spineItem == null) {
			return null;
		}
		String spineFile = opfFolder + spineItem.href;
		return spineFile;
	}

	@Override
	public String getMediaType(String href) {
		RMEPUBOPFManifestItem item = opfManager.getManifest().getItemForHref(href);
		if (item == null) {
			return null;
		}
		return item.media_type;
	}
}
