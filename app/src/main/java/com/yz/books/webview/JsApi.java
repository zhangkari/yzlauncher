package com.yz.books.webview;

import android.webkit.JavascriptInterface;

/**
 * @author lilin
 * @time on 2020/5/5 下午7:51
 */
public class JsApi {

    private String mBookParams;

    public JsApi(String bookParams) {
        mBookParams = bookParams;
    }

    @JavascriptInterface
    public String getJournalParams(Object msg) {
        return mBookParams;
    }

    @JavascriptInterface
    public void readBook(Object object) {
        /*data class Book(
                val bookId: Int,
                val bookName: String,
                val bookType: String?,
                val coverImg: String,
                val path: String?,
                val fileMd5String: String?,
)*/
    }

}
