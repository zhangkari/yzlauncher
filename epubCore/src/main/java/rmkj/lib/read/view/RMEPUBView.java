package rmkj.lib.read.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rmkj.lib.epub_ggebook.R;
import rmkj.lib.read.RMReadController;
import rmkj.lib.read.db.RMReadingManager;
import rmkj.lib.read.db.RMReadingMark;
import rmkj.lib.read.db.RMReadingNote;
import rmkj.lib.read.epub.entity.RMEPUBResourceProvider;
import rmkj.lib.read.epub.search.RMSpineSearchController;
import rmkj.lib.read.epub.search.RMSpineSearchController.RMSpineSearchControllerEventListener;
import rmkj.lib.read.epub.search.RMSpineSearchResult;
import rmkj.lib.read.global.PRMWebHtmlParam;
import rmkj.lib.read.global.PRMWebSetting;
import rmkj.lib.read.itf.IRMEPUBPageInterface;
import rmkj.lib.read.itf.IRMEPUBSpineInterface;
import rmkj.lib.read.itf.IRMObejctInterface;
import rmkj.lib.read.itf.OnRMEPUBJSOnClickListener;
import rmkj.lib.read.itf.OnRMEPUBSelectionListener;
import rmkj.lib.read.itf.OnRMPageChangeListener;
import rmkj.lib.read.itf.OnRMPageTurningListener;
import rmkj.lib.read.itf.OnRMSpineChangedListener;
import rmkj.lib.read.js.PRMHtmlDoctype;
import rmkj.lib.read.js.PRMJSToAndroidInterface;
import rmkj.lib.read.js.PRMJSToAndroidInterface.JsInterfaceListener;
import rmkj.lib.read.js.PRMWebHtml;
import rmkj.lib.read.js.entity.HighlightSelectionText;
import rmkj.lib.read.search.RMSearchSesultItem;
import rmkj.lib.read.search.RMSearcher;
import rmkj.lib.read.search.RMSearcher.OnRMSearchListener;
import rmkj.lib.read.touch.PRMReadViewTouch;
import rmkj.lib.read.touch.PRMReadViewTouch.OnRMWebViewListener;
import rmkj.lib.read.util.LogUtil;
import rmkj.lib.read.util.MD5;
import rmkj.lib.read.util.RMUtilFileStream;
import rmkj.lib.read.util.RMWebUIHelper;
import rmkj.lib.read.webview.PRMWebChromeClient;
import rmkj.lib.read.webview.PRMWebViewClient;
import rmkj.lib.read.webview.PRMWebViewClient.OnSpineListener;
import rmkj.lib.view.drag.DragController;
import rmkj.lib.view.drag.DragLayer;
import rmkj.lib.view.drag.DragListener;
import rmkj.lib.view.drag.DragSource;
import rmkj.lib.view.drag.MyAbsoluteLayout;

