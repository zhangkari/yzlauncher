package rmkj.lib.read.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RMUtilFileStream {
	// UTF-8 头部有三个字节，分别是：0xEF、0xBB、0xBF
	// UTF-16BE 头部有两个字节，分别是：0xFE、0xFF
	// UTF-16LE 头部有两个字节，分别是：0xFF、0xFE
	// UTF-32BE 头部有4个字节，分别是：0x00、0x00、0xFE、0xFF
	public static InputStream clearFileBom(File xmlFile) throws IOException {
		InputStream in = new FileInputStream(xmlFile);
		byte[] bom = new byte[3];
		in.read(bom, 0, bom.length);
		if ((bom[0] == (byte) 0xEF) && (bom[1] == (byte) 0xBB)
				&& (bom[2] == (byte) 0xBF)) {
			return in;
		}
		in.close();
		in = null;
		in = new FileInputStream(xmlFile);
		return in;
	}

	/**
	 * 保存字符串到文件
	 */
	public static void StringToFile(String str, String path) {

		File destFile = new File(path);
		destFile.getParentFile().mkdirs();

		FileOutputStream f = null;
		try {
			f = new FileOutputStream(path);
			f.write(str.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (f != null) {
					f.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		System.gc();
	}

	/**
	 * 读取文件 转换为字符串
	 */
	public static String getTextFromFile(String filePath)
			throws FileNotFoundException, IOException {
		File file = new File(filePath);
		BufferedReader br = new BufferedReader(new FileReader(file));
		try {
			StringBuffer sb = new StringBuffer();
			String str;
			while ((str = br.readLine()) != null) {
				sb.append(str);
				sb.append("\n");
			}
			return sb.toString();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static String getDecryptTextFromFile(InputStream in, String encode) throws FileNotFoundException, IOException {

		return FileEnCryptUtil.Deciphering(in, FileEnCryptUtil.key);

	}

	public static String getTextFromFile(InputStream in, String encode)
			throws FileNotFoundException, IOException {
		InputStreamReader isr = new InputStreamReader(in, encode);

		BufferedReader br = new BufferedReader(isr);
		try {
			StringBuffer sb = new StringBuffer();
			String str;
			while ((str = br.readLine()) != null) {
				sb.append(str);
				sb.append("\n");
			}
			return sb.toString();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

//	public static String getTextFromFile(InputStream in)
//			throws FileNotFoundException, IOException {
//		BufferedReader br = new BufferedReader(new InputStreamReader(in));
//		try {
//			StringBuffer sb = new StringBuffer();
//			String str;
//			while ((str = br.readLine()) != null) {
//				sb.append(str);
//				sb.append("\n");
//			}
//			return sb.toString();
//		} finally {
//			try {
//				br.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}
}
