package rmkj.lib.read;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import rmkj.lib.exception.PRMException;
import rmkj.lib.read.db.RMReadingManager;
import rmkj.lib.read.db.RMReadingMark;
import rmkj.lib.read.db.RMReadingNote;
import rmkj.lib.read.epub.parser.PRMEPUBLoader;
import rmkj.lib.read.global.RMFileType;
import rmkj.lib.read.itf.IRMObejctInterface;
import rmkj.lib.read.itf.OnRMEPUBJSOnClickListener;
import rmkj.lib.read.itf.OnRMEPUBLoaderListener;
import rmkj.lib.read.itf.OnRMEPUBSelectionListener;
import rmkj.lib.read.itf.OnRMPageChangeListener;
import rmkj.lib.read.itf.OnRMSpineChangedListener;
import rmkj.lib.read.util.LogUtil;
import rmkj.lib.read.view.PRMLoadingView;
import rmkj.lib.read.view.RMEPUBView;

/**
 * @author zsx
 */
public class RMReadController {
    /**
     * 解析器
     */
    private PRMEPUBLoader loader;
    /**
     * 解析出的全部数据接口
     */
    private IRMObejctInterface epubObject;
    /**
     * 加载书籍监听
     */
    private OnRMEPUBLoaderListener listener;
    /**
     * 显示的View
     */
    private RMEPUBView epub;
    /**
     * 书籍数据库关键字 一般为书籍路径,数据库唯一值
     */
    private String book_key, userId;
    /**
     * 全局数据 loadEpub完成后 赋值
     */
    public static GlobalData GLOBAL_DATA = new GlobalData();

    public RMReadController(RMEPUBView epub, String book_key, String userId) {
        GLOBAL_DATA.object = null;
        GLOBAL_DATA.dbManager = null;
        GLOBAL_DATA.BOOK_INFO = null;
        GLOBAL_DATA.BOOK_PATH = null;

        GLOBAL_DATA.FILE_TYPE = null;
        this.book_key = book_key;
        this.userId = userId;
        this.epub = epub;
        epub.setEnabled(false);
    }

    /**
     * Activity 结束时调用
     */
    public void onDestroy() {
        if (epub != null) {
            epub.onDestory();
            epub = null;
        }
        GLOBAL_DATA.object = null;
        GLOBAL_DATA.dbManager = null;
        GLOBAL_DATA.BOOK_INFO = null;
        GLOBAL_DATA.BOOK_PATH = null;

        GLOBAL_DATA.PASS_WORDS = null;

        GLOBAL_DATA.FILE_TYPE = null;
        System.gc();
    }

    /**
     * 返回数据库
     */
    public RMReadingManager getDBManager() {
        return epub.getDBManager();
    }

    /**
     * 刷新View
     */
    public void refresh() {
        epub.refresh();
    }

    /**
     * 拿到当前页面的书签
     */
    public RMReadingMark getCurrentPageMark() {
        return epub.getCurrentPageMark();
    }

    /**
     * 添加或者删除书签
     */
    public boolean toggleCurrentPageMark(String content) {
        return epub.toggleCurrentPageMark(content);
    }

    /**
     * 跳转到某章节某页
     *
     * @param spineIndex  章节 0 开始
     * @param pageInSpine 页码 0 开始
     */
    public void showSpine(int spineIndex, int pageInSpine, int totalInSpine) {
        epub.showSpine(spineIndex, pageInSpine, totalInSpine);
    }

    public float getPageInSpinePercent() {
        return epub.getPageInSpinePercent();
    }

    public float getTotalPercent() {
        return epub.getTotalPercent();
    }

    public String getCurrentSpineName() {
        return epub.getCurrentSpineName();
    }

    /**
     * 添加笔记
     */
    public boolean addNote(RMReadingNote note) {
        return epub.addNote(note);
    }

