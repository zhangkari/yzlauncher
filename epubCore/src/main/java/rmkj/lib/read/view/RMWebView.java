package rmkj.lib.read.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;

import rmkj.lib.read.js.PRMWebHtml;
import rmkj.lib.read.util.LogUtil;

public class RMWebView extends WebView {
    public RMWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public RMWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RMWebView(Context context) {
        super(context);
        init();
    }

    @SuppressLint({"SetJavaScriptEnabled", "NewApi"})
    private void init() {
        this.setHorizontalScrollBarEnabled(false);
        this.setHorizontalScrollbarOverlay(false);
        this.setVerticalScrollBarEnabled(false);
        this.setVerticalScrollbarOverlay(false);
        this.setScrollbarFadingEnabled(false);
        this.getSettings().setBuiltInZoomControls(false);
        if (Build.VERSION.SDK_INT >= 11) {
            setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        }
        getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
        getSettings().setJavaScriptEnabled(true);
        getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        getSettings().setAppCacheEnabled(false);
        setBackgroundColor(0);
    }

    public void loadDataWithBaseURL(String html, String baseDir, String encoding) {
        //super.loadDataWithBaseURL(baseDir, html, mimeType, "UTF-8", null);

        //默认用html加载
        super.loadDataWithBaseURL(baseDir, html, "text/html", encoding, null);


        // super.loadDataWithBaseURL(baseDir + "/OPS", html, "text/html",
        // "UTF-8", null);
    }

    @Override
    public void loadData(String data, String mimeType, String encoding) {
        throw new RuntimeException("不能调用");
    }

    /*********************
     * android调用JS方法
     ****************************/
    public synchronized void jsGetMarkDesc() {
        String js = "javascript:epub.getBookMarkDesc()";
        loadUrl(js);
    }

    public synchronized void jsSearch(String key, String classId) {
        String js = "javascript:epub.searchKey('" + key + "','" + classId + "')";
        loadUrl(js);
    }

    public synchronized void startSelect(float x, float y) {
        String js = "javascript:epub.setStartPos(" + x + "," + y + ")";
        loadUrl(js);
    }

    public synchronized void moveSelectionTo(float x, float y) {
        String js = "javascript:epub.moveSelectionTo(" + x + "," + y + ")";
        loadUrl(js);
    }

    public synchronized void showPage(int page) {
        if (LogUtil.DEBUG) {
            LogUtil.e(this, "showPage:" + page);
        }
        String js = "javascript:epub.showPage(" + page + ")";
        loadUrl(js);
    }

    /**
     * 处理点击事件，通过js执行，回掉到jsinterface
     */
    public synchronized void handleClick(float x, float y) {
        String js = "javascript:epub.handleClick(" + x + "," + y + ")";
        loadUrl(js);
    }

    /**
     * 请求回调长按最后选择的文本
     */
    public synchronized void getSelectionText() {
        String js = "javascript:epub.getSelectionText()";
        loadUrl(js);
    }

    public synchronized void clearSelection() {
        String js = "javascript:epub.clearSelection()";
        loadUrl(js);
    }

    public synchronized void getHighlightSelection() {
        String js = "javascript:epub.getHighlightSelectionText('" + PRMWebHtml.LINE_SPAN_CLASS_NAME + "')";
        loadUrl(js);
    }

    public synchronized void handleClickEvent(int x, int y) {
        String js = "javascript:epub.handleClickEvent(" + x + "," + y + ")";
        loadUrl(js);

    }
}
