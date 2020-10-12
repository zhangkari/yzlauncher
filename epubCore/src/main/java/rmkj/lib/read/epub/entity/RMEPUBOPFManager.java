package rmkj.lib.read.epub.entity;


/**
 * 映射OPF文件
 * 
 * @author zsx
 * 
 */
public class RMEPUBOPFManager {
	private RMEPUBOPFGuide guide = null;
	private RMEPUBOPFManifest manifest = null;
	private RMEPUBOPFMeta metadata = null;
	private RMEPUBOPFSpine spine = null;

	public RMEPUBOPFGuide getGuide() {
		return guide;
	}

	public void setGuide(RMEPUBOPFGuide guide) {
		this.guide = guide;
	}

	public RMEPUBOPFManifest getManifest() {
		return manifest;
	}

	public void setManifest(RMEPUBOPFManifest manifest) {
		this.manifest = manifest;
	}

	public RMEPUBOPFMeta getMetadata() {
		return metadata;
	}

	public void setMetadata(RMEPUBOPFMeta metadata) {
		this.metadata = metadata;
	}

	public RMEPUBOPFSpine getSpine() {
		return spine;
	}

	public void setSpine(RMEPUBOPFSpine spine) {
		this.spine = spine;
	}

	public String getBookName() {
		if (metadata == null)
			return null;
		return metadata.getTitle();
	}

	public String getBookAuthor() {
		if (metadata == null) {
			return null;
		}
		return metadata.getCreator();
	}

	public String getBookDate() {
		if (metadata == null) {
			return null;
		}
		return metadata.getDate();
	}
}
