/**
 * @class RZPConverter.java
 * @date 2014-4-9
 * @copyright 版权(C)重庆软媒科技有限公司 2013-2014
 * @author vken
 */
package rmkj.lib.rzp.converter;

import java.io.File;

import rmkj.lib.rzp.core.RZPFile;
import rmkj.lib.rzp.exception.RZPException;
import rmkj.lib.rzp.model.RZPParameters;
import rmkj.lib.rzp.util.RZPConstants;
import rmkj.lib.rzp.util.RZPUtil;

public class RZPConverter {

	public static boolean zip2rzp(String zipFilePath, String rzpFilePath,
			String tmpFolder) throws RZPException {
		if (tmpFolder == null) {
			throw new RZPException("RZPConverter::zip2rzp invalid tmp folder");
		}
		if (RZPUtil.checkFileReadAccess(zipFilePath) == false) {
			throw new RZPException("RZPConverter::zip2rzp invalid zipFilePath:"
					+ zipFilePath);
		}

		// zip 解压文件到临时文件夹
		String tmpDir = tmpFolder;
		File tmpDirFile = new File(tmpDir);
		tmpDirFile.mkdirs();
		
		RZPFile zip = new RZPFile(zipFilePath);
		zip.setZipMode(RZPFile.RZPMODE_ZIP);
		zip.extractAll(tmpDir);

		// 把临时文件夹压缩到 rzp
		RZPFile rzp = new RZPFile(rzpFilePath);
		zip.setZipMode(RZPFile.RZPMODE_RZP);
		RZPParameters parameters = new RZPParameters();
		parameters.setCompressionMethod(RZPConstants.COMP_DEFLATE);
		parameters.setCompressionLevel(RZPConstants.DEFLATE_LEVEL_NORMAL);
		
		String[] files = tmpDirFile.list();
		if (files != null && files.length > 0) {
			for (String fileName : files) {
				File rzpItem = new File(tmpDir+File.separator+fileName);
				if (rzpItem.isDirectory()) {
					rzp.addFolder(rzpItem.getAbsolutePath(), parameters);
				} else if (rzpItem.isFile()) {
					rzp.addFile(rzpItem, parameters);
				}
			}
		}

		// 删除临时文件夹
		delete(tmpDirFile);
		return true;
	}

	public static boolean rzp2zip(String rzpFilePath, String zipFilePath,
			String tmpFolder) throws RZPException {

		if (tmpFolder == null) {
			throw new RZPException("RZPConverter::zip2rzp invalid tmp folder");
		}
		if (RZPUtil.checkFileReadAccess(rzpFilePath) == false) {
			throw new RZPException("RZPConverter::zip2rzp invalid rzpFilePath:"
					+ rzpFilePath);
		}

		// zip 解压文件到临时文件夹
		String tmpDir = tmpFolder ;
		File tmpDirFile = new File(tmpDir);
		tmpDirFile.mkdirs();
		
		RZPFile rzp = new RZPFile(rzpFilePath);
		rzp.setZipMode(RZPFile.RZPMODE_RZP);
		rzp.extractAll(tmpDir);

		// 把临时文件夹压缩到 rzp
		RZPFile zip = new RZPFile(zipFilePath);
		zip.setZipMode(RZPFile.RZPMODE_ZIP);
		RZPParameters parameters = new RZPParameters();
		parameters.setCompressionMethod(RZPConstants.COMP_DEFLATE);
		parameters.setCompressionLevel(RZPConstants.DEFLATE_LEVEL_NORMAL);

		String[] files = tmpDirFile.list();
		if (files != null && files.length > 0) {
			for (String fileName : files) {
				File rzpItem = new File(tmpFolder + File.separator+fileName);
				if (rzpItem.isDirectory()) {
					zip.addFolder(rzpItem.getAbsolutePath(), parameters);
				} else if (rzpItem.isFile()) {
					zip.addFile(rzpItem, parameters);
				}
			}
		}
		// 删除临时文件夹
		delete(tmpDirFile);
		return true;
	}

	// 刪除文件或者文件夾
	public static void delete(File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}

		if (file.isDirectory()) {
			File[] childFiles = file.listFiles();
			if (childFiles == null || childFiles.length == 0) {
				file.delete();
				return;
			}
			for (int i = 0; i < childFiles.length; i++) {
				delete(childFiles[i]);
			}
			file.delete();
		}
	}

}
