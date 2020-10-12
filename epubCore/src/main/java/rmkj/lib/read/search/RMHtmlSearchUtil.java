package rmkj.lib.read.search;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import rmkj.lib.read.util.LogUtil;
import rmkj.lib.read.util.RMUnicodeInputStream;
import android.util.Log;

public class RMHtmlSearchUtil {
	private static final int CONTEXT_LENGTH = 20;

	public static List<RMSearchSesultItem> searchOneFile(String key, String filePath, int spineIndex) {
		// 打开文档
		File inputFile = new File(filePath);
		boolean isInputFileExist = inputFile.exists();
		if (LogUtil.DEBUG) {
			Log.e("RMJsoupHtmlSearcher", "inputFile:" + inputFile.getPath() + "\t exists=" + isInputFileExist);
		}
		InputStream fis;
		try {
			fis = new FileInputStream(filePath);
			return searchOneFile(key, fis, spineIndex);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return new ArrayList<RMSearchSesultItem>();
	}

	public static List<RMSearchSesultItem> searchOneFile(String key, InputStream input, int spineIndex) {
		ArrayList<RMSearchSesultItem> lists = new ArrayList<RMSearchSesultItem>();
		try {
			Document doc = Jsoup.parse(new RMUnicodeInputStream(input, "UTF-8"), "UTF-8", "log");
			// FileInputStream fis = new FileInputStream(filePath);
			// Document doc = Jsoup.parse(fis, "UTF-8","");
			Element body = doc.body();
			long bodyLength = body.text().length();
			int offset = 0;
			List<Element> bodyElements = body.children();
			if (bodyElements == null || bodyElements.size() <= 0)
				return null;
			int index = 0;
			for (Element e : bodyElements) {
				int keyIndex = 0;
				String text = e.text();
				keyIndex = text.indexOf(key);
				while (keyIndex > 0) {
					offset += text.length();
					// 找到某个关键字
					// 取出关键字附近20个字符
					RMSearchSesultItem result = new RMSearchSesultItem();
					result.context = getContextString(text, CONTEXT_LENGTH, keyIndex);
					result.keyword = key;
					result.spineIndex = spineIndex;
					result.indexInSpine = index++;
					result.keywordIndex = offset + keyIndex;
					result.percent = (float) (keyIndex + offset) / bodyLength;
					lists.add(result);
					// 继续查找下一个关键字
					text = text.substring(keyIndex + key.length());
					keyIndex = text.indexOf(key);
					LogUtil.e(RMHtmlSearchUtil.class,"spine:"+result.spineIndex+"\t index:"+result.indexInSpine+"\r"+result.context);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return lists;
	}

	private static String getContextString(String src, int contextLength, int keyIndex) {
		int length = src.length();
		if (length <= contextLength)
			return src;
		else {
			int endIndex = 0;
			int startIndex = keyIndex - contextLength / 2;
			if (startIndex <= 0) {
				startIndex = 0;
			}
			endIndex = startIndex + contextLength;
			if (endIndex >= length) {
				endIndex = length - 1;
				startIndex = endIndex - contextLength;
				if (startIndex <= 0)
					startIndex = 0;
			}
			return src.substring(startIndex, endIndex);
		}
	}
}
