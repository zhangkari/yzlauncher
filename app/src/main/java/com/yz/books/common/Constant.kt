package com.yz.books.common

import android.os.Build

/**
 * @author lilin
 * @time on 2020-01-16 11:41
 */
object Constant {

    /**
     * 低版本系统
     */
    val LOW_SYSTEM = Build.MODEL == "rk3288"
            || Build.VERSION.RELEASE.startsWith("5")

    /**
     * 在线离线版本
     */
    const val ONLINE_VERSION = false
    /*val ONLINE_VERSION: Boolean
        get() = getApplicationContext().isNetworkConnected()*/

    /**
     * 是否横屏
     */
    const val SCREEN_ORIENTATION_LANDSCAPE = true

    const val DB_PATH = "db"

    const val SD_PATH = "/mnt/sdcard/usb_storage/USB_DISK2/udisk0"
    const val ROOT_PATH = "/mnt/sdcard"
    const val USB_PATH = "/mnt/usb_storage/USB_DISK1"  //存储设备的固定根目录  复制文件用-固定值

    const val READ_NATIVE_PATH = "$ROOT_PATH/readNative.txt" //读取路径的配置文件
    const val READ_PATH_KEY = "path"  //资源配置中，设备id

    /**
     * 本地路径
     */
    const val LOCAL_PATH = "$ROOT_PATH/yzbooks/"

    const val MASSIVE_BOOK_INFO_KEY_EXTRA = "massiveBookInfo"
    const val AUDIO_BOOK_DETAIL_KEY_EXTRA = "audioBookDetail"
    const val VIDEO_INFO_KEY_EXTRA = "videoInfo"
    const val H5_URL_KEY_EXTRA = "h5Url"
    const val THEMATIC_ID_KEY_EXTRA = "thematicId"
    const val DOWNLOAD_LIST_TAG_KEY_EXTRA = "downloadListTag"
}