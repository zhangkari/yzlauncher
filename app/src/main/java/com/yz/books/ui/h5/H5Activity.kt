package com.yz.books.ui.h5

import android.content.res.AssetManager
import android.os.Build
import android.os.Message
import android.view.View
import android.view.ViewGroup
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
import com.yz.books.R
import com.yz.books.base.activity.BaseMVVMActivity
import com.yz.books.base.viewmodel.State
import com.yz.books.common.Constant
import com.yz.books.utils.LogUtils
import kotlinx.android.synthetic.main.activity_h5.*


/**
 * H5界面
 *
 * @author lilin
 * @time on 2020-01-20 12:55
 */
class H5Activity : BaseMVVMActivity<H5ViewModel>() {

//region var/val

//endregion

//region implement methods

    override fun onDestroy() {
        super.onDestroy()
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

    override fun providerVMClass() = H5ViewModel()

    override fun getLayoutId() = R.layout.activity_h5

    override fun initView() {
        initWebView()
    }

    override fun initData() {
        val h5Url = intent?.extras?.getString(Constant.H5_URL_KEY_EXTRA)
        LogUtils.e("h5url==$h5Url")
        web_view.loadUrl(h5Url)
    }

    override fun initListener() {
        super.initListener()
        btn_back.setOnClickListener { goBack() }
    }

    override fun observerForever() = false

    override fun observerUI(state: State) {

    }

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
        }

        web_view.webChromeClient = object : WebChromeClient() {
            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                tv_title.text = title
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

            override fun onCreateWindow(
                view: WebView?,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: Message?
            ): Boolean {
                val newWebView = WebView(view?.context)
                newWebView.webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView,
                        url: String
                    ): Boolean {
                        // 在此处进行跳转URL的处理, 一般情况下_black需要重新打开一个页面, 这里我直接让当前的webview重新load了url
                        web_view.loadUrl(url)
                        return true
                    }
                }
                val transport = resultMsg?.obj as WebView.WebViewTransport?
                transport?.webView = newWebView
                resultMsg?.sendToTarget()
                return true
            }
        }

        //web_view.addJavascriptObject(JsApi(), null)
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
        //Android 5.0上Webview默认不允许加载Http与Https混合内容
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSetting.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
    }

    override fun showFocusIndicator(): Boolean {
        return false
    }

//endregion
}