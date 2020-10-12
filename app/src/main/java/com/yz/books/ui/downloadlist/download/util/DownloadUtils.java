package com.yz.books.ui.downloadlist.download.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author lilin
 * @time on 2020/3/23 下午5:21
 */
public class DownloadUtils {

    public static void close(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
