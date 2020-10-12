package rmkj.lib.read.epub.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import rmkj.lib.exception.PRMException;
import rmkj.lib.read.epub.entity.RMEPUBMimeType;

/**
 * 解析 mimetype 文件
 * 
 * @author zsx
 * 
 */
public class PRMEPUBMimeTypeParser {
	public RMEPUBMimeType parserMimeType(String dirPath) throws IOException, PRMException {
		RMEPUBMimeType mimeType = new RMEPUBMimeType();
		File mimeTypeFile = new File(dirPath, "mimetype");
		if (mimeTypeFile.exists() && mimeTypeFile.isFile()) {
			BufferedReader read = null;
			try {
				read = new BufferedReader(new FileReader(mimeTypeFile.getPath()));
				mimeType.setMimeType(read.readLine());
			} finally {
				read.close();
			}
			return mimeType;
		} else {
			throw new PRMException(PRMException.ERROR_FILE_FORMART, "mimetype:" + mimeTypeFile.getPath());
		}
	}

	public RMEPUBMimeType parserMimeType(InputStream in) throws IOException, PRMException {
		RMEPUBMimeType mimeType = new RMEPUBMimeType();
		BufferedReader read = null;
		try {
			read = new BufferedReader(new InputStreamReader(in));
			mimeType.setMimeType(read.readLine());
		} finally {
			read.close();
		}
		return mimeType;
	}
}
