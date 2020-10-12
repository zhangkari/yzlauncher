package rmkj.lib.read.epub.entity;

import rmkj.lib.read.util.LogUtil;

/**
 * 映射 mimetype 文件
 * 
 * @author zsx
 * 
 */
public class RMEPUBMimeType {
	private String mimeType;

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public boolean isEpub() {
		if (mimeType == null) {
			if (LogUtil.DEBUG) {
				LogUtil.e(this, "mimeType:" + String.valueOf(mimeType));
			}
			return false;
		}
		if (mimeType.equals("application/epub+zip")) {
			return true;
		}
		if (LogUtil.DEBUG) {
			LogUtil.e(this, "mimeType:" + mimeType);
		}
		return false;
	}

	@Override
	public String toString() {
		return String.valueOf(mimeType);
	}
}
