package rmkj.lib.read.webview;

import java.net.URLDecoder;

import rmkj.lib.read.RMReadController;
import rmkj.lib.read.epub.entity.RMEPUBResourceProvider;
import rmkj.lib.read.util.LogUtil;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Message;
import android.view.KeyEvent;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * webview WebViewClient
 * 
 * @author zsx
 * 
 */
public class PRMWebViewClient extends WebViewClient {
	private OnSpineListener listener;
	private RMEPUBResourceProvider provider;
	private String setRandomStr;

	public void setZipMode(RMEPUBResourceProvider provider) {
		this.provider = provider;
	}

	public void setRandomStr(String setRandomStr) {
		this.setRandomStr = setRandomStr;
	}

	public PRMWebViewClient(OnSpineListener listener) {
		this.listener = listener;
	}

	@SuppressLint("NewApi")
	@Override
	public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
		if (LogUtil.DEBUG) {
			LogUtil.e(this, "shouldInterceptRequest" + url);
		}
		switch (RMReadController.GLOBAL_DATA.FILE_TYPE) {
		case EPUB_ZIP:
		case EPUB_RZP:
			String filePath,sourcePath;
			WebResourceResponse res = null;
			String mediaType = null;
			try {
				filePath = URLDecoder.decode(url.replace("file:///", "")
						.replace(setRandomStr, ""), "utf-8");
				sourcePath = RMReadController.GLOBAL_DATA.object
						.getOpfFolder() + filePath;
				sourcePath = sourcePath.replace("//", "/");
				mediaType = RMReadController.GLOBAL_DATA.object
						.getMediaType(filePath);
				res = new WebResourceResponse(
						mediaType,
						"utf-8",
						provider.getSpineContent(sourcePath));
			} catch (Exception e) {
				e.printStackTrace();
				filePath = url.replace("file:///", "")
						.replace(setRandomStr, "");
				sourcePath = RMReadController.GLOBAL_DATA.object
						.getOpfFolder() + filePath;
				sourcePath = sourcePath.replace("//", "/");
				mediaType = RMReadController.GLOBAL_DATA.object
						.getMediaType(filePath);
				try {
					res = new WebResourceResponse(
							mediaType,
							"utf-8",
							provider.getSpineContent(sourcePath));
				} catch (Exception e1) {
					e1.printStackTrace();
					LogUtil.e(this, "shouldInterceptRequest() have error:"
							+ url);
					return super.shouldInterceptRequest(view, url);
				}
			}
			return res;
		case EPUB:
		case TXT:
			return super.shouldInterceptRequest(view, url);
		}
		return super.shouldInterceptRequest(view, url);
	}

	@Override
	public void onFormResubmission(WebView view, Message dontResend,
			Message resend) {
		if (LogUtil.DEBUG) {
			LogUtil.e(this, "onFormResubmission");
		}
		super.onFormResubmission(view, dontResend, resend);
	}

	@Override
	public void onLoadResource(WebView view, String url) {
		super.onLoadResource(view, url);
		if (LogUtil.DEBUG) {
			LogUtil.e(this, "onLoadResource" + url);
		}
	}

	@Override
	public void onPageFinished(WebView view, String url) {
		super.onPageFinished(view, url);
		if (listener != null) {
			listener.onSpineComplete();
		}
		if (LogUtil.DEBUG) {
			LogUtil.e(this, "onPageFinished");
		}
	}

	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
		super.onPageStarted(view, url, favicon);
		if (listener != null) {
			listener.onSpineStart();
		}
		if (LogUtil.DEBUG) {
			LogUtil.e(this, "onPageStarted");
		}
	}

	@Override
	public void onReceivedError(WebView view, int errorCode,
			String description, String failingUrl) {
		super.onReceivedError(view, errorCode, description, failingUrl);
		if (listener != null) {
			listener.onSpineError();
		}
		if (LogUtil.DEBUG) {
			LogUtil.e(this, "onReceivedError");
		}
	}

	@Override
	public void onReceivedHttpAuthRequest(WebView view,
			HttpAuthHandler handler, String host, String realm) {
		if (LogUtil.DEBUG) {
			LogUtil.e(this, "onReceivedHttpAuthRequest");
		}
		super.onReceivedHttpAuthRequest(view, handler, host, realm);
	}

	@SuppressLint("NewApi")
	@Override
	public void onReceivedLoginRequest(WebView view, String realm,
			String account, String args) {
		if (LogUtil.DEBUG) {
			LogUtil.e(this, "onReceivedLoginRequest");
		}
		super.onReceivedLoginRequest(view, realm, account, args);
	}

	@Override
	public void onReceivedSslError(WebView view, SslErrorHandler handler,
			SslError error) {
		if (LogUtil.DEBUG) {
			LogUtil.e(this, "onReceivedSslError");
		}
		super.onReceivedSslError(view, handler, error);
	}

	@Override
	public void onScaleChanged(WebView view, float oldScale, float newScale) {
		if (LogUtil.DEBUG) {
			LogUtil.e(this, "onScaleChanged");
		}
		super.onScaleChanged(view, oldScale, newScale);
	}

	@Override
	public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
		if (LogUtil.DEBUG) {
			LogUtil.e(this, "onUnhandledKeyEvent");
		}
		super.onUnhandledKeyEvent(view, event);
	}

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		if (LogUtil.DEBUG) {
			LogUtil.e(this, "shouldOverrideUrlLoading" + url);
		}
		
		return super.shouldOverrideUrlLoading(view, url);
	}

	public interface OnSpineListener {
		void onSpineStart();

		void onSpineComplete();

		void onSpineError();
	}
}