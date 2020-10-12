package rmkj.lib.read.util;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

/**
 * 
 * 单独文件 异或运算 加密算法
 * 
 * @author li.c
 *
 */
public class FileEnCryptUtil {
	public static String key = "BFEBFBFF000006F6";
	/**
	 * 加密算法
	 * 
	 * @param path
	 * @param secretKey
	 * @return
	 * @throws Exception
	 *             2015年11月11日 li.c
	 */
	public static void EnCrypt(InputStream in, String secretKey, String target) {
		try {
			String key = MD5.getMD5(secretKey);
			key = key.substring(0, 8);
			byte[] keybytes = key.getBytes();
			byte[] cbytes = ByteFileUtill.toByteArray(in);
			for (int i = 0; i < cbytes.length; i++) {
				for (int j = keybytes.length - 1; j >= 0; j--) {
					cbytes[i] = (byte) (cbytes[i] ^ keybytes[j]);
				}
			}
			getFile(cbytes, target);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 解密算法
	 * 
	 * @param path
	 * @param secretKey
	 * @return
	 * @throws Exception
	 *             2015年11月11日 li.c
	 */
	public static String Deciphering(InputStream in, String secretKey) {
		String encrypt = "";
		try {
			String key = MD5.getMD5(secretKey);
			key = key.substring(0, 8);
			byte[] keybytes = key.getBytes();
			byte[] cbytes = ByteFileUtill.toByteArray(in);
			for (int i = 0; i < cbytes.length; i++) {
				for (int j = keybytes.length - 1; j >= 0; j--) {
					cbytes[i] = (byte) (cbytes[i] ^ keybytes[j]);
				}
			}
			encrypt = new String(cbytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.print(encrypt);
		return encrypt;
	}

	/**
	 * 根据byte数组，生成文件
	 */
	public static void getFile(byte[] bfile, String filePath) {
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		File file = null;
		try {
			file = new File(filePath);
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(bfile);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	/**
	 * 将string字符串保存入文件
	 * 
	 * @param result
	 * @param path
	 *            2015年11月11日 li.c
	 */
	public static void stringToFile(String result, String path) {
		try {
			File distFile = new File(path);
			Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(distFile), "UTF-8"));
			out.write(result);
			out.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			File file = new File(path);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());

			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(result);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 判断文件的编码格式
	 * 
	 * @param fileName
	 *            :file
	 * @return 文件编码格式
	 * @throws Exception
	 */
	public static String codeString(String fileName) throws Exception {
		BufferedInputStream bin = new BufferedInputStream(new FileInputStream(fileName));

		int p = (bin.read() << 8) + bin.read();
		String codes = null;

		switch (p) {
		case 0xefbb:
			codes = "UTF-8";
			break;
		case 0xfffe:
			codes = "Unicode";
			break;
		case 0xfeff:
			codes = "UTF-16BE";
			break;
		default:
			codes = "GBK";
		}
		bin.close();
		return codes;
	}

	/**
	 * 单文件加密测试方法
	 * 
	 * @param args
	 * @throws Exception
	 *             2015年11月11日 li.c
	 */
	public static void main(String[] args) throws Exception {
		String source = "D:/5n.html";
		String target = "D:/5nn.html";
		String secretKey = "BFEBFBFF000006F6";
		// EnCrypt(source, secretKey, target);
		System.out.print("EnCrypt Done");
		// Deciphering(target, secretKey);
		System.out.print("Deciphering Done");
	}
}
