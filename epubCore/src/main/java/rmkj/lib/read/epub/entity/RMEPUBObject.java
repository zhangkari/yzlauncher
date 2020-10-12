package rmkj.lib.read.epub.entity;

import java.io.File;
import java.util.List;

import rmkj.lib.read.itf.IRMObejctInterface;
import rmkj.lib.read.util.LogUtil;

public class RMEPUBObject implements IRMObejctInterface {
	protected RMEPUBOPFManager opfManager;
	protected RMEPUBNCXManager ncxManager;
	protected RMEPUBContainer container;
	protected RMEPUBMimeType mimeType;
	protected String opfFolder;

	public RMEPUBOPFManager getOpfManager() {
		return opfManager;
	}

	public void setOpfManager(RMEPUBOPFManager opfManager) {
		this.opfManager = opfManager;
	}

	public RMEPUBNCXManager getNcxManager() {
		return ncxManager;
	}

	public void setNcxManager(RMEPUBNCXManager ncxManager) {
		this.ncxManager = ncxManager;
	}

	public RMEPUBContainer getContainer() {
		return container;
	}

	public void setContainer(RMEPUBContainer container) {
		this.container = container;
	}

	public RMEPUBMimeType getMimeType() {
		return mimeType;
	}

	public void setMimeType(RMEPUBMimeType mimeType) {
		this.mimeType = mimeType;
	}

	public String getOpfFolder() {
		return opfFolder;
	}

	public void setOpfFolder(String opfFolder) {
		this.opfFolder = opfFolder;
	}

	/**
	 * 检查是否有spine
	 */
	public boolean hasSpine(int spineIndex) {
		return opfManager.getSpine().isSpineValid(spineIndex);
	}

	/**
	 * 拿到目录名
	 */
	public String getSpineName(int spineIndex) {
		RMEPUBOPFManifestItem spineItem = getSpineItem(spineIndex);
		if (spineItem == null) {
			return null;
		}
		if (ncxManager == null) {
			return null;
		}
		return ncxManager.getSpineName(spineItem.href);
	}

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

		System.out.println("拿到封面-----" + item.href);

		return item.href;
	}

	/**
	 * 拿到书名
	 */
	public String getBookName() {
		return opfManager.getBookName();
	}

	/**
	 * 拿到作者
	 */
	public String getAuthor() {
		return opfManager.getBookAuthor();
	}

	/**
	 * 拿到Spine
	 */
	public RMEPUBOPFManifestItem getSpineItem(int spineIndex) {
		if (!hasSpine(spineIndex)) {
			return null;
		}
		String spineId = opfManager.getSpine().getList().get(spineIndex)
				.getItemref();
		RMEPUBOPFManifestItem spineItem = opfManager.getManifest().getItem(
				spineId);
		return spineItem;
	}

	public int getSpineIndex(String spineRelativePath) {
		if (spineRelativePath == null) {
			return -1;
		}
		spineRelativePath = spineRelativePath.substring(spineRelativePath.lastIndexOf("/") + 1, spineRelativePath.lastIndexOf("."));
		RMEPUBOPFManifest manifest = opfManager.getManifest();
		List<RMEPUBOPFSpineItem> list = opfManager.getSpine().getList();
		for (int i = 0; i < list.size(); i++) {
			RMEPUBOPFManifestItem item = manifest.getItem(list.get(i)
					.getItemref());
			if (item != null) {
				// if (spineRelativePath.equals(item.href)) {
				// return i;
				// }
				if (item.href.contains(spineRelativePath)) {
					return i;
				}
			}
		}
		return 0;
	}

	public int getTotalSpine() {
		return opfManager.getSpine().getList().size();
	}

	/**
	 * 返回spine所在路径
	 */
	public String getSpineFile(int spineIndex) {
		RMEPUBOPFManifestItem spineItem = getSpineItem(spineIndex);
		if (spineItem == null) {
			return null;
		}
		String spineFile = opfFolder + File.separator + spineItem.href;
		return spineFile;
	}

	@Override
	public String getSpineMimeType(int spineIndex) {
		RMEPUBOPFManifestItem spineItem = getSpineItem(spineIndex);

		// 如果没找到，默认返回 html类型
		if (spineItem == null) {
			return "text/html";
		}
		return spineItem.media_type;
	}

	// pageOrientation
	// layoutOrientation
	public boolean isVertical() {
		if (opfManager.getSpine().getPage_progression_direction() == null) {
			return false;
		}
		if (opfManager.getSpine().getPage_progression_direction().equals("rtl")) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isVerticalOrientation(int spineIndex) {
		if (hasSpine(spineIndex)) {
			String orientation = opfManager.getSpine().getOrientation();
			if (orientation != null) {
				if ("vertical".equals(orientation)) {
					return true;
				} else if ("horizontal".equals(orientation)) {
					return false;
				} else {
					if (LogUtil.DEBUG) {
						LogUtil.e(this,
								"error:opfManager.getSpine().getOrientation() is :"
										+ orientation);
					}
				}
			}
			orientation = opfManager.getSpine().getList().get(spineIndex)
					.getOrientation();
			if (orientation != null) {
				if ("vertical".equals(orientation)) {
					return true;
				} else if ("horizontal".equals(orientation)) {
					return false;
				} else {
					if (LogUtil.DEBUG) {
						LogUtil.e(this,
								"error:opfManager.getSpine().getOrientation() is :"
										+ orientation);
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean isRightPageOrientation() {
		if (opfManager.getSpine().getPage_progression_direction() == null) {
			return false;
		}
		if (opfManager.getSpine().getPage_progression_direction().equals("rtl")) {
			return true;
		}
		return false;
	}

	@Override
	public String getMediaType(String href) {
		RMEPUBOPFManifestItem item = opfManager.getManifest().getItemForHref(
				href);
		if (item == null) {
			return null;
		}
		return item.media_type;
	}

	@Override
	public String getSpineEncode(int spineIndex) {
		return "utf-8";
	}

}
