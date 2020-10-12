package com.yz.books.ui.massive.readbook

import android.content.DialogInterface
import android.content.res.AssetManager
import android.view.View
import android.view.ViewGroup
import com.tencent.smtt.export.external.interfaces.JsResult
import com.tencent.smtt.export.external.interfaces.SslError
import com.tencent.smtt.export.external.interfaces.SslErrorHandler
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
import com.yz.books.R
import com.yz.books.api.ApiConstant
import com.yz.books.base.activity.BaseMVVMActivity
import com.yz.books.base.viewmodel.State
import com.yz.books.common.Constant
import com.yz.books.ext.addFileHostUrl
import com.yz.books.ext.dismissDialog
import com.yz.books.ext.isNetworkConnected
import com.yz.books.ext.showToast
import com.yz.books.ui.massive.bean.JournalBookParamsBean
import com.yz.books.utils.*
import com.yz.books.webview.JsApi
import com.yz.books.widget.dialog.LoadingDialog
import kotlinx.android.synthetic.main.activity_read_journal_book.*
import kotlinx.coroutines.*
import me.jessyan.autosize.internal.CancelAdapt
import java.io.File

/**
 * @author lilin
 * @time on 2020/5/5 下午9:29
 */
class ReadJournalBookActivity : BaseMVVMActivity<ReadBookViewModel>(), CancelAdapt {

//region var/val

    private val mProgressDialog by lazy(LazyThreadSafetyMode.NONE) {
        LoadingDialog(this)
    }

    private var mBookParams: String? = null

    private var mZipJob: Job? = null

//endregion

//region implement methods

    override fun onDestroy() {
        super.onDestroy()
        mProgressDialog.dismissDialog()
        mZipJob?.cancel()

        with(web_view) {
            loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
            clearHistory()
            (parent as ViewGroup).removeView(this)
            destroy()
        }
    }

    override fun getAssets(): AssetManager {
        return resources.assets
    }

    override fun providerVMClass() = ReadBookViewModel()

    override fun getLayoutId() = R.layout.activity_read_journal_book

    override fun initView() {
        initWebView()
    }

    override fun initData() {
        //ZipUtils.unZipFolder("/mnt/sdcard/yzbooks/666798.zip", FileUtils.getLocalPath())

        val bookParamsBean = intent?.extras?.getParcelable(Constant.MASSIVE_BOOK_INFO_KEY_EXTRA)
                as JournalBookParamsBean
        tv_title.text = bookParamsBean.journalName

        val fileName = bookParamsBean.path?.substring(bookParamsBean.path.lastIndexOf("/") + 1)//.replace(".zip", "")
        bookParamsBean.relative_path = FileUtils.getLocalPath()
        // /mnt/sdcard/yzbooks/666798
        mBookParams = GsonUtils.toJson(bookParamsBean)

        web_view.addJavascriptObject(JsApi(mBookParams), null)
        //web_view.loadUrl("file:///android_asset/journal_html/index.html?")

        //val fileZipName = bookParamsBean.path.substring(bookParamsBean.path.lastIndexOf("/") + 1)
        LogUtils.e("relative_path==${bookParamsBean.relative_path}//$fileName//${bookParamsBean.path}//${bookParamsBean.lateralReader}")
        if (File(bookParamsBean.relative_path + fileName).exists()) {
            loadH5()
        } else {
            if (isNetworkConnected()) {
                val url = if (bookParamsBean.lateralReader?.startsWith("http") == true
                    || bookParamsBean.lateralReader?.startsWith("https") == true) {
                    bookParamsBean.lateralReader
                } else {
                    ApiConstant.HOST + bookParamsBean.lateralReader
                }
                web_view.loadUrl(url)
                return
            }
            if (!Constant.ONLINE_VERSION) {
                showToast("期刊文件不存在，请到资源列表下载！")
                //showToast("离线版本，期刊文件不存在")
                return
            }
            //showToast("期刊文件不存在，执行下载")
            downloadJournalBook(bookParamsBean)
        }
    }

    override fun initListener() {
        super.initListener()
        btn_back.setOnClickListener { goBack() }
    }

    override fun observerUI(state: State) {

    }

    override fun observerForever() = false

//endregion

//region public methods

//endregion

//region private methods

    private fun goBack() {
        with(web_view) {
            if (canGoBack()) {
                goBack()
            } else {
                finish()
            }
        }
    }

    private fun initWebView() {
        initWebSetting()

        web_view.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(webview: WebView?, url: String?): Boolean {
                webview?.loadUrl(url)
                return true
            }

            override fun onReceivedSslError(p0: WebView?, handler: SslErrorHandler?, p2: SslError?) {
                handler?.proceed()
            }

            override fun onPageFinished(webview: WebView?, p1: String?) {
                //LogUtils.e("onPageFinished==$mBookParams")
                //webview?.loadUrl("javascript: init('$mBookParams');")
            }
        }

