package rmkj.lib.read.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class RMZipFileUtil {
	final static int FILE_IO_BUFFER = 1024 * 64;

	@SuppressWarnings("resource")
	public static boolean unzipTo(String zipFile, String unzipFolder) {
		ZipInputStream in = null;
		FileOutputStream os = null;

		File f = new File(unzipFolder);
		f.mkdirs();

		try {
			in = new ZipInputStream(new FileInputStream(zipFile));
			ZipEntry entry = null;
			while ((entry = in.getNextEntry()) != null) {
				String entryName = entry.getName();
				if (entry.isDirectory()) {
					File file = new File(unzipFolder + entryName);
					file.mkdirs();
					continue;
				} else {
					File file = new File(unzipFolder + "/" + entryName);
					if (!file.getParentFile().exists()) {
						file.getParentFile().mkdirs();
					}
					os = new FileOutputStream(unzipFolder + "/" + entryName);
					byte[] buf = new byte[1024];
					int len;
					while ((len = in.read(buf)) > 0) {
						os.write(buf, 0, len);
					}
					os.close();
					in.closeEntry();
				}
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.closeEntry();
				}
				if (os != null) {
					os.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return false;
	}

	public static byte[] getUnzipString(String zipFile) {
		if (zipFile == null)
			return null;

		try {
			ZipInputStream in = new ZipInputStream(new FileInputStream(zipFile));
			ZipEntry entry = in.getNextEntry();
			if (entry != null && (!entry.isDirectory())) {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				byte[] buf = new byte[4096];
				int len = 0;
				while ((len = in.read(buf)) > 0) {
					bos.write(buf, 0, len);
				}
				byte[] buffer = bos.toByteArray();
				in.closeEntry();
				return buffer;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ZipInputStream getUnzipStream(String zipFile) {
		if (zipFile == null)
			return null;

		try {
			ZipInputStream in = new ZipInputStream(new FileInputStream(zipFile));

			return in;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean zipFile(String srcFilePath, String destFilePath) {
		if (destFilePath == null || srcFilePath == null)
			return false;

		File srcFile = new File(srcFilePath);

		if (srcFile == null || (!srcFile.isFile()))
			return false;
		try {

			// 目标文件
			ZipOutputStream destZipOPS = new ZipOutputStream(new FileOutputStream(destFilePath));

			// 源文件
			FileInputStream inputStream = new FileInputStream(srcFile);

			// 压缩文件内部名称
			ZipEntry zipEntry = new ZipEntry(srcFile.getName());
			destZipOPS.putNextEntry(zipEntry);

			int len;
			byte[] buffer = new byte[4096];

			while ((len = inputStream.read(buffer)) != -1) {
				destZipOPS.write(buffer, 0, len);
			}

			destZipOPS.closeEntry();

			inputStream.close();

			// 生成目标文件
			destZipOPS.finish();
			destZipOPS.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@SuppressWarnings("resource")
	/**
	 * 压缩html
	 * @param zipFile
	 * @param unzipFolder
	 * @return
	 */
	public static boolean unzipEpubTo(String zipFile, String unzipFolder) {
		ZipInputStream in = null;
		FileOutputStream os = null;

		File f = new File(unzipFolder);
		if (f.mkdirs() == false)
			return false;

		try {
			in = new ZipInputStream(new FileInputStream(zipFile));
			ZipEntry entry = null;
			while ((entry = in.getNextEntry()) != null) {
				String entryName = entry.getName();
				if (entry.isDirectory()) {
					File file = new File(unzipFolder + entryName);
					file.mkdirs();
					continue;
				} else {

					boolean needZipChild = false;
					// html进行加密
					if (entryName.contains("htm") || entryName.contains("html") || entryName.contains("xhtml")) {
						needZipChild = true;
					}

					String childFilePath = unzipFolder + "/" + entryName;
					String childTmpFilePath = unzipFolder + "/" + entryName + ".tmp";

					File childFile = null;
					if (needZipChild) {
						childFile = new File(childTmpFilePath);
					} else
						childFile = new File(childFilePath);

					if (!childFile.getParentFile().exists()) {
						childFile.getParentFile().mkdirs();
					}
					os = new FileOutputStream(childFile.getPath());
					byte[] buf = new byte[1024];
					int len;
					while ((len = in.read(buf)) > 0) {
						os.write(buf, 0, len);
					}
					os.close();
					in.closeEntry();
					// 解压完成之后，把html压缩
					if (needZipChild) {
						zipFile(childTmpFilePath, childFilePath);
						// 删除临时文件
						childFile.delete();
					}
				}
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.closeEntry();
				}
				if (os != null) {
					os.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return false;
	}

	/**
	 * 从zip包获取指定文件流数据
	 * 
	 * @param zipFile
	 *            zip包路径
	 * @param childPath
	 *            需要获取的文件相对路径
	 * @return
	 * @throws IOException
	 */
	public static InputStream getFileFromZipFile(String zipFile, String childPath) throws IOException {
		ZipFile file = new ZipFile(zipFile);
		ZipEntry entry = file.getEntry(childPath);
		if (entry == null)
			return null;

		InputStream is = file.getInputStream(entry);
		return is;
	}

}
