package rmkj.lib.read.webview;

import android.graphics.Bitmap;
import android.os.Message;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions.Callback;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebStorage.QuotaUpdater;
import android.webkit.WebView;

import rmkj.lib.read.util.LogUtil;

/**
 * webview WebChromeClient
 * 
 * @author zsx
 * 
 */
public class PRMWebChromeClient extends WebChromeClient {
	@Override
	public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
		if (LogUtil.DEBUG) {
			LogUtil.e(this, "onJsAlert:" + message);
			return super.onJsAlert(view, url, message, result);
		}
		/** 如果不是Debug模式.不弹出JS错误对话框 */
		return true;
	}

	@Override
	public void onCloseWindow(WebView window) {
		super.onCloseWindow(window);
		if (LogUtil.DEBUG) {
			LogUtil.e(this, "onCloseWindow");
		}
	}

	@Override
	public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
		if (LogUtil.DEBUG) {
			LogUtil.e(this, "onConsoleMessage:");
		}
		return super.onConsoleMessage(consoleMessage);
	}

	@Override
	public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
		if (LogUtil.DEBUG) {
			LogUtil.e(this, "onCreateWindow:");
		}
		return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
	}

	@Override
	public void onExceededDatabaseQuota(String url, String databaseIdentifier, long quota, long estimatedDatabaseSize, long totalQuota,
			QuotaUpdater quotaUpdater) {
		if (LogUtil.DEBUG) {
			LogUtil.e(this, "onExceededDatabaseQuota:");
		}
		super.onExceededDatabaseQuota(url, databaseIdentifier, quota, estimatedDatabaseSize, totalQuota, quotaUpdater);
	}

	@Override
	public void onGeolocationPermissionsHidePrompt() {
		if (LogUtil.DEBUG) {
			LogUtil.e(this, "onGeolocationPermissionsHidePrompt:");
		}
		super.onGeolocationPermissionsHidePrompt();
	}

	@Override
	public void onGeolocationPermissionsShowPrompt(String origin, Callback callback) {
		if (LogUtil.DEBUG) {
			LogUtil.e(this, "onGeolocationPermissionsShowPrompt:");
		}
		super.onGeolocationPermissionsShowPrompt(origin, callback);
	}

	@Override
	public void onHideCustomView() {
		if (LogUtil.DEBUG) {
			LogUtil.e(this, "onHideCustomView:");
		}
		super.onHideCustomView();
	}

	@Override
	public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
		if (LogUtil.DEBUG) {
			LogUtil.e(this, "onJsBeforeUnload:");
		}
		return super.onJsBeforeUnload(view, url, message, result);
	}

	@Override
	public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
		if (LogUtil.DEBUG) {
			LogUtil.e(this, "onJsConfirm:");
		}
		return super.onJsConfirm(view, url, message, result);
	}

	@Override
	public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
		if (LogUtil.DEBUG) {
			LogUtil.e(this, "onJsPrompt:");
		}
		return super.onJsPrompt(view, url, message, defaultValue, result);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onJsTimeout() {
		if (LogUtil.DEBUG) {
			LogUtil.e(this, "onJsTimeout:");
		}
		return super.onJsTimeout();
	}

	@Override
	public void onProgressChanged(WebView view, int newProgress) {
		if (LogUtil.DEBUG) {
			LogUtil.e(this, "onProgressChanged:" + newProgress);
		}
		super.onProgressChanged(view, newProgress);
	}

	@Override
	public void onReachedMaxAppCacheSize(long requiredStorage, long quota, QuotaUpdater quotaUpdater) {
		if (LogUtil.DEBUG) {
			LogUtil.e(this, "onReachedMaxAppCacheSize:");
		}
		super.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater);
	}

	@Override
	public void onReceivedIcon(WebView view, Bitmap icon) {
		if (LogUtil.DEBUG) {
			LogUtil.e(this, "onReceivedIcon:");
		}
		super.onReceivedIcon(view, icon);
	}

	@Override
	public void onReceivedTitle(WebView view, String title) {
		if (LogUtil.DEBUG) {
			LogUtil.e(this, "onReceivedTitle:" + title);
		}
//		super.onReceivedTitle(view, title);
	}

	@Override
	public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
		if (LogUtil.DEBUG) {
			LogUtil.e(this, "onReceivedTouchIconUrl:" + url + ":" + String.valueOf(precomposed));
		}
		super.onReceivedTouchIconUrl(view, url, precomposed);
	}

	@Override
	public void onRequestFocus(WebView view) {
		if (LogUtil.DEBUG) {
			LogUtil.e(this, "onRequestFocus:");
		}
		super.onRequestFocus(view);
	}

	@Override
	public void onShowCustomView(View view, CustomViewCallback callback) {
		if (LogUtil.DEBUG) {
			LogUtil.e(this, "onShowCustomView:");
		}
		super.onShowCustomView(view, callback);
	}
}
