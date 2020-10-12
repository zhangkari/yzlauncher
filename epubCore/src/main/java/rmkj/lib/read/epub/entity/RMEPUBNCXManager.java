package rmkj.lib.read.epub.entity;


/**
 * 映射 NCX文件
 * 
 * @author zsx
 * 
 */
public class RMEPUBNCXManager {
	private RMEPUBNCXHead head;
	private RMEPUBNCXDocTitle docTitle;
	private RMEPUBNCXNavMap nav;

	public RMEPUBNCXHead getHead() {
		return head;
	}

	public void setHead(RMEPUBNCXHead head) {
		this.head = head;
	}

	public RMEPUBNCXDocTitle getDocTitle() {
		return docTitle;
	}

	public void setDocTitle(RMEPUBNCXDocTitle docTitle) {
		this.docTitle = docTitle;
	}

	public RMEPUBNCXNavMap getNav() {
		return nav;
	}

	public void setNav(RMEPUBNCXNavMap nav) {
		this.nav = nav;
	}

	/**
	 * @param spinePath
	 *            相对路径
	 */
	public String getSpineName(String spinePath) {
		if (nav == null) {
			return null;
		}
		return nav.getSpineName(spinePath);
	}
}
