/**
 * @class RMEPUBSearchController.java
 * @date 2014-3-12
 * @copyright 版权(C)重庆软媒科技有限公司 2013-2014
 * @author vken
 */
package rmkj.lib.read.epub.search;

import rmkj.lib.read.epub.search.RMSpineSearchResult.RMEPUBSearchResultItem;
import rmkj.lib.read.view.RMEPUBView;

public class RMSpineSearchController {

	public interface RMSpineSearchControllerEventListener {
		public void onNoPrevItem(RMSpineSearchController sender);

		public void onNoNextItem(RMSpineSearchController sender);
	}

	private RMEPUBView epubView = null;
	private RMSpineSearchResult searchResult = null;
	private RMSpineSearchControllerEventListener eventListener;

	private String keyword = null;
	private int searchIndex = 0;

	public RMSpineSearchController(RMEPUBView view, RMSpineSearchControllerEventListener listener) {
		epubView = view;
		this.eventListener = listener;
	}

	public RMSpineSearchResult getSearchResult() {
		return searchResult;
	}

	public void setSearchResult(RMSpineSearchResult searchResult) {
		searchIndex = 0;
		this.searchResult = searchResult;
	}

	public int getSearchItemPage(RMEPUBSearchResultItem item) {
		if (item == null) {
			return 0;
		}
		return item.getPage();
	}

	public final static int ITEM_INDEX_LAST = -1;
	public final static int ITEM_INDEX_FIRST = 0;

	public void showItem(int itemIndex) {
		switch (itemIndex) {
		case ITEM_INDEX_LAST:
			searchIndex = searchResult.getCount() - 1;
			break;
		case ITEM_INDEX_FIRST:
			searchIndex = ITEM_INDEX_FIRST;
			break;
		default:
			searchIndex = itemIndex;
			break;
		}
		RMEPUBSearchResultItem item = searchResult.getItem(searchIndex);
		int page = getSearchItemPage(item);
		epubView.showPage(page);
	}

	public void showPrevItem() {
		if (searchIndex - 1 >= 0) {
			RMEPUBSearchResultItem item = searchResult.getItem(searchIndex - 1);
			if (item == null)
				return;
			searchIndex = searchIndex - 1;
			int page = getSearchItemPage(item);
			epubView.showPage(page);
		} else {
			if (eventListener != null) {
				eventListener.onNoPrevItem(this);
			}
		}
	}

	public void showNextItem() {
		if (searchIndex + 1 < searchResult.getCount()) {
			RMEPUBSearchResultItem item = searchResult.getItem(searchIndex + 1);
			if (item == null)
				return;
			searchIndex = searchIndex + 1;
			int page = getSearchItemPage(item);
			epubView.showPage(page);
		} else {
			if (eventListener != null) {
				eventListener.onNoNextItem(this);
			}
		}
	}
	
	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

}
