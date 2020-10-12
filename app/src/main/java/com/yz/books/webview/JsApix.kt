package com.yz.books.webview

import android.webkit.JavascriptInterface

/**
 * @author lilin
 * @time on 2020-01-12 16:27
 */
class JsApix {

    /**
     * 获取目录
     * @return 目录json
     */
    @JavascriptInterface
    fun getBookChapters(any: Any?): String {
        return ""
    }

    /**
     * 下载章节
     * @param any 章节url
     */
    @JavascriptInterface
    fun downloadBookChapter(any: Any?) {

    }
}