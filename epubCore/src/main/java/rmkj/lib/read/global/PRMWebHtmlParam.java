package rmkj.lib.read.global;

public class PRMWebHtmlParam {
	public int width;
	public int height;
	public int margin;
	public boolean isVertical;
	/**
	 * 从章节最后一页开始显示
	 */
	public boolean loadFromEnd;
	public static int END_PAGE = -1;

	/**
	 * 从章节内指定百分比显示
	 */
	public boolean loadFromPercent;
	public float loadPercent;

	/**
	 * 从章节指定页码显示
	 */
	public boolean loadFromPage;
	public int loadPage;

	/**
	 * 从章节内指定锚点显示
	 */
	public boolean loadFromAnchor;
	public String loadAnchor;
	/**
	 * 从章节总页数 和页数
	 */
	public boolean loadFormPageAndTotalPage;
	public int loadTotalPage;

	/**
	 * 搜索模式
	 */
	public boolean isSearchMode;
	public String searchKey;

	public void resetLoad() {
		loadFromEnd = false;
		loadFromPage = false;
		loadFromPercent = false;
		loadFromAnchor = false;
		loadFormPageAndTotalPage = false;
		loadPage = 0;
		loadTotalPage = 0;
		loadAnchor = null;
		isSearchMode = false;
		searchKey = null;
		loadPercent = Float.NaN;
	}

	public void resetAll() {
		resetLoad();
		isSearchMode = false;
		searchKey = null;
	}
}