    /**
     * 更新笔记
     */
    public boolean updateNote(String noteID, String noteText) {
        return epub.updateNote(noteID, noteText);
    }

    /**
     * 删除笔记
     */
    public boolean deleteNote(String spanID) {
        return epub.deleteNote(spanID);
    }

    /**
     * @param epubPath    epub存储路径
     * @param unZipFolder 解压路径
     * @param listener
     */
    public void loadEpub(String epubPath, String unZipFolder,
                         OnRMEPUBLoaderListener listener) {
        if (loader == null) {
            loader = new PRMEPUBLoader();
            loader.setFilePath(epubPath);
            loader.setUnZipPath(unZipFolder);
            this.listener = listener;
            new LoadRunable(loader, RMFileType.EPUB).start();
        }
    }

    public void loadEpubRzp(String epubPath, OnRMEPUBLoaderListener listener) {
        if (loader == null) {
            loader = new PRMEPUBLoader();
            loader.setFilePath(epubPath);
            this.listener = listener;
            new LoadRunable(loader, RMFileType.EPUB_RZP).start();
        }
    }

    public void loadEpubZip(String epubPath, OnRMEPUBLoaderListener listener) {
        if (loader == null) {
            loader = new PRMEPUBLoader();
            loader.setFilePath(epubPath);
            this.listener = listener;
            new LoadRunable(loader, RMFileType.EPUB_ZIP).start();
        }
    }

    /**
     * @param txtPath     txt文件路径
     * @param unZipFolder txt文件拆分路径
     * @param listener
     */
    public void loadTxt(String txtPath, String unZipFolder,
                        OnRMEPUBLoaderListener listener) {
        if (loader == null) {
            loader = new PRMEPUBLoader();
            loader.setFilePath(txtPath);
            loader.setUnZipPath(unZipFolder);
            this.listener = listener;
            new LoadRunable(loader, RMFileType.TXT).start();
        }
    }

    public RMEPUBView getEpub() {
        return epub;
    }

    public IRMObejctInterface getEpubObject() {
        return epubObject;
    }

    /**
     * 设置点击WebView(webview 没有相应响应的点击事件)
     */
    public void setOnJSClick(OnRMEPUBJSOnClickListener noneClickListener) {
        epub.setOnJSClick(noneClickListener);
    }

    /**
     * 页码发生改变监听
     */
    public void setOnPageChaneListener(OnRMPageChangeListener pageChangeListener) {
        epub.setOnPageChaneListener(pageChangeListener);
    }

    /**
     * 章节发生改变监听
     */
    public void setOnSpineChangedListener(
            OnRMSpineChangedListener spineChangedListener) {
        epub.setOnSpineChangedListener(spineChangedListener);
    }

    /**
     * 选中区域变化监听
     *
     * @param listener
     */
    public void setOnSelectionListener(OnRMEPUBSelectionListener listener) {
        epub.setSelectionListener(listener);
    }

    /**
     * 当前章节
     */
    public int getCurrentSpine() {
        return epub.getCurrentSpineIndex();
    }

    /**
     * 当前章节所在页码
     */
    public int getCurrentPageInSpine() {
        return epub.getCurrentPage();
    }

    /**
     * 当前章节总页码
     */
    public int getTotalPageInSpine() {
        return epub.getTotalPage();
    }

    /**
     * 当前章节名
     */
    public String getSpineName() {
        return epub.getCurrentSpineName();
    }

    /**
     * 拿到封面
     */
    public String getCover() {
        return epubObject.getCover();
    }

    /**
     * 拿到作者
     */
    public String getAuthor() {
        return epubObject.getAuthor();
    }

    /**
     * 拿到书名
     */
    public String getBookName() {
        return epubObject.getBookName();
    }

    /**
     * @param anchorPath 相对路径#锚点
     */
    public void showSpine(String anchorPath) {
        epub.showSpine(anchorPath);
    }

    public void showSpine(int spineIndex) {
        epub.showSpine(spineIndex);
    }

