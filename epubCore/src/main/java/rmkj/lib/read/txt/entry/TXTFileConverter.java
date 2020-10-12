package rmkj.lib.read.txt.entry;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import rmkj.lib.read.util.RMEncode;

public class TXTFileConverter {

	private static void toOneHtml(InputStream is,String encoding, String htmlPath) {
		if (is == null || htmlPath == null)
			return ;

		
		try {
			File htmlFile = new File(htmlPath);
			if(htmlFile.getParentFile()!=null)
			{
				htmlFile.getParentFile().mkdirs();
			}
			htmlFile.createNewFile();

			InputStreamReader isr = new InputStreamReader(is, encoding);
			BufferedReader br = new BufferedReader(isr);
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(htmlFile), encoding));

			String str = null;
			String htmlHeader = String.format(
					"<html><head><meta charset=\"%s\"></head><body>", encoding);
			pw.print(htmlHeader);

			while ((str = br.readLine()) != null) {
				pw.print("<p>");
				pw.print(str.replaceAll(">", "&gt;").replaceAll("<", "&lt;"));
				pw.print("</p>");
			}
			pw.print("</body></html>");
			br.close();
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 返回编码
	 * 
	 * @param htmlFilePath
	 * @return
	 */
	public static String toOneHtml(String txtPath, String htmlPath) {
		if (txtPath == null || htmlPath == null)
			return null;
		String encoding = null;
		try {
			encoding = RMEncode.getTxtEncode(txtPath);
			InputStream is = new FileInputStream(txtPath);
			toOneHtml(is,encoding,htmlPath);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return encoding;
	}
}
