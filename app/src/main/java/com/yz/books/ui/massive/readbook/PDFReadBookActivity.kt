package com.yz.books.ui.massive.readbook

import android.annotation.SuppressLint
import android.graphics.Bitmap
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
import com.yz.books.webview.DWebView
import me.jessyan.autosize.internal.CancelAdapt

/**
 * pdf阅读
 *
 * @author lilin
 * @time on 2020-01-19 16:19
 */
class PDFReadBookActivity : ReadBookActivity() {

//region var/val

    private var mWebView: DWebView? = null

//endregion

//region implement methods

    override fun onDestroy() {
        super.onDestroy()
        mWebView?.destroy()
    }

    override fun getDocumentView(): WebView {
        mWebView = DWebView(this)
        initWebSetting()
        return mWebView!!
    }

    override fun getDocType() = DocTypeEnum.PDF

    override fun showPreviousChapter() {
    }

    override fun showNextChapter() {
    }

    override fun setChapterIndex(index: Int) {
    }

    override fun showPreviousPage() {
    }

    override fun showNextPage() {
    }

    override fun setFontSize(size: Int) {
    }

    override fun preBookView() {
        val data = getReadBookBean()
        setBookTitle(data?.bookName)
        prePDFView(data?.bookPath)
    }

//endregion

//region public methods

//endregion

//region private methods

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebSetting() {
        mWebView?.settings?.apply {
            javaScriptEnabled = true
            allowFileAccess = true
            setAllowFileAccessFromFileURLs(true)
            setAllowUniversalAccessFromFileURLs(true)
        }

        mWebView?.webViewClient = object : WebViewClient() {
            override fun onPageStarted(p0: WebView?, p1: String?, p2: Bitmap?) {
                super.onPageStarted(p0, p1, p2)
                showLoading()
            }

            override fun onPageFinished(p0: WebView?, p1: String?) {
                super.onPageFinished(p0, p1)
                dismissLoading()
            }
        }
    }

    /**
     * 预览pdf
     *
     * @param pdfUrl url或者本地文件路径
     */
    private fun prePDFView(pdfUrl: String?) {
        mWebView?.loadUrl("file:///android_asset/index.html?$pdfUrl")
    }

//endregion
}