public class RMEPUBView extends RelativeLayout implements
        IRMEPUBSpineInterface, IRMEPUBPageInterface,
        RMSpineSearchControllerEventListener, OnRMSearchListener {

    private static String TAG = "RMEPUBView";

    /**
     * 数据提供者(接口) 包含epub和txt
     */
    private IRMObejctInterface epub;
    /**
     * 真正显示数据的WebView
     */
    private RMWebView webview;
    /**
     * 组装javascript 和 css
     */
    private PRMWebHtml htmlUtil;
    /**
     * TouchEvent事件分发
     */
    private PRMReadViewTouch mGestureDetector;
    /**
     * 包含书签,笔记的数据库
     */
    private RMReadingManager dbManager;
    /**
     * 书籍唯一关键字,如果不存在多用户共享一本书，可用book_path
     */
    private String book_key;
    /**
     * 章节改变时弹出的[Loading...]View
     */
    private PRMLoadingView loading;
    /**
     * 章节改变监听
     */
    private OnRMSpineChangedListener spineChangedListener;
    /**
     * 防止未加载完Spine连续跳章节
     */
    private boolean isLoadingSpine = false;
    /**
     * 当前章节
     */
    private int currentSpineIndex = 0;
    /**
     * 总章节
     */
    private int totalSpine;

    /**
     * 点击WebView 分发的事件
     */
    private OnRMEPUBJSOnClickListener onJsClickListener;
    /**
     * 用与加载章节时,组装JS 默认跳转
     */
    private PRMWebHtmlParam param = new PRMWebHtmlParam();
    /**
     * 总页码 未防止除0 报错.默认为1
     */
    private int totalPage = 1;
    /**
     * 当前页码
     */
    private int currentPage = 0;
    /**
     * 是否有上一页 下一页监听
     */
    private OnRMPageTurningListener pageTurningListener;
    /**
     * 屏幕的宽度
     */
    private int screenWidth;
    /**
     * 左右点击翻页的范围占屏幕宽度的多少
     */
    private float pageTurningBack = 0.4f;
    /**
     * 页码改变监听
     */
    private OnRMPageChangeListener pageChaneListener;
    private Toast toast;
    private float currentUpX;
    private float currentUpY;
    private float currentDownX;
    private float currentDownY;
    private PRMWebSetting setting;

    // webview 初始化
    protected JSInterface jsInterface;
    private PRMWebViewClient client;
    protected PRMJSToAndroidInterface rmjsInterface;

    // 选中事件监听
    private OnRMEPUBSelectionListener selectionListener;

    // private RMFileType type;
    public RMEPUBView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public RMEPUBView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RMEPUBView(Context context) {
        super(context);
        init(context);
    }

    /**
     * 统一处理 清理内存
     */
    public void onDestory() {
        loading = null;
        if (rmjsInterface != null) {
            rmjsInterface.destroy();
            rmjsInterface = null;
            jsInterface = null;
        }

        if (htmlUtil != null) {
            htmlUtil = null;
        }
        if (mSearch != null) {
            mSearch.cancelSearch();
        }
        if (webview != null) {
            this.removeView(webview);
            webview.clearFormData();
            webview.clearHistory();
            webview.clearCache(true);
            webview.clearView();
            webview.removeAllViews();
            webview.destroy();
            webview = null;
            LogUtil.e("RMEPUBView", "RMEPUBView onDestroy");
        }
        this.removeAllViews();
    }

    public void setSelectionListener(OnRMEPUBSelectionListener selectionListener) {
        this.selectionListener = selectionListener;
    }

    @SuppressWarnings("deprecation")
    private void init(Context context) {
        webview = new RMWebView(context);
        webview.setWebChromeClient(new PRMWebChromeClient());
        webview.setWebViewClient(client = new PRMWebViewClient(
                new OnSpineSkipInterface()));
        rmjsInterface = new PRMJSToAndroidInterface((Activity) context,
                jsInterface = new JSInterface());
        webview.addJavascriptInterface(rmjsInterface, "JSInterface");
        LayoutParams lp = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        lp.setMargins(20, 10, 20, 10);

        setBackgroundColor(0);
        this.addView(webview, lp);
        htmlUtil = new PRMWebHtml();
        mGestureDetector = new PRMReadViewTouch(new TouchInterface());
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        screenWidth = wm.getDefaultDisplay().getWidth();

        if (!LogUtil.isPort(context)) {
            screenWidth = screenWidth / 2;
        } else {
            screenWidth = screenWidth * 8 / 10;
        }

        pageTurningListener = new OnRMPageTurningListener() {

            @Override
            public void onPageNonePrePage() {
                if (toast == null) {
                    toast = Toast
                            .makeText(getContext(), "", Toast.LENGTH_SHORT);
                }
                toast.setText("已经是第一页了");
                toast.show();
            }

            @Override
            public void onPageNoneNextPage() {
                if (toast == null) {
                    toast = Toast
                            .makeText(getContext(), "", Toast.LENGTH_SHORT);
                }
                toast.setText("已经是最后一页了");
                toast.show();
            }
        };

        createSelectionLayer(context);
    }

    private String randomStr;

    /*********************************** 数据库 ********************************************/
    /**
     * RMEPUBController 中调用
     *
     * @throws IOException
     */
    public void init(IRMObejctInterface object, String book_key, String path)
            throws IOException {
        this.epub = object;
        this.book_key = book_key;
        totalSpine = epub.getTotalSpine();
        setting = new PRMWebSetting();
        dbManager = RMReadController.GLOBAL_DATA.dbManager;
        // type = RMReadController.GLOBAL_DATA.FILE_TYPE;
        provider = new RMEPUBResourceProvider();
        client.setZipMode(provider);
        randomStr = MD5.getMD5(book_key) + "/";
        client.setRandomStr(randomStr);
    }


    private RMEPUBResourceProvider provider;

    /**
     * 返回数据库
     */
    public RMReadingManager getDBManager() {
        return dbManager;
    }

    /**
     * 该方法调用之后，异步返回，通过JSInterface返回
     */
    public void getHighlightText() {
        webview.getHighlightSelection();
    }

    /**
     * 拿到当前页面的书签
     */
    public RMReadingMark getCurrentPageMark() {
        return dbManager.getMark(currentSpineIndex, currentPage, totalPage);
    }

    /**
     * 通过js获取标记描述
     */
    public void getMarkDesc() {
        webview.jsGetMarkDesc();
    }

    /**
     * 添加或者删除书签
     */
    public boolean toggleCurrentPageMark(String content) {
        return dbManager.toggleMark(currentSpineIndex, currentPage, totalPage,
                content);
    }

    /**
     * 添加笔记
     */
    public boolean addNote(RMReadingNote note) {
        boolean isTrue = dbManager.addNote(note);
        if (isTrue) {
            showSpine(currentSpineIndex, currentPage, totalPage);
        } else {
            Toast.makeText(getContext(), "添加笔记失败", Toast.LENGTH_SHORT).show();
        }
        return isTrue;
    }

    /**
     * 更新笔记
     */
    public boolean updateNote(String noteID, String noteText) {
        return dbManager.updateNote(noteID, noteText);
    }

    /**
     * 拿到笔记
     */
    public RMReadingNote getNote(String noteID) {
        return dbManager.getNote(noteID);
    }

    /**
     * 删除笔记
     */
    public boolean deleteNote(String noteID) {
        boolean isTrue = dbManager.deleteNote(noteID);
        if (isTrue) {
            showSpine(currentSpineIndex, currentPage, totalPage);
        }
        return isTrue;
    }

    /***********************************
     * 滑动及滑动触发事件
     ******************************************************/

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // 如果现在是编辑模式，事件无需要拦截
        if (isInSelectionMode()) {
            return false;
        } else {
            // 否则所有事件拦截到本地处理
            return true;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (isInSelectionMode()) {
            return mSelectionDragLayer.dispatchTouchEvent(ev);
        } else
            return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    public void setOnJSClick(OnRMEPUBJSOnClickListener onJsClickListener) {
        this.onJsClickListener = onJsClickListener;
    }

    /**
     * 是否第一页 最后一页监听
     */
    public void setOnPageTurningListener(
            OnRMPageTurningListener pageTurningListener) {
        this.pageTurningListener = pageTurningListener;
    }

    public int getTotalSpine() {
        return totalSpine;
    }

    public int getCurrentSpineIndex() {
        return currentSpineIndex;
    }

    @Override
    public boolean hasNextSpine() {
        int gotoChapterIndex = currentSpineIndex + 1;
        return hasSpine(gotoChapterIndex);
    }

    @Override
    public void showNextSpine() {
        int gotoChapterIndex = currentSpineIndex + 1;
        if (hasSpine(gotoChapterIndex)) {
            gotoSpine(gotoChapterIndex);
        }
    }

    @Override
    public boolean hasPrevSpine() {
        int gotoChapterIndex = currentSpineIndex - 1;
        return hasSpine(gotoChapterIndex);
    }

    @Override
    public void showPrevSpine() {
        int gotoChapterIndex = currentSpineIndex - 1;
        if (hasSpine(gotoChapterIndex)) {
            gotoSpine(gotoChapterIndex);
        }
    }

    @Override
    public synchronized boolean hasSpine(int spineIndex) {
        return epub.hasSpine(spineIndex);
    }

    public void showSpine(int spineIndex) {
        if (hasSpine(spineIndex)) {
            gotoSpine(spineIndex);
        }
    }

    public void showSpine(int spineIndex, float startPercent) {
        gotoSpine(spineIndex, startPercent);
    }

    public void showSpine(int spineIndex, int pageInSpine, int totalInSpine) {
        if (totalInSpine <= 0) {
            totalInSpine = 1;
        }
        if (spineIndex < 0) {
            spineIndex = 0;
        }
        gotoSpine(spineIndex, pageInSpine, totalInSpine);
    }

    /**
     * 根据锚点显示章节
     */
    public synchronized void showSpine(String relativePath) {
        String spinePath = null;
        String spineAnchor = null;
        if (relativePath == null) {
            if (LogUtil.DEBUG) {
                LogUtil.e(this, "跳转章节相对路径为 null");
            }
            return;
        }
        String[] path = relativePath.split("#");
        if (path.length == 1) {
            spinePath = path[0];
        } else if (path.length == 2) {
            spinePath = path[0];
            spineAnchor = path[1];
        } else if (path.length > 2) {
            if (LogUtil.DEBUG) {
                LogUtil.e(this, "relativePath is error:" + relativePath);
            }
            return;
        }
        int currentSpineIndex = epub.getSpineIndex(spinePath);
        gotoSpine(currentSpineIndex, spineAnchor);
    }

    /**
     * 内部调用统一用gotoSpine 外部调用统一用showSpine
     */
    private final synchronized void gotoSpine(int spineIndex) {
        if (hasSpine(spineIndex)) {
            if (isLoadingSpine) {
                if (LogUtil.DEBUG) {
                    LogUtil.e(this, "正在加载Spine");
                }
                return;
            }
            totalPage = 1;
            currentPage = 0;
            currentSpineIndex = spineIndex;
            isLoadingSpine = true;
            doLoadSpine(spineIndex, epub, provider);
        }
    }

    private void gotoSpine(int spineIndex, String anchor) {
        param.loadAnchor = anchor;
        param.loadFromAnchor = true;
        gotoSpine(spineIndex);
    }

    private void gotoSpine(int spineIndex, float startPercent) {
        param.loadPercent = startPercent;
        param.loadFromPercent = true;
        gotoSpine(spineIndex);
    }

    private void gotoSpine(int spineIndex, int page, int totalPage) {
        param.loadPage = page;
        param.loadTotalPage = totalPage;
        param.loadFormPageAndTotalPage = true;
        gotoSpine(spineIndex);
    }

    private void gotoSpineFromEnd(int spineIndex) {
        param.loadFromEnd = true;
        gotoSpine(spineIndex);
    }

    protected void doLoadSpine(int spineIndex, IRMObejctInterface epub,
                               RMEPUBResourceProvider provider) {
        String sourceHtml = null;
        String spineFile = null;
        String encoding = epub.getSpineEncode(spineIndex);
        try {
            spineFile = epub.getSpineFile(spineIndex);
            String[] spineSplit = spineFile.split("#");
            String bookpath = RMReadController.GLOBAL_DATA.BOOK_PATH;
            String bookType = bookpath.substring(bookpath.lastIndexOf(".") + 1);
            if (spineSplit.length == 1) {
                if (bookType.equals("rzp")) {
                    sourceHtml = RMUtilFileStream.getDecryptTextFromFile(provider.getSpineContent(spineFile), encoding);
                } else {
                    sourceHtml = RMUtilFileStream.getTextFromFile(provider.getSpineContent(spineFile), encoding);
                }
            } else if (spineSplit.length == 2) {
                if (bookType.equals("rzp")) {
                    sourceHtml = RMUtilFileStream.getDecryptTextFromFile(provider.getSpineContent(spineSplit[1]), encoding);
                } else {
                    sourceHtml = RMUtilFileStream.getTextFromFile(provider.getSpineContent(spineSplit[1]), encoding);
                }
            } else {
                if (LogUtil.DEBUG) {
                    LogUtil.e(this, "getSpineFile is error:" + spineFile
                            + ",at spineIndex:" + spineIndex);
                }
            }
        } catch (FileNotFoundException e) {
            if (LogUtil.DEBUG) {
                LogUtil.e(this, "文件未找到 spineIndex:" + spineFile);
            }
            e.printStackTrace();
        } catch (Exception e) {
            if (LogUtil.DEBUG) {
                LogUtil.e(this, "读取章节发生错误:" + spineFile);
            }
            e.printStackTrace();
        }
        if (sourceHtml == null) {
            return;
        }

        // 把xhtml转换成html
        sourceHtml = PRMHtmlDoctype.changeXhtmlToHtml(sourceHtml);

        sourceHtml = htmlUtil.replaceSpan(sourceHtml, spineIndex, dbManager);


        //LogUtil.e("YANG", "把xhtml转换成html -- " + sourceHtml);

        int width = webview.getWidth();
        int widthDip = RMWebUIHelper.px2dip(getContext(), width);
        int height = webview.getHeight();
        int heightDip = RMWebUIHelper.px2dip(getContext(), height);
        param.width = widthDip;
        param.height = heightDip;
        param.margin = 0;
        param.isVertical = epub.isVerticalOrientation(currentSpineIndex);

        if (isSearchMode) {
            param.isSearchMode = true;
            param.searchKey = searchKey;
        }

        String html = htmlUtil.getSpineHtmlData(sourceHtml, "", param, setting, getContext());
        String file = null;
        switch (RMReadController.GLOBAL_DATA.FILE_TYPE) {
            case EPUB_RZP:
            case EPUB_ZIP:
                file = "file:///" + randomStr;
                break;
            default:
                file = Uri.fromFile(new File(spineFile)).toString();
                break;
        }

        file += "/";
        webview.clearView();
        webview.scrollTo(0, 0);
        webview.setVisibility(View.INVISIBLE);

        webview.getSettings().setDefaultTextEncodingName(encoding);
        webview.loadDataWithBaseURL(html, file, "utf-8");
    }

    public void setNoteImagePath(String imagePath) {
        htmlUtil.setNoteImagePath(imagePath);
    }

    /*********************
     * 页码相关 (开始)
     ****************************/
    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public synchronized void showNextPage() {
        if (isLoadingSpine) {
            return;
        }
        if (epub.isRightPageOrientation()) {
            if (hasNextPage()) {
                showPage(currentPage + 1);
            } else {
                if (hasNextSpine()) {
                    isSearchTurningSpine = false;
                    showNextSpine();
                } else {
                    if (pageTurningListener != null) {
                        pageTurningListener.onPageNoneNextPage();
                    }
                }
            }
        } else {
            if (hasPrevPage()) {
                showPage(currentPage - 1);
            } else {
                if (hasPrevSpine()) {
                    isSearchTurningSpine = false;
                    gotoSpineFromEnd(currentSpineIndex - 1);
                } else {
                    if (pageTurningListener != null) {
                        pageTurningListener.onPageNonePrePage();
                    }
                }
            }
        }
    }

    boolean isSearchTurningSpine = false;

    public synchronized void showPrevPage() {
        if (isLoadingSpine) {
            return;
        }

        if (epub.isRightPageOrientation()) {
            if (hasPrevPage()) {
                showPage(currentPage - 1);
            } else {
                if (hasPrevSpine()) {
                    isSearchTurningSpine = false;
                    gotoSpineFromEnd(currentSpineIndex - 1);
                } else {
                    if (pageTurningListener != null) {
                        pageTurningListener.onPageNonePrePage();
                    }
                }
            }
        } else {
            if (hasNextPage()) {
                showPage(currentPage + 1);
            } else {
                if (hasNextSpine()) {
                    isSearchTurningSpine = false;
                    showNextSpine();
                } else {
                    if (pageTurningListener != null) {
                        pageTurningListener.onPageNoneNextPage();
                    }
                }
            }
        }

    }

    public void showPage(int pageIndex) {
        if (isLoadingSpine) {
            return;
        }
        if (hasPage(pageIndex)) {
            webview.showPage(pageIndex);
        }
    }

    public boolean hasPage(int page) {
        if (page < 0) {
            return false;
        }
        if (page >= totalPage) {
            return false;
        }
        return true;
    }

    public boolean hasPrevPage() {
        int page = currentPage - 1;
        return hasPage(page);
    }

    public boolean hasNextPage() {
        int page = currentPage + 1;
        return hasPage(page);
    }

    /**
     * 页码发生改变监听
     */
    public void setOnPageChaneListener(OnRMPageChangeListener pageChaneListener) {
        this.pageChaneListener = pageChaneListener;
    }

    /**
     * 章节改变监听
     */
    public void setOnSpineChangedListener(
            OnRMSpineChangedListener spineChangedListener) {
        this.spineChangedListener = spineChangedListener;
    }

    /*********************************
     * 进度
     ************************************/

    public float getPageInSpinePercent() {
        if (totalPage <= 0) {
            totalPage = 1;
        }
        float pagePercent = 1f * (currentPage + 1) / totalPage;
        return pagePercent;
    }

    public float getTotalPercent() {
        if (totalPage <= 0) {
            totalPage = 1;
        }
        if (totalSpine <= 0) {
            totalSpine = 1;
        }
        float oneSpinePercent;
        if (totalSpine > 1) {
            oneSpinePercent = 1f / (totalSpine - 1);
        } else {
            oneSpinePercent = 1f / (totalSpine);
        }
        float beforeSpinePercent = (currentSpineIndex - 1) * oneSpinePercent;
        float percent = beforeSpinePercent + oneSpinePercent
                * (currentPage + 1) / totalPage;
        if (percent < 0) {
            percent = (float) 0.00;
        }
        return percent;
    }

    public String getCurrentSpineName() {
        return epub.getSpineName(currentSpineIndex);
    }

    /*********************************
     * 字体间距等设置
     ************************************************/
    // TODO 初始化 设置 字体大小 间距 颜色 时 , 会打开html3次 需要修改
    public void setWebViewLineSpace(int lineSpace) {
        setWebViewLineSpace(lineSpace, true);
    }

    public void setWebViewLineSpace(int lineSpace, boolean isRefresh) {
        setting.setLineSpace(lineSpace);
        if (isRefresh) {
            showSpine(currentSpineIndex, currentPage, totalPage);
        }
    }

    public void setWebViewFontSize(int fontSize) {
        setWebViewFontSize(fontSize, true);
    }

    public void setWebViewFontSize(int fontSize, boolean isRefresh) {
        setting.setFontSize(fontSize);
        if (isRefresh) {
            showSpine(currentSpineIndex, currentPage, totalPage);
        }
    }

    public void setWebViewFontColor(String fontColor) {
        setWebViewFontColor(fontColor, true);
    }

    public void setWebViewFontColor(String fontColor, boolean isRefresh) {
        setting.setFontColor(fontColor);
        if (isRefresh) {
            showSpine(currentSpineIndex, currentPage, totalPage);
        }
    }

    /*************************************************************************************************/
    public void refresh() {
        dbManager.refreshMark();
        gotoSpine(currentSpineIndex, currentPage, totalPage);
    }

    /********************************************
     * 书本信息
     ********************************************************/
    public String getBookName() {
        return epub.getBookName();
    }

    public String getBookAuthor() {
        return epub.getAuthor();
    }

    public String getBookCover() {
        return epub.getCover();
    }

    /********************************************/
    class OnSpineSkipInterface implements OnSpineListener {
        @Override
        public void onSpineStart() {
            // isLoadingSpine = true;
            if (loading == null) {
                loading = new PRMLoadingView(getContext());
            }
            loading.show();
        }

        @Override
        public void onSpineComplete() {
            // isLoadingSpine = false;
            // if (loading != null) {
            // loading.cancel();
            // }
            if (spineChangedListener != null) {
                spineChangedListener.onSpineChanged(currentSpineIndex);
            }
        }

        @Override
        public void onSpineError() {
            // isLoadingSpine = false;
            if (loading != null) {
                loading.cancel();
            }
        }
    }

    /***************************************
     * 触摸事件的回调
     ***********************************************************/
    class TouchInterface implements OnRMWebViewListener {
        /**
         * webview右滑动
         */
        @Override
        public void onMoveRight() {
            showPrevPage();
        }

        /**
         * webview左滑动
         */
        @Override
        public void onMoveLeft() {
            showNextPage();
        }

        /**
         * webView被点击
         */
        @SuppressLint("Recycle")
        @Override
        public void onClick(float currentX, float currentY, MotionEvent evt) {
            MotionEvent down = MotionEvent.obtain(evt);
            down.setAction(MotionEvent.ACTION_DOWN);
            webview.onTouchEvent(down);
            MotionEvent up = MotionEvent.obtain(evt);
            up.setAction(MotionEvent.ACTION_UP);
            webview.onTouchEvent(up);
            currentDownX = currentX;
            currentDownY = currentY;
            // webview.handleClickEvent((int)currentX, (int)currentY);
        }

        @Override
        public void onMove(MotionEvent ev) {
        }

        /**
         * WebView长按触发后手指up
         */
        @Override
        public void onLongClickUp(float currentX, float currentY, float downX,
                                  float downY, MotionEvent evt) {
            // if (longClickUp != null) {
            // currentUpX = currentX;
            // currentUpY = currentY;
            // webview.getHighlightSelection();
            // }
        }

        /**
         * webView被长按
         */
        @SuppressWarnings("deprecation")
        @Override
        public void onLongClick(float currentX, float currentY, MotionEvent evt) {
//            enterSelection(currentX, currentY);
            try {
                Vibrator vibrator = (Vibrator) getContext().getSystemService(
                        Service.VIBRATOR_SERVICE);
                vibrator.vibrate(100);
            } catch (SecurityException e) {
                if (LogUtil.DEBUG) {
                    LogUtil.e(this,
                            "未注册<uses-permission android:name='android.permission.VIBRATE>'");
                }
            }
        }

        /**
         * webview长按移动
         */
        @SuppressWarnings("deprecation")
        @Override
        public void onLongClickMoveing(float currentX, float currentY,
                                       MotionEvent evt) {
            // float xPoint = RMWebUIHelper.getDensityIndependentValue(currentX,
            // getContext())
            // / RMWebUIHelper.getDensityIndependentValue(
            // webview.getScale(), getContext());
            // float yPoint = RMWebUIHelper.getDensityIndependentValue(currentY,
            // getContext())
            // / RMWebUIHelper.getDensityIndependentValue(
            // webview.getScale(), getContext());
            // webview.moveSelectionTo(xPoint, yPoint);
        }
    }

    /**
     * 传递的是 px
     *
     * @param x
     * @param y
     */
    public void enterSelection(float x, float y) {
        webview.clearSelection();

        Log.e(TAG, "enterSelection x:" + x + " y:" + y);

        float scale = getDensityIndependentValue(webview.getScale(),
                getContext());

        float startXPix = x - webview.getScrollX();
        float startYPix = y - webview.getScrollY();

        startXPix = getDensityIndependentValue(startXPix, getContext()) / scale;
        startYPix = getDensityIndependentValue(startYPix, getContext()) / scale;

        webview.startSelect(startXPix, startYPix);

        int absLeft = (int) x;// (int) (x - webview.getScrollX());
        int absTop = (int) (y - webview.getScrollY());

        // 进入drag，自动选择
        setupDragBounds(absLeft, absTop);
        startSelectionMode();

        if (selectionListener != null) {
            selectionListener.onEnterSelectionMode(mSelectionBounds.right,
                    mSelectionBounds.bottom);
        }
        // 转换为网页位置
        // float xPoint = RMWebUIHelper
        // .getDensityIndependentValue(x, getContext())
        // / RMWebUIHelper.getDensityIndependentValue(webview.getScale(),
        // getContext());
        // float yPoint = RMWebUIHelper
        // .getDensityIndependentValue(y, getContext())
        // / RMWebUIHelper.getDensityIndependentValue(webview.getScale(),
        // getContext());
        // webview.startSelect(xPoint, yPoint);
        //
        // int xDip = (int) getDensityIndependentValue(x, getContext());
        // int yDip = (int) getDensityIndependentValue(y, getContext());

    }

    public void exitSelection() {
        mSelectionBounds = null;
        endSelectionMode();
        // 清除选中
        webview.clearSelection();
        if (selectionListener != null) {
            selectionListener.onExitSelectionMode();
        }
    }

    public void selectionEnd() {
        // webview.getHighlightSelection();
        if (selectionListener != null) {
            selectionListener.onSelectionEnd();
        }
    }

    /**
     * 传递的值均为 dp
     *
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    public void updateSelection(int left, int top, int right, int bottom) {

        @SuppressWarnings("deprecation")
        float scale = getDensityIndependentValue(webview.getScale(),
                getContext());

        float startXPix = left;
        float startYPix = top - webview.getScrollY();
        float endXPix = right;
        float endYPix = bottom - webview.getScrollY();

        startXPix = getDensityIndependentValue(startXPix, getContext()) / scale;
        startYPix = getDensityIndependentValue(startYPix, getContext()) / scale;
        endXPix = getDensityIndependentValue(endXPix, getContext()) / scale;
        endYPix = getDensityIndependentValue(endYPix, getContext()) / scale;

        webview.startSelect(startXPix, startYPix);
        webview.moveSelectionTo(endXPix, endYPix);

        int absLeft = left;
        int absTop = top - webview.getScrollY();
        int absRight = right;
        int absBottom = bottom - webview.getScrollY();

        if (selectionListener != null) {
            selectionListener.onSelectionUpdate(new Rect(absLeft, absTop,
                    absRight, absBottom));
        }
    }

    /***************************************
     * JS的回调
     ***************************************************************/
    protected class JSInterface implements JsInterfaceListener {

        @Override
        public void onJSPageChanged(int page, int total) {
            if (totalPage != 0) {
                currentPage = page;
                totalPage = total;
                if (pageChaneListener != null) {
                    pageChaneListener.onPageChanged(currentPage, totalPage);
                }
            }
        }

        @Override
        public void onJSLoadComplete(int page, int total, int scrollWidth,
                                     int viewWidth) {

            if (totalPage != 0) {
                currentPage = page;
                totalPage = total;
                if (pageChaneListener != null) {
                    pageChaneListener.onPageChanged(currentPage, totalPage);
                }
            }
            if (isSearchMode == true) {
                // webview.setVisibility(View.INVISIBLE);
                search(searchKey);
                return;
            }
            isLoadingSpine = false;
            param.resetLoad();
            if (loading != null) {
                loading.cancel();
            }
            webview.setVisibility(View.VISIBLE);
        }

        /**
         * 点击图片
         */
/*        @Override
        public void onJSImageClicked(String src) {
            if (onJsClickListener != null) {
                String srcUrl = null;
                switch (RMReadController.GLOBAL_DATA.FILE_TYPE) {
                    case EPUB_RZP:
                    case EPUB_ZIP:
                        srcUrl = src.replace(randomStr, "");
                        break;
                    case EPUB:
                    case TXT:
                        srcUrl = src;
                        break;
                }
                try {
                    srcUrl = URLDecoder.decode(srcUrl, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                System.out.println("onJSImageClicked --- " + srcUrl);

                onJsClickListener.onImageClick(srcUrl);
            }
        }*/

        /**
         * 笔记图标被点击
         */
        @Override
        public void onJSEditNoteClicked(String noteID) {
            if (onJsClickListener != null) {
                onJsClickListener.onEditNoteClick(noteID, currentDownX,
                        currentDownY);
            }
        }

        /**
         * 笔记被点击
         */
        @Override
        public void onJSNoteClicked(String id) {
            if (onJsClickListener != null) {
                onJsClickListener.onSelectTextClick(id, currentDownX,
                        currentDownY);
            }
        }

        /**
         * 锚被点击
         */
        @Override
        public void onJSHrefClicked(String href) {
            if (onJsClickListener != null) {
                onJsClickListener.onHrefClick(href);
            }
        }

        /**
         * 点击空白区域
         */
        @Override
        public void onJSBlankClicked(float currentX, float currentY) {
            // JS回调的currentX currentY 不准确

            if (currentDownX < pageTurningBack * screenWidth) {
                showNextPage();

                System.out.println("currentDownX = " + currentDownX + "screenWidth =  " + screenWidth + "pageTurningBack * screenWidth =   " + pageTurningBack * screenWidth);
            } else if (currentDownX > (1 - pageTurningBack) * screenWidth) {
                showPrevPage();

                System.out.println("currentDownX = " + currentDownX + "(1 - pageTurningBack) * screenWidth) =    " + (1 - pageTurningBack) * screenWidth);
            } else {

                System.out.println("currentDownX = " + currentDownX + "pageTurningBack * screenWidth =   " + pageTurningBack * screenWidth + "(1 - pageTurningBack) * screenWidth) =    " + (1 - pageTurningBack) * screenWidth);
                if (onJsClickListener != null) {
                    onJsClickListener.onNoneClick(currentDownX, currentDownY);
                }
            }
        }

        @Override
        public void onJSHighlightSelectText(String selectText,
                                            String selectionReplaceArray, String spanID) {
            if (onJsClickListener != null) {
                HighlightSelectionText hText = new HighlightSelectionText(
                        selectionReplaceArray, spanID, selectText);
                onJsClickListener.onGotHighlightText(hText);
            }
        }

        @Override
        public void onjsSelectionText(String selectText) {
            // TODO 未用
        }

        @Override
        public void onJSSearchText(RMSpineSearchResult result) {
            isLoadingSpine = false;
            spineSearchController.setSearchResult(result);
            if (isSearchTurningSpine) {
                if (param.loadFromEnd == true) {
                    spineSearchController
                            .showItem(RMSpineSearchController.ITEM_INDEX_LAST);
                } else {
                    if (searchStartIndexInSpine == -1) {
                        spineSearchController
                                .showItem(RMSpineSearchController.ITEM_INDEX_FIRST);
                    } else {
                        spineSearchController.showItem(searchStartIndexInSpine);
                        searchStartIndexInSpine = -1;
                    }
                }
            } else {
                if (param.loadFromEnd == true) {
                    showPage(totalPage - 1);
                } else {
                    showPage(0);
                }
            }

            param.resetLoad();
            if (loading != null) {
                loading.cancel();
            }
            webview.setVisibility(View.VISIBLE);
        }


        @Override
        public void onJsAnnotaionClicked(String annotaion) {
            onJsClickListener.onClickAnnotation(annotaion);
        }
    }

    /***************************** 搜索 ************************************/
    /**
     * 搜索模式
     */
    private boolean isSearchMode = false;
    private String searchKey;
    private RMSpineSearchController spineSearchController = null;
    private Map<Integer, Integer> epubSearchItems = null;
    private RMSearcher mSearch;
    private int searchStartSpine;
    private int searchStartIndexInSpine;

    public void setSearchText(String keyword) {
        setSearchText(keyword, null);
    }

    public void setSearchText(String keyword, Map<Integer, Integer> map) {
        if (keyword == null) {
            return;
        }
        if (keyword.trim().length() == 0) {
            return;
        }
        if (map == null) {
            map = new HashMap<Integer, Integer>();
            if (mSearch == null) {
                mSearch = new RMSearcher();
            }
            epubSearchItems = map;
            mSearch.searchFile(keyword, this);
        } else {
            epubSearchItems = map;
        }
        if (spineSearchController == null)
            spineSearchController = new RMSpineSearchController(this, this);

        this.isSearchMode = true;
        this.searchKey = keyword;
        spineSearchController.setKeyword(searchKey);
    }

    public void exitSearchMode() {
        this.searchKey = null;
        this.isSearchMode = false;
        if (mSearch != null) {
            mSearch.cancelSearch();
        }
    }

    public void search(String keyword) {

        webview.jsSearch(keyword, PRMWebHtml.SEARCH_CSS_CLASS_NAME);
    }

    public void showSearchItem(int spineIndex, int indexInSpine) {
        this.searchStartIndexInSpine = indexInSpine;
        this.searchStartSpine = spineIndex;
        isSearchTurningSpine = true;
        showSpine(searchStartSpine);
    }

    public void showNextSearchItem() {
        spineSearchController.showNextItem();
    }

    public void showPrevSearchItem() {
        spineSearchController.showPrevItem();
    }

    @Override
    public void onNoPrevItem(RMSpineSearchController sender) {
        for (int i = currentSpineIndex - 1; i >= 0; i--) {
            if (epubSearchItems.containsKey(i)) {
                isSearchTurningSpine = true;
                gotoSpineFromEnd(i);
                this.searchStartIndexInSpine = RMSpineSearchController.ITEM_INDEX_LAST;
                return;
            }
        }
        if (toast == null) {
            toast = Toast.makeText(getContext(), "没有上一个了", Toast.LENGTH_SHORT);
        }
        toast.show();
    }

    @Override
    public void onNoNextItem(RMSpineSearchController sender) {
        for (int i = currentSpineIndex + 1; i < getTotalSpine(); i++) {
            if (epubSearchItems.containsKey(i)) {
                isSearchTurningSpine = true;
                gotoSpine(i);
                this.searchStartIndexInSpine = RMSpineSearchController.ITEM_INDEX_FIRST;
                return;
            }
        }
        if (toast == null) {
            toast = Toast.makeText(getContext(), "", Toast.LENGTH_SHORT);
        }
        if (mSearch == null) {
            toast.setText("没有下一个了");
        } else {
            if (mSearch.isStarting()) {
                toast.setText("请稍等...后续章节正在扫描中。。");
            } else {
                toast.setText("没有下一个了");
            }
        }
        toast.show();
    }

    @Override
    public void onSearchItemComplete(int searchIndex, String name,
                                     List<RMSearchSesultItem> currentList) {
        if (currentList != null && currentList.size() != 0) {
            epubSearchItems.put(searchIndex, searchIndex);
        }
    }

    /********* 选中相关 ******/
    /**
     * The drag layer for selection.
     */
    protected DragLayer mSelectionDragLayer;

    /**
     * The drag controller for selection.
     */
    protected DragController mDragController;

    /**
     * The selection bounds.
     */
    protected Rect mSelectionBounds = null;
    /**
     * The draging state
     */
    protected boolean mDragging;

    // *****************************************************
    // *
    // * Selection Handles
    // *
    // *****************************************************

    /**
     * The start selection handle.
     */
    protected ImageView mStartSelectionHandle;

    /**
     * the end selection handle.
     */
    protected ImageView mEndSelectionHandle;

    /**
     * Identifier for the selection start handle.
     */
    protected final int SELECTION_START_HANDLE = 0;

    /**
     * Identifier for the selection end handle.
     */
    protected final int SELECTION_END_HANDLE = 1;

    /**
     * Last touched selection handle.
     */
    protected int mLastTouchedSelectionHandle = -1;
    private static int drag_top_padding_y = 50;

    protected DragListener dragListener = new DragListener() {

        @Override
        public void onDragStart(DragSource source, Object info, int dragAction) {
            Log.e(TAG, "onDragStart");
        }

        @Override
        public void onDrag() {
            MyAbsoluteLayout.LayoutParams startHandleParams = (MyAbsoluteLayout.LayoutParams) mStartSelectionHandle
                    .getLayoutParams();
            MyAbsoluteLayout.LayoutParams endHandleParams = (MyAbsoluteLayout.LayoutParams) mEndSelectionHandle
                    .getLayoutParams();

            int startX = (int) startHandleParams.x
                    + mStartSelectionHandle.getWidth() / 2;
            int endX = (int) endHandleParams.x + mEndSelectionHandle.getWidth()
                    / 2;
            int startY = (int) startHandleParams.y
                    - mStartSelectionHandle.getHeight() / 2;
            int endY = (int) endHandleParams.y
                    - mEndSelectionHandle.getHeight() / 2;

            updateSelection(startX, startY, endX, endY);

        }

        @Override
        public void onDragEnd() {
            selectionEnd();
        }

        @Override
        public void onDragClickNothing(float x, float y) {
            exitSelection();
        }
    };

    protected void setupDragBounds(int startX, int startY) {
        mSelectionBounds = new Rect();
        mSelectionBounds.left = startX;
        mSelectionBounds.right = startX + 50;
        mSelectionBounds.top = startY;
        mSelectionBounds.bottom = startY;
    }

    /**
     * Creates the selection layer.
     *
     * @param context
     */
    protected void createSelectionLayer(Context context) {
        mSelectionDragLayer = (DragLayer) LayoutInflater.from(context).inflate(
                R.layout.selection_drag_layer, null);

        // Make sure it's filling parent
        mDragController = new DragController(context);
        mDragController.setDragListener(dragListener);
        mDragController.addDropTarget(mSelectionDragLayer);
        mSelectionDragLayer.setDragController(mDragController);

        mStartSelectionHandle = (ImageView) mSelectionDragLayer
                .findViewById(R.id.startHandle);
        mStartSelectionHandle.setTag(new Integer(SELECTION_START_HANDLE));
        mEndSelectionHandle = (ImageView) mSelectionDragLayer
                .findViewById(R.id.endHandle);
        mEndSelectionHandle.setTag(new Integer(SELECTION_END_HANDLE));

        OnTouchListener handleTouchListener = new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                boolean handledHere = false;

                final int action = event.getAction();

                // Down event starts drag for handle.
                if (action == MotionEvent.ACTION_DOWN) {
                    handledHere = startDrag(v);
                    mLastTouchedSelectionHandle = (Integer) v.getTag();
                }
                return handledHere;
            }
        };

        mStartSelectionHandle.setOnTouchListener(handleTouchListener);
        mEndSelectionHandle.setOnTouchListener(handleTouchListener);
    }

    private void addSelectionLayer(View v) {
        this.addView(v);
    }

    private void removeSelectionLayer(View v) {
        this.removeView(v);
    }

    /**
     * Starts selection mode on the UI thread
     */
    private Handler startSelectionModeHandler = new Handler() {

        public void handleMessage(Message m) {

            addSelectionLayer(mSelectionDragLayer);
            int contentHeight = (int) Math.ceil(getDensityDependentValue(
                    webview.getContentHeight(), getContext()));

            // Update Layout Params
            ViewGroup.LayoutParams layerParams = mSelectionDragLayer
                    .getLayoutParams();
            layerParams.height = contentHeight;
            layerParams.width = webview.getWidth();
            mSelectionDragLayer.setLayoutParams(layerParams);

            drawSelectionHandles();
        }
    };

    /**
     * Starts selection mode.
     */
    public void startSelectionMode() {

        startSelectionModeHandler.sendEmptyMessage(0);

    }

    // Ends selection mode on the UI thread
    private Handler endSelectionModeHandler = new Handler() {
        public void handleMessage(Message m) {
            mSelectionBounds = null;
            mLastTouchedSelectionHandle = -1;
            removeSelectionLayer(mSelectionDragLayer);

        }
    };

    /**
     * Ends selection mode.
     */
    public void endSelectionMode() {
        endSelectionModeHandler.sendEmptyMessage(0);
    }

    /**
     * Calls the handler for drawing the selection handles.
     */
    private void drawSelectionHandles() {
        drawSelectionHandlesHandler.sendEmptyMessage(0);
    }

    /**
     * Handler for drawing the selection handles on the UI thread.
     */
    private Handler drawSelectionHandlesHandler = new Handler() {
        public void handleMessage(Message m) {

            MyAbsoluteLayout.LayoutParams startParams = (MyAbsoluteLayout.LayoutParams) mStartSelectionHandle
                    .getLayoutParams();
            startParams.x = (int) (mSelectionBounds.left - mStartSelectionHandle
                    .getDrawable().getIntrinsicWidth());
            startParams.y = (int) (mSelectionBounds.top - mStartSelectionHandle
                    .getDrawable().getIntrinsicHeight());

            // Stay on screen.
            startParams.x = (startParams.x < 0) ? 0 : startParams.x;
            startParams.y = (startParams.y < 0) ? 0 : startParams.y;
            mStartSelectionHandle.setLayoutParams(startParams);

            MyAbsoluteLayout.LayoutParams endParams = (MyAbsoluteLayout.LayoutParams) mEndSelectionHandle
                    .getLayoutParams();
            endParams.x = (int) mSelectionBounds.right;
            endParams.y = (int) mSelectionBounds.bottom;

            // Stay on screen
            endParams.x = (endParams.x < 0) ? 0 : endParams.x;
            endParams.y = (endParams.y < 0) ? 0 : endParams.y;
            mEndSelectionHandle.setLayoutParams(endParams);

            dragListener.onDrag();
            dragListener.onDragEnd();
        }
    };

    /**
     * Checks to see if this view is in selection mode.
     *
     * @return
     */
    public boolean isInSelectionMode() {

        return mSelectionDragLayer.getParent() != null;

    }

    /**
     * Checks to see if the view is currently dragging.
     *
     * @return
     */
    public boolean isDragging() {
        return mDragging;
    }

    // *****************************************************
    // *
    // * DragListener Methods
    // *
    // *****************************************************

    /**
     * Start dragging a view.
     */
    private boolean startDrag(View v) {
        // Let the DragController initiate a drag-drop sequence.
        // I use the dragInfo to pass along the object being dragged.
        // I'm not sure how the Launcher designers do this.

        mDragging = true;
        Object dragInfo = v;
        mDragController.startDrag(v, mSelectionDragLayer, dragInfo,
                DragController.DRAG_ACTION_MOVE);
        return true;
    }

    /**
     * Returns the density dependent value of the given float 返回的是dp
     *
     * @param val
     * @param ctx
     * @return
     */
    public float getDensityDependentValue(float val, Context ctx) {

        // Get display from context
        Display display = ((WindowManager) ctx
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        // Calculate min bound based on metrics
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        return val * (metrics.densityDpi / 160f);

        // return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, val,
        // metrics);

    }

    /**
     * Returns the density independent value of the given float 返回的是像素
     *
     * @param val
     * @param ctx
     * @return
     */
    public float getDensityIndependentValue(float val, Context ctx) {

        // Get display from context
        Display display = ((WindowManager) ctx
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        // Calculate min bound based on metrics
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        return val / (metrics.densityDpi / 160f);

        // return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, val,
        // metrics);

    }

}
