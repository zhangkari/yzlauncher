package rmkj.lib.read.epub.entity;

/**
 * 映射 META-INF/container 文件信息
 * 
 * @author zsx
 * 
 */
public class RMEPUBContainer {
	private String fullPath;
	private String mediaType;

	public String getFullPath() {
		return fullPath;
	}

	public void setFullPath(String fullPath) {
		this.fullPath = fullPath;
	}

	public String getMediaType() {
		return mediaType;
	}

	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}

	@Override
	public String toString() {
		return "fullPath:" + fullPath + "\tmediaType:" + mediaType;
	}

}
