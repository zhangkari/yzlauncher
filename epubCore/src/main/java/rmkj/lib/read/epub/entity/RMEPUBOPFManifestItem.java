package rmkj.lib.read.epub.entity;

public class RMEPUBOPFManifestItem {
	public String href;
	public String media_type;

	public RMEPUBOPFManifestItem(String href, String media_type) {
		this.href = href;
		this.media_type = media_type;
	}

	@Override
	public String toString() {
		return "href:" + href + "\t" + "media_type:" + media_type;
	}
}
