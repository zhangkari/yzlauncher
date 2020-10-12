package rmkj.lib.read.itf;

import rmkj.lib.read.epub.entity.RMEPUBOPFManifestItem;

public interface IRMObejctInterface {

	/**
	 * 检查是否有spine
	 */
	public boolean hasSpine(int spineIndex);

	/**
	 * 拿到目录名
	 */
	public String getSpineName(int spineIndex);

	public String getOpfFolder();

	/**
	 * 拿到封面
	 */
	public String getCover();

	/**
	 * 拿到书名
	 */
	public String getBookName();

	/**
	 * 拿到作者
	 */
	public String getAuthor();

	/**
	 * 拿到Spine
	 */
	public RMEPUBOPFManifestItem getSpineItem(int spineIndex);

	public String getMediaType(String href);

	/**
	 * 拿到对应章节位置
	 */
	public int getSpineIndex(String spineRelativePath);

	/**
	 * 拿到总章节
	 */
	public int getTotalSpine();

	/**
	 * 返回spine所在路径
	 */
	public String getSpineFile(int spineIndex);

	/**
	 * 返回spine文件 mimeType
	 * 
	 */
	public String getSpineMimeType(int spineIndex);
	
	/** 
	 * 获取spine的编码方式
	 * @param spineIndex
	 * @return
	 */
	public String getSpineEncode(int spineIndex);

	/**
	 * 是否是竖排
	 */
	public boolean isVerticalOrientation(int spineIndex);

	/**
	 * 是否是右翻页
	 */
	public boolean isRightPageOrientation();

}