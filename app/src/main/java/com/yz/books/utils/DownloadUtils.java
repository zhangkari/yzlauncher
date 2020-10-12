package com.yz.books.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author lilin
 * @time on 2020-01-16 15:39
 */
public class DownloadUtils {

    public static boolean mCancelDownload;

    private static Call mCall;

    public static Call download(final String url, final String saveFile,
                                final OnDownloadListener listener) {
        mCancelDownload = false;

        Request request = new Request.Builder().url(url).build();
        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtils.INSTANCE.e("onFailure==");
                listener.onDownloadFailed(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                mCall = call;
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len;
                FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    File file = new File(saveFile);
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        listener.onDownloading(progress);

                        if (mCancelDownload) {
                            call.cancel();
                            break;
                        }
                    }
                    if (mCancelDownload) {
                        if (!call.isCanceled()) {
                            call.cancel();
                        }
                        listener.onDownloadFailed("已取消下载");
                    } else {
                        fos.flush();
                        listener.onDownloadSuccess(file.getAbsolutePath());
                    }
                } catch (Exception e) {
                    listener.onDownloadFailed(e.getMessage());
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        return mCall;
    }

    public interface OnDownloadListener {
        void onDownloadSuccess(String path);

        void onDownloading(int progress);

        void onDownloadFailed(String msg);
    }
}