    public static class GlobalData {
        private GlobalData() {
        }

        public Object BOOK_INFO;
        public String BOOK_PATH;

        public String PASS_WORDS;

        public RMFileType FILE_TYPE;
        public RMReadingManager dbManager;
        public IRMObejctInterface object;
    }

    /**************************
     * 私有方法
     ***********************************/
    private Handler mHandler = new Handler();

    private void loadStart() {

        mHandler.post(new Runnable() {

            @Override
            public void run() {
                showLoading();
                // listener.onLoadStart();
            }
        });
    }

    private void loadComplete(IRMObejctInterface o, RMFileType type, String path) {
        epubObject = (IRMObejctInterface) o;
        GLOBAL_DATA.object = epubObject;
        GLOBAL_DATA.BOOK_PATH = path;
        GLOBAL_DATA.dbManager = new RMReadingManager(epub.getContext(),
                book_key, userId);
        GLOBAL_DATA.FILE_TYPE = type;
        try {
            epub.init(epubObject, book_key, path);
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    epub.setEnabled(true);
                    cancelLoading();
                    listener.onLoadBookComplete();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            loadError("文件未找到");
        }
    }

    private void loadError(final String message) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                cancelLoading();
                new AlertDialog.Builder(epub.getContext())
                        .setTitle("Sorry")
                        .setIcon(android.R.drawable.ic_delete)
                        .setMessage(message)
                        .setNegativeButton(
                                "确定",
                                new android.content.DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        Activity readActivity = (Activity) epub
                                                .getContext();
                                        readActivity.finish();
                                    }
                                }).setCancelable(false).create().show();
                // TODO 读取EPUB错误事件未传出. 因为发生错误一般不会再阅读,内部消化
                // listener.onLoadError(message);
            }
        });
    }

    private PRMLoadingView loadingView;

    private void showLoading() {
        if (loadingView == null) {
            loadingView = new PRMLoadingView(epub.getContext());
        }
        loadingView.show();
    }

    private void cancelLoading() {
        if (loadingView != null) {
            loadingView.cancel();
        }
    }

    private class LoadRunable extends Thread {
        private PRMEPUBLoader loader;
        private RMFileType type;

        public LoadRunable(PRMEPUBLoader loader, RMFileType type) {
            this.loader = loader;
            this.type = type;
        }

        @Override
        public void run() {
            try {
                loadStart();
                IRMObejctInterface o = null;

                LogUtil.e("yang", "run  type ----" + type);

                switch (type) {
                    case EPUB:
                        o = PRMEPUBLoader.loadEpub(loader.getFilePath(),
                                loader.getUnZipPath());
                        break;
                    case TXT:
                        o = PRMEPUBLoader.loadTXT(loader.getFilePath(),
                                loader.getUnZipPath());
                        break;
                    case EPUB_ZIP:
                        o = PRMEPUBLoader.loadEpubZip(loader.getFilePath());
                        break;
                    case EPUB_RZP:
                      //  LogUtil.e("yang", "EPUB_RZP--开始");
                        o = PRMEPUBLoader.loadEpubRzp(loader.getFilePath());
                      //  LogUtil.e("yang", "EPUB_RZP--结束");
                        break;
                }
                loadComplete(o, type, loader.getFilePath());
                loader = null;
            } catch (PRMException e) {
                loadError(e.getMessage() + " ");
                LogUtil.e(e.getMessage(), e.getMessageInfo());
                e.printStackTrace();
            } catch (XmlPullParserException e) {

                //LogUtil.e("yang", "解析错误--" + e.getMessage() + "toString --" + e.toString() + "e.getCause() - " + e.getCause());
                loadError("解析错误");
                e.printStackTrace();
            } catch (IOException e) {
                loadError("发生未知错误");
                e.printStackTrace();
            } catch (Exception e) {
                loadError("发生未知错误");
                e.printStackTrace();
            }
        }
    }
}