        web_view.webChromeClient = object : WebChromeClient() {
            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                //tv_title.text = title
            }

            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                progress_bar.progress = newProgress
                progress_bar.visibility = if (newProgress == 100) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
            }

            override fun onJsAlert(p0: WebView?, p1: String?, p2: String?, p3: JsResult?): Boolean {
                //showToast(p2)
                return true
            }
        }
    }

    private fun initWebSetting() {
        val webSetting = web_view.settings
        webSetting.javaScriptEnabled = true
        webSetting.setSupportZoom(false)
        webSetting.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS// 排版适应屏幕
        webSetting.displayZoomControls = false
        webSetting.loadWithOverviewMode = true
        webSetting.javaScriptCanOpenWindowsAutomatically = true
        webSetting.allowFileAccess = true
        webSetting.builtInZoomControls = true
        webSetting.useWideViewPort = true
        webSetting.setSupportMultipleWindows(true)
        webSetting.setAppCacheEnabled(false)
        webSetting.domStorageEnabled = true
        webSetting.setGeolocationEnabled(true)
        webSetting.setAppCacheMaxSize(java.lang.Long.MAX_VALUE)
        WebView.setWebContentsDebuggingEnabled(true)
        webSetting.pluginState = WebSettings.PluginState.ON_DEMAND
        //根据cache-control决定是否从网络上取数据
        webSetting.cacheMode = WebSettings.LOAD_NO_CACHE
    }

    private fun downloadJournalBook(bookParamsBean: JournalBookParamsBean) {
        val bookUrl = bookParamsBean.path
        //val bookId = bookParamsBean.journalId

        val fileName = bookUrl?.substring(bookUrl.lastIndexOf("/") + 1)
        LogUtils.e("fileName==$fileName")
        val file = FileUtils.createTempFile(fileName ?: "")

        //LogUtils.e("url==${bookUrl}//${bookUrl.addFileHostUrl()}")

        val url = ApiConstant.HOST + bookUrl//bookUrl?.addFileHostUrl()

        LogUtils.e("url==$url")

        if (bookUrl == null || bookUrl.isEmpty()) return

        DownloadUtils.download(url, file.path,
            object : DownloadUtils.OnDownloadListener {
                override fun onDownloading(progress: Int) {
                    runOnUiThread {
                        with(mProgressDialog) {
                            setTitle("提示")
                            setMessage("正在下载...请耐心等待")
                            isIndeterminate = false
                            max = 100
                            setCancelable(false)
                            setProgressStyle(android.app.ProgressDialog.STYLE_HORIZONTAL)
                            setButton(DialogInterface.BUTTON_NEGATIVE, "取消下载") { dialog, which ->
                                DownloadUtils.mCancelDownload = true
                                FileUtils.deleteFile(file.path)
                            }
                            show()

                            setProgress(progress)
                        }
                    }
                }

                override fun onDownloadFailed(msg: String?) {
                    LogUtils.e("onDownloadFailed==$msg")
                    runOnUiThread {
                        mProgressDialog.dismissDialog()
                        showToast("下载失败，请重新下载！")
                    }
                }

                override fun onDownloadSuccess(path: String?) {
                    path?.let {
                        val job = GlobalScope.launch(Dispatchers.Main) {
                            val deferred = GlobalScope.async (context = Dispatchers.IO, start = CoroutineStart.LAZY) {
                                if (FileMD5Utils.getFileMD5(File(it)) == bookParamsBean.fileMd5String) {
                                    if (it.endsWith(".zip")) {
                                        ZipUtils.unZipFolder(it, file.parent)
                                    }
                                }
                            }
                            deferred.await()
                        }
                        mZipJob = job
                        job.invokeOnCompletion {
                            runOnUiThread {
                                mProgressDialog.dismissDialog()
                                if (FileMD5Utils.getFileMD5(File(path)) == bookParamsBean.fileMd5String) {
                                    //ZipUtils.unZipFolder(it, FileUtils.getLocalPath())
                                    loadH5()
                                } else {
                                    showToast("下载失败，请重新下载！")
                                    FileUtils.deleteFile(path)
                                }
                            }
                        }
                    }
                }
            })
    }

    private fun loadH5() {
        //web_view.loadData("","text/html","UTF-8")

        /*if (!Constant.SCREEN_ORIENTATION_LANDSCAPE) {
            web_view.loadDataWithBaseURL(null, "file:///android_asset/journal_html_test/index_vertical.html?",
                "text/html; charset=UTF-8", "utf-8",null)
        } else {
            web_view.loadDataWithBaseURL(null, "file:///android_asset/journal_html_test/index_across.html?",
                "text/html; charset=UTF-8", "utf-8",null)
        }*/

        if (!Constant.SCREEN_ORIENTATION_LANDSCAPE) {
            web_view.loadUrl("file:///android_asset/journal_html/index_vertical.html?")
        } else {
            web_view.loadUrl("file:///android_asset/journal_html/index_across.html?")
        }
    }


//endregion
}