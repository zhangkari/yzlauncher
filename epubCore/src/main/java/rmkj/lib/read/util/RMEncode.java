package rmkj.lib.read.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class RMEncode {
	public static final String UTF8 = "utf-8";
	public static final String UNICODE = "unicode";
	public static final String UTF16BE = "utf-16be";
	public static final String GBK = "gbk";

	public static String getTxtEncode(String fileName) throws IOException {
		InputStream is = new FileInputStream(fileName);
		String ret = getTxtEncode(is);

		is.close();
		return ret;
	}

	/**
	 * 获取文本编码
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static String getTxtEncode(InputStream is) throws IOException {
		BufferedInputStream bin = new BufferedInputStream(is);
		int p = (bin.read() << 8) + bin.read();
		String code = null;
		switch (p) {
		case 0xefbb:
			code = "utf-8";
			break;
		case 0xfffe:
			code = "unicode";
			break;
		case 0xfeff:
			code = "utf-16be";
			break;
		default:
			code = "gbk";
		}

		return code;

	}
}
