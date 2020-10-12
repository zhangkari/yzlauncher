/**
 * @class RMEPUBSearchItem.java
 * @date 2014-3-12
 * @copyright 版权(C)重庆软媒科技有限公司 2013-2014
 * @author vken
 */
package rmkj.lib.read.epub.search;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import rmkj.lib.read.util.LogUtil;

public class RMSpineSearchResult {

	public class RMEPUBSearchResultItem {
//		private String key;
//		private Rect rect = new Rect();
		// private int containerIndex;
		// private int startIndex;
		// private int endIndex;
		private int page;

		public int getPage() {
			return page;
		}

		public RMEPUBSearchResultItem() {
			/*
			 * {"containerIndex":13,"startIndex":9,"endIndex":11,"value":"彼得"}
			 * 'left': rect.left + scrollLeft, 'top': rect.top + scrollTop,
			 * 'width': rect.width, 'height': rect.height
			 */
			// containerIndex = valueJson.getInt("containerIndex");
			// startIndex = valueJson.getInt("startIndex");
			// endIndex = valueJson.getInt("endIndex");

		}

//		public String getKey() {
//			return key;
//		}
//
//		public void setKey(String key) {
//			this.key = key;
//		}

//		public Rect getRect() {
//			return rect;
//		}
//
//		public void setRect(Rect rect) {
//			this.rect = rect;
//		}

		// public int getContainerIndex() {
		// return containerIndex;
		// }
		//
		// public void setContainerIndex(int containerIndex) {
		// this.containerIndex = containerIndex;
		// }
		//
		// public int getStartIndex() {
		// return startIndex;
		// }
		//
		// public void setStartIndex(int startIndex) {
		// this.startIndex = startIndex;
		// }
		//
		// public int getEndIndex() {
		// return endIndex;
		// }
		//
		// public void setEndIndex(int endIndex) {
		// this.endIndex = endIndex;
		// }
	}

	private List<RMEPUBSearchResultItem> searchArray = null;

	public RMSpineSearchResult(JSONObject json) throws JSONException {
		if (json == null)
			return;
		if (searchArray == null)
			searchArray = new ArrayList<RMEPUBSearchResultItem>();
		// int resultCount = json.getInt("count");
		JSONArray itemsArray = json.getJSONArray("searchResult");
		for (int i = 0; i < itemsArray.length(); i++) {
			RMEPUBSearchResultItem item = new RMEPUBSearchResultItem();
			JSONObject itemObject = itemsArray.getJSONObject(i);
			try{
				item.page = itemObject.getInt("page");
			}catch(Exception e){
				item.page=0;
			}
			//LogUtil.e(RMSpineSearchResult.class, "")
//			JSONObject rect = itemObject.getJSONObject("rect");
//			item.rect.left = rect.getInt("x");
//			item.rect.bottom = rect.getInt("y");
//			item.rect.right = rect.getInt("w");
//			item.rect.top = rect.getInt("h");
			searchArray.add(item);
		}
	}

	public RMEPUBSearchResultItem getItem(int i) {
		if (searchArray == null)
			return null;
		if (searchArray.size() > i && i >= 0) {
			return searchArray.get(i);
		} else
			return null;
	}

	public int getCount() {
		if (searchArray == null)
			return 0;
		return searchArray.size();
	}
}
