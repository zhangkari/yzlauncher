package rmkj.lib.read.itf;

import rmkj.lib.read.js.entity.HighlightSelectionText;

/**
 * WebView 点击空白监听
 * 
 * @author zsx
 * 
 */
public interface OnRMEPUBJSOnClickListener {
	/**
	 * @param x
	 * @param y
	 */
	void onNoneClick(float x, float y);

	/**
	 * @param spanID
	 */
	void onSelectTextClick(String spanID, float x, float y);

	/**
	 * @param src
	 */
//	void onImageClick(String src);

	/**
	 * @param href
	 */
	void onHrefClick(String href);

	void onEditNoteClick(String noteID, float x, float y);


	void onClickAnnotation(String annotaion);

	/**
	 * 获取高亮文字
	 * @param text
	 */
	void onGotHighlightText(HighlightSelectionText text);
}
