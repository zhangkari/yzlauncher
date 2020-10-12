package rmkj.lib.read.js;

import java.io.IOException;
import java.io.InputStream;

import rmkj.lib.read.util.LogUtil;

public class PRMJarResource {
	public String getJqueryJS() throws IOException {
		InputStream jqueryJS = getClass().getResourceAsStream("/rmkj/lib/read/js/jquery-1.9.1.min.js");
		return inputSreamToString(jqueryJS);
	}

	public String getDebugJS() throws IOException {
		InputStream debugJS = getClass().getResourceAsStream("/rmkj/lib/read/js/rmkj.debug.js");
		return inputSreamToString(debugJS);
	}

	public String getClientJS() throws IOException {
		InputStream clientJS = getClass().getResourceAsStream("/rmkj/lib/read/js/rmkj.client.js");
		return inputSreamToString(clientJS);
	}

	public String getEpubJS() throws IOException {
		InputStream epubJS = getClass().getResourceAsStream("/rmkj/lib/read/js/rmkj.epub.js");
		return inputSreamToString(epubJS);
	}

	public String getNativeJS() throws IOException {
		InputStream nativeJS = getClass().getResourceAsStream("/rmkj/lib/read/js/rmkj.native.js");
		return inputSreamToString(nativeJS);
	}
	public String getSearchJS() throws IOException {
		InputStream nativeJS = getClass().getResourceAsStream("/rmkj/lib/read/js/rmkj.search.js");
		return inputSreamToString(nativeJS);
	}

	private String inputSreamToString(InputStream is) throws IOException {
		if (is == null) {
			LogUtil.e("RMJarResource", "inputStream is null");
			return null;
		}
		StringBuilder sb = new StringBuilder();
		byte[] b = new byte[1024 * 4];
		int count = 0;
		while ((count = is.read(b)) != -1) {
			sb.append(new String(b, 0, count, "utf-8"));
		}
		return sb.toString();
	}
}
