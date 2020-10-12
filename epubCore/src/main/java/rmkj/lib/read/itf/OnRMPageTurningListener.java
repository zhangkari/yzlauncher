package rmkj.lib.read.itf;

public interface OnRMPageTurningListener {
	/**
	 * 当没有上一页被调用
	 */
	void onPageNonePrePage();

	/**
	 * 当没有下一页被调用
	 */
	void onPageNoneNextPage();
}
