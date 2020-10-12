package com.yz.books.ui.downloadlist.download;

import android.content.Context;

import com.yz.books.ui.downloadlist.download.util.FileManager;


public class DownloadFacade {
    private static final DownloadFacade sFacade = new DownloadFacade();

    private DownloadFacade() {
    }

    public static DownloadFacade getFacade() {
        return sFacade;
    }

    public void init(Context context) {
        FileManager.getInstance().init(context);
        DaoManagerHelper.getManager().init(context);
    }

    public void startDownload(String url, String name, DownloadCallback callback) {
        DownloadDispatcher.getInstance().startDownload(url, name, callback);
    }

    public void stopDownload(String url) {
        DownloadDispatcher.getInstance().stopDownLoad(url);
    }


    public void startDownload(String url) {
        //  DownloadDispatcher.getInstance().startDownload(url);
    }
}
