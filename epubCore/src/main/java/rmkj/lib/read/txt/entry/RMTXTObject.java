package rmkj.lib.read.txt.entry;

import java.io.File;

import rmkj.lib.read.epub.entity.RMEPUBOPFManifestItem;
import rmkj.lib.read.itf.IRMObejctInterface;

public class RMTXTObject implements IRMObejctInterface {
	
	public static final String DEFAULT_HTML_NAME = "chapter0.html";  
	
	private String workPath;
	private String testFile = RMTXTObject.DEFAULT_HTML_NAME;
	private String encode = "utf-8";

	public void setWorkPath(String workPath) {
		this.workPath = workPath;
	}

	public void setEncode(String encode) {
		this.encode = encode;
	}

	@Override
	public boolean hasSpine(int spineIndex) {
		// TODO
		if (spineIndex == 0) {
			return true;
		}
		return false;
	}

	@Override
	public String getSpineName(int spineIndex) {
		// TODO
		return null;
	}

	@Deprecated
	@Override
	public String getCover() {
		return null;
	}

	@Override
	public String getBookName() {
		// TODO
		return null;
	}

	@Deprecated
	@Override
	public String getAuthor() {
		return null;
	}

	@Override
	public RMEPUBOPFManifestItem getSpineItem(int spineIndex) {
		RMEPUBOPFManifestItem item = null;
		if (spineIndex == 0) {
			item = new RMEPUBOPFManifestItem(testFile, "txt");
		}
		return item;
	}

	@Override
	public int getSpineIndex(String spineRelativePath) {
		// TODO
		return 0;
	}

	@Deprecated
	@Override
	public int getTotalSpine() {
		// TODO
		return 1;
	}

	@Deprecated
	@Override
	public String getSpineFile(int spineIndex) {
		File f = new File(workPath, testFile);
		return f.getAbsolutePath();
	}

	@Deprecated
	@Override
	public boolean isVerticalOrientation(int spineIndex) {
		return false;
	}

	@Deprecated
	@Override
	public boolean isRightPageOrientation() {
		return false;
	}

	@Deprecated
	@Override
	public String getMediaType(String href) {
		return null;
	}

	@Deprecated
	@Override
	public String getOpfFolder() {
		return null;
	}

	@Override
	public String getSpineMimeType(int spineIndex) {
		return "text/html";
	}

	@Override
	public String getSpineEncode(int spineIndex) {
		return encode;
	}

}
