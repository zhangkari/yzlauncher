package rmkj.lib.read.js;

import android.app.Activity;
import android.webkit.JavascriptInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import rmkj.lib.read.epub.search.RMSpineSearchResult;
import rmkj.lib.read.util.LogUtil;

/**
 * JS回调类
 *
 * @author zsx
 */
public class PRMJSToAndroidInterface {
    private static final String FUNCTION_LOAD_COMPLETE = "loadComplete";
    private static final String FUNCTION_PAGE_CHANGED = "pageChanged";
    private static final String FUNCTION_CLICK_IMAGE = "clickImage";
    private static final String FUNCTION_CLICK_HREF = "clickHref";
    private static final String FUNCTION_CLICK_NOTE = "clickNote";
    private static final String FUNCTION_CLICK_BLANK = "clickBlank";
    private static final String FUNCTION_SELECTION_TEXT = "getSelection";
    private static final String FUNCTION_HIGHT_SELECTION_TEXT = "getHighlightSelectionText";
    private static final String FUNCTION_EDIT_NOTE = "editNote";
    private static final String FUNCTION_SEARCH = "searchKey";
    private static final String FUNCTION_GOTO_ANCHOR = "gotoAnchor";
    private static final String FUNCTION_GET_MARK = "getBookMarkDesc";

    // 点击注释
    private static final String FUNCTION_CLICK_ANNOTATION = "clickAnnotation";
    private JsInterfaceListener listener;
    private Activity context;

    public PRMJSToAndroidInterface(Activity context,
                                   JsInterfaceListener listener) {
        this.listener = listener;
        this.context = context;
    }

    public void destroy() {
        this.listener = null;
        this.context = null;
    }

    /**
     * JS 回调函数
     *
     * @param jsonStr
     */
    @JavascriptInterface
    public void onJSCall(final String jsonStr) {
        if (LogUtil.DEBUG) {
            LogUtil.e(this, jsonStr);
        }
        context.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                handleJS(jsonStr);
            }
        });
    }

    private synchronized void handleJS(String jsonStr) {
        JSONObject json;
        try {
            json = new JSONObject(jsonStr);
            String function = json.getString("function");
            JSONObject params = json.getJSONObject("parameters");
            if (FUNCTION_LOAD_COMPLETE.equals(function)) {
                // int scrollWidth = params.getInt("scrollWidth");
                int scrollWidth = 0;
                int viewWidth = params.getInt("viewWidth");
                int totalPage = 1;
                int currentPage = 0;
                try {
                    totalPage = params.getInt("totalPage");
                } catch (Exception e) {
                }
                try {
                    currentPage = params.getInt("currentPage");
                } catch (Exception e) {
                }
                listener.onJSLoadComplete(currentPage, totalPage, scrollWidth,
                        viewWidth);
            } else if (FUNCTION_PAGE_CHANGED.equals(function)) {
                int currentPage = params.getInt("currentPage");
                int totalPage = params.getInt("totalPage");
                listener.onJSPageChanged(currentPage, totalPage);
            } /*else if (FUNCTION_CLICK_IMAGE.equals(function)) {
                String src = params.getString("src");
                listener.onJSImageClicked(src);
            } */ else if (FUNCTION_CLICK_HREF.equals(function)) {
                String href = params.getString("href");
                listener.onJSHrefClicked(href);
            } else if (FUNCTION_CLICK_NOTE.equals(function)) {
                String noteId = params.getString("id");
                listener.onJSNoteClicked(noteId);
            } else if (FUNCTION_CLICK_BLANK.equals(function)) {
                float x = (float) params.getDouble("x");
                float y = (float) params.getDouble("y");
                listener.onJSBlankClicked(x, y);
            } else if (FUNCTION_SELECTION_TEXT.equals(function)) {
                String selectText = params.getString("selectionText");
                listener.onjsSelectionText(selectText);
            } else if (FUNCTION_HIGHT_SELECTION_TEXT.equals(function)) {
                JSONArray selectionReplaceArray = params
                        .getJSONArray("selectionReplaceArray");
                String selectionText = params.getString("selectionText");
                String spanID = params.getString("id");
                listener.onJSHighlightSelectText(selectionText,
                        selectionReplaceArray.toString(), spanID);
            } else if (FUNCTION_EDIT_NOTE.equals(function)) {
                String noteID = params.getString("id");
                listener.onJSEditNoteClicked(noteID);
            } else if (FUNCTION_SEARCH.equals(function)) {
                RMSpineSearchResult result = new RMSpineSearchResult(params);
                listener.onJSSearchText(result);
            } else if (FUNCTION_GOTO_ANCHOR.equals(function)) {
                int currentPage = params.getInt("currentPage");
                int totalPage = params.getInt("totalPage");
                listener.onJSPageChanged(currentPage, totalPage);
            } else if (FUNCTION_CLICK_ANNOTATION.equals(function)) {
                String annotation = params.getString("annotation");
                listener.onJsAnnotaionClicked(annotation);
            }
        } catch (JSONException e) {
            if (LogUtil.DEBUG) {
                LogUtil.e(this, "JSONException error:" + jsonStr);
            }
            e.printStackTrace();
        }
    }

    public interface JsInterfaceListener {
        void onJSLoadComplete(int currentPage, int totalPage, int scrollWidth,
                              int viewWidth);

        void onJSPageChanged(int currentPage, int totalPage);

//        void onJSImageClicked(String src);

        void onJSNoteClicked(String id);

        void onJSHrefClicked(String href);

        void onJSBlankClicked(float x, float y);

        void onjsSelectionText(String selectText);

        void onJSHighlightSelectText(String selectText,
                                     String selectionReplaceArray, String spanID);

        void onJSEditNoteClicked(String noteID);

        void onJSSearchText(RMSpineSearchResult result);

        void onJsAnnotaionClicked(String annotaion);
    }
}
