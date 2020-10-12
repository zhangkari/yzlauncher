package com.yz.books.utils

import android.util.Log
import com.yz.books.common.Constant
import com.yz.books.ext.getApplicationContext
import java.io.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import kotlin.experimental.and


/**
 * @author lilin
 * @time on 2020-01-16 15:51
 */
object FileUtils {

    fun getLocalPath(): String {
        return "${getUsableStoragePath()}/yzbooks/"
    }

    /**
     * 获取可用存储路径
     */
    fun getUsableStoragePath(): String {
        val path = NativeTxtUtils.getInstance().getStringXmlValue(Constant.READ_PATH_KEY)
        if (path == null || path.isEmpty()) {
            return Constant.ROOT_PATH
        }
        if (path.contains(",")) {
            val paths = path.split(",")
            try {
                if (File(paths[0]).exists()) {
                    return paths[0]
                }
                if (File(paths[1]).exists()) {
                    return paths[1]
                }
                ToastUtils.showToast(getApplicationContext(), "硬盘存储错误")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return path
    }

    /**
     * 创建数据库文件
     * @param fileName
     */
    fun createDBFile(fileName: String): File {
        //Environment.getExternalStorageDirectory()
        val appDir = File(getUsableStoragePath(), "/yzbooks/${Constant.DB_PATH}")
        if (!appDir.exists()) {
            appDir.mkdirs()
        }
        return File(appDir, fileName)
    }

    /**
     * 创建临时文件
     * @param bookName
     */
    fun createAudioFile(bookName: String?, fileName: String): File {
        //Environment.getExternalStorageDirectory()
        val appDir = File(getUsableStoragePath(), "/yzbooks/$bookName")
        if (!appDir.exists()) {
            appDir.mkdirs()
        }
        return File(appDir, fileName)
    }

    /**
     * 创建临时文件
     * @param fileName
     */
    fun createTempFile(fileName: String, filePath: String? = null): File {
        //Environment.getExternalStorageDirectory()
        val appDir = if (filePath == null) {
            File(getUsableStoragePath(), "/yzbooks")
        } else {
            File(getUsableStoragePath(), "/yzbooks$filePath")
        }
        if (!appDir.exists()) {
            appDir.mkdirs()
        }
        return File(appDir, fileName)
    }

    /**
     * 创建txt文件
     * @param fileName
     */
    fun createRecordTxtFile(fileName: String): File? {
        return try {
            val file = File(getLocalPath(), fileName)
            if (!file.exists()) {
                file.createNewFile()
            }
            file
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 创建临时文件
     * @param path
     */
    /*fun createTempFile(path: String?): File {
        val appDir = File(Environment.getExternalStorageDirectory(), path)
        if (!appDir.exists()) {
            appDir.mkdirs()
        }
        val date = Date(System.currentTimeMillis()) // 系统当前时间
        val dateFormat = SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss", Locale.CHINA)
        val fileName = dateFormat.format(date) + ".jpg"
        return File(appDir, fileName)
    }*/

    /**
     * 删除文件
     * @param file
     */
    fun deleteFile(file: File?): Boolean {
        try {
            if (file?.exists() == true) {
                return file.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * 删除文件
     * @param path
     */
    fun deleteFile(path: String?): Boolean {
        if (!path.isNullOrEmpty()) {
            return deleteFile(File(path))
        }
        return false
    }

    /**
     * 删除文件夹以下面文件
     * @param path
     */
    fun deleteDirectory(path: String): Boolean {
        var filePath = path
        var flag = false
        //如果filePath不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator)) {
            filePath += File.separator
        }
        val dirFile = File(filePath)
        if (!dirFile.exists() || !dirFile.isDirectory) {
            return false
        }
        flag = true

        val files = dirFile.listFiles() ?: return false

        //遍历删除文件夹下的所有文件(包括子目录)
        //遍历删除文件夹下的所有文件(包括子目录)
        for (i in files.indices) {
            if (files[i].isFile) {
                //删除子文件
                flag = deleteFile(files[i].absolutePath)
                if (!flag) break
            } else {
                //删除子目录
                flag = deleteDirectory(files[i].absolutePath)
                if (!flag) break
            }
        }
        return if (!flag) false else dirFile.delete()
    }

    /**
     * 获取文件md5
     */
    @Throws(NoSuchAlgorithmException::class, IOException::class)
    fun getFileMD5(filePath: String): String? {
        val `in`: InputStream = FileInputStream(File(filePath))
        val md5 = StringBuffer()
        val md: MessageDigest = MessageDigest.getInstance("MD5")
        val dataBytes = ByteArray(1024)
        var nread = 0
        while (`in`.read(dataBytes).also { nread = it } != -1) {
            md.update(dataBytes, 0, nread)
        }
        val mdbytes: ByteArray = md.digest()
        // convert the byte to hex format
        for (i in mdbytes.indices) {
            md5.append(
                ((mdbytes[i] and 0xff.toByte()) + 0x100).toString(16).substring(1)
            )
        }
        return md5.toString().toLowerCase()
    }

    /**
     * android10访问文件的问题
     * @param context
     * @return
     */
    /*public String getPath(Context context){
      String path;
      if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
        path = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/luban/image/";
      } else {
        path = Environment.getExternalStorageDirectory() + "/luban/image/";
      }
      File file = new File(path);
      if (file.mkdirs()) {
        return path;
      }
      return path;
    }*/

    /**
     * 复制文件夹及其中的文件
     *
     * @param oldPath String 原文件夹路径 如：data/user/0/com.test/files
     * @param newPath String 复制后的路径 如：data/user/0/com.test/cache
     * @return `true` if and only if the directory and files were copied;
     * `false` otherwise
     */
    fun copyFolder(oldPath: String, newPath: String): Boolean {
        return try {
            val newFile = File(newPath)
            if (!newFile.exists()) {
                if (!newFile.mkdirs()) {
                    Log.e("--Method--", "copyFolder: cannot create directory.")
                    return false
                }
            }
            val oldFile = File(oldPath)
            val files = oldFile.list()
            var temp: File
            for (file in files) {
                temp = if (oldPath.endsWith(File.separator)) {
                    File(oldPath + file)
                } else {
                    File(oldPath + File.separator + file)
                }
                if (temp.isDirectory) {   //如果是子文件夹
                    copyFolder("$oldPath/$file", "$newPath/$file")
                } else if (!temp.exists()) {
                    Log.e("--Method--", "copyFolder:  oldFile not exist.")
                    return false
                } else if (!temp.isFile) {
                    Log.e("--Method--", "copyFolder:  oldFile not file.")
                    return false
                } else if (!temp.canRead()) {
                    Log.e("--Method--", "copyFolder:  oldFile cannot read.")
                    return false
                } else {
                    val fileInputStream = FileInputStream(temp)
                    val fileOutputStream = FileOutputStream(newPath + "/" + temp.name)
                    val buffer = ByteArray(1024)
                    var byteRead: Int
                    while (fileInputStream.read(buffer).also { byteRead = it } != -1) {
                        fileOutputStream.write(buffer, 0, byteRead)
                    }
                    fileInputStream.close()
                    fileOutputStream.flush()
                    fileOutputStream.close()
                }

                /* 如果不需要打log，可以使用下面的语句
                if (temp.isDirectory()) {   //如果是子文件夹
                    copyFolder(oldPath + "/" + file, newPath + "/" + file);
                } else if (temp.exists() && temp.isFile() && temp.canRead()) {
                    FileInputStream fileInputStream = new FileInputStream(temp);
                    FileOutputStream fileOutputStream = new FileOutputStream(newPath + "/" + temp.getName());
                    byte[] buffer = new byte[1024];
                    int byteRead;
                    while ((byteRead = fileInputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, byteRead);
                    }
                    fileInputStream.close();
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
                */
            }
            true
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            false
        }
    }
}