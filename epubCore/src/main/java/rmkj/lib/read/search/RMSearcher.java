package rmkj.lib.read.search;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import rmkj.lib.read.RMReadController;
import rmkj.lib.read.epub.entity.RMEPUBResourceProvider;
import rmkj.lib.read.itf.IRMObejctInterface;
import rmkj.lib.rzp.exception.RZPException;
import android.os.Handler;
import android.util.Log;

public class RMSearcher {
	private Handler mHandler = new Handler();
	private IRMObejctInterface data = RMReadController.GLOBAL_DATA.object;
	private SearchThread mThread;

	public interface OnRMSearchListener {
		void onSearchItemComplete(int searchIndex, String name, List<RMSearchSesultItem> currentList);
	}

	private class SearchThread extends Thread {
		private List<String> mSearchFiles = null;
		private OnRMSearchListener listener;
		private String mKeyword;
		private boolean isCancel = false;
		private RMEPUBResourceProvider provider;

		public void cancel() {
			this.isCancel = true;
		}

		public SearchThread(String keyword, List<String> mSearchFiles, OnRMSearchListener listener) {
			this.mKeyword = keyword;
			this.mSearchFiles = mSearchFiles;
			this.listener = listener;
		}

		@Override
		public void run() {
			for (int i = 0; i < mSearchFiles.size(); i++) {
				if (isCancel) {
					return;
				}
				final int index = i;
				List<RMSearchSesultItem> templist = null;
				switch (RMReadController.GLOBAL_DATA.FILE_TYPE) {
				case EPUB_RZP:
				case EPUB_ZIP:
					if (provider == null) {
						provider = new RMEPUBResourceProvider();
					}
					InputStream in = null;
					try {
						in = provider.getSpineContent(mSearchFiles.get(i));
						templist = doSearchOneFile(mKeyword, in, index);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (RZPException e) {
						e.printStackTrace();
					}
					break;
				case EPUB:
				case TXT:
					templist = doSearchOneFile(mKeyword, mSearchFiles.get(i), index);
					break;
				}
				final List<RMSearchSesultItem> list = templist;
				if (isCancel) {
					return;
				}
				mHandler.post(new Runnable() {

					@Override
					public void run() {
						String name = "";
						if (data != null) {
							name = data.getSpineName(index);
						}
						listener.onSearchItemComplete(index, name, list);
					}
				});
			}
			mThread = null;
		}
	}

	public boolean isStarting() {
		if (mThread != null) {
			return true;
		}
		return false;
	}

	public void cancelSearch() {
		if (mThread != null) {
			mThread.cancel();
		}
	}

	public void searchFile(String keyword, OnRMSearchListener listener) {
		IRMObejctInterface data = RMReadController.GLOBAL_DATA.object;
		if (data == null) {
			throw new RuntimeException("阅读数据被系统回收");
		}
		List<String> files = new ArrayList<String>();
		for (int i = 0; i < data.getTotalSpine(); i++) {
			String path = data.getSpineFile(i);
			files.add(path);
		}
		searchFile(files, keyword, listener);
	}

	public void searchFile(List<String> files, String keyword, OnRMSearchListener listener) {
		if (mThread != null) {
			return;
		}
		if (keyword == null) {
			Log.e("RMHtmlSearcher", "keyword is null");
			return;
		}
		mThread = new SearchThread(keyword, files, listener);
		mThread.start();
	}

	public void searchFile(String filePath, String keyword, OnRMSearchListener listener) {
		List<String> files = new ArrayList<String>();
		files.add(filePath);
		searchFile(files, keyword, listener);
	}

	private List<RMSearchSesultItem> doSearchOneFile(String key, String filePath, int spineIndex) {
		if (key == null) {
			Log.e("RMHtmlSearcher", "keyword is null");
			return null;
		}
		if (filePath == null || filePath.length() <= 0) {
			Log.e("RMHtmlSearcher", "filePath is null or is invalid");
			return null;
		}
		// 开始搜索
		return RMHtmlSearchUtil.searchOneFile(key, filePath, spineIndex);
	}

	private List<RMSearchSesultItem> doSearchOneFile(String key, InputStream in, int spineIndex) {
		if (key == null) {
			Log.e("RMHtmlSearcher", "keyword is null");
			return null;
		}
		if (in == null) {
			Log.e("RMHtmlSearcher", "InputStream is null or");
			return null;
		}
		// 开始搜索
		return RMHtmlSearchUtil.searchOneFile(key, in, spineIndex);
	}
